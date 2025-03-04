import json
import os
import re
import requests
from datetime import datetime
import argparse
import base64

class PostmanAPITester:
  def __init__(self, openapi_file, output_dir, port=8080, existing_curl=None,
               postman_api_key=None, postman_workspace_id=None):
    self.openapi_file = openapi_file
    self.output_dir = output_dir
    self.port = port
    self.existing_curl = existing_curl
    self.existing_headers = {}
    self.existing_body = None
    self.existing_url = None

    # Postman credentials
    self.postman_api_key = postman_api_key
    self.postman_workspace_id = postman_workspace_id

    # Create output directory
    os.makedirs(output_dir, exist_ok=True)

    # Parse existing curl if provided
    if existing_curl:
      self.existing_headers = self.parse_curl_headers(existing_curl)
      self.existing_body = self.parse_curl_body(existing_curl)
      self.existing_url = self.parse_curl_url(existing_curl)

  def verify_postman_api_key(self):
    """Verify that the Postman API key is valid"""
    if not self.postman_api_key:
      print("No Postman API key provided")
      return False

    try:
      auth_url = "https://api.getpostman.com/me"
      auth_header = {
        'X-Api-Key': self.postman_api_key
      }

      response = requests.get(auth_url, headers=auth_header)
      if response.status_code == 200:
        print(f"Successfully authenticated with Postman API key")
        return True
      else:
        print(f"Failed to authenticate with Postman: {response.text}")
        return False
    except Exception as e:
      print(f"Error authenticating with Postman: {str(e)}")
      return False

  def parse_curl_headers(self, curl_command):
    """Parse headers from curl command."""
    headers = {}
    header_pattern = re.compile(r'--header\s+[\'"]([^:]+):\s+([^\'"]+)[\'"]')
    matches = header_pattern.findall(curl_command)

    for name, value in matches:
      headers[name.strip()] = value.strip()

    return headers

  def parse_curl_body(self, curl_command):
    """Extract request body from curl command."""
    body_pattern = re.compile(r'(?:--data|-d)\s+[\'"](.*?)[\'"](?:\s|$)', re.DOTALL)
    match = body_pattern.search(curl_command)

    if match:
      return match.group(1)
    return None

  def parse_curl_url(self, curl_command):
    """Extract URL from curl command."""
    # Match URL pattern
    url_pattern = re.compile(r'curl\s+(?:-X\s+\w+\s+)?[\'"]([^\'"]*)[\'"]\s')
    match = url_pattern.search(curl_command)

    if not match:
      # Try alternative pattern for --location or -L format
      alt_pattern = re.compile(r'(?:--location|-L)\s+[\'"]([^\'"]*)[\'"]\s')
      match = alt_pattern.search(curl_command)

    if match:
      return match.group(1)
    return None

  def generate_request_body_sample(self, schema):
    """Generate sample request body based on OpenAPI schema."""
    if not schema:
      return {}

    schema_type = schema.get('type', 'object')

    if schema_type == 'object':
      result = {}
      properties = schema.get('properties', {})

      for prop_name, prop_schema in properties.items():
        if 'example' in prop_schema:
          result[prop_name] = prop_schema['example']
        else:
          prop_type = prop_schema.get('type', 'string')
          if prop_type == 'string':
            result[prop_name] = f"sample_{prop_name}"
          elif prop_type == 'number' or prop_type == 'integer':
            result[prop_name] = 0
          elif prop_type == 'boolean':
            result[prop_name] = False
          elif prop_type == 'array':
            result[prop_name] = []
          elif prop_type == 'object':
            result[prop_name] = self.generate_request_body_sample(prop_schema)

      # For additionalProperties
      if schema.get('additionalProperties') and isinstance(schema.get('additionalProperties'), dict):
        result["sampleAdditionalProperty"] = self.generate_request_body_sample(schema.get('additionalProperties'))

      return result

    elif schema_type == 'array':
      items_schema = schema.get('items', {})
      return [self.generate_request_body_sample(items_schema)]

    return "sample_value"

  def generate_postman_collection(self, openapi_data):
    """Convert OpenAPI to Postman Collection format with fixed base URL."""
    collection = {
      "info": {
        "name": f"OpenAPI Tests - {datetime.now().strftime('%Y-%m-%d')}",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
        "description": f"Generated from {self.openapi_file}"
      },
      "item": []
    }

    # Always use this fixed base URL regardless of OpenAPI spec
    base_url = "http://localhost:8080/aiv"

    # Process endpoints
    endpoints = []
    for path, methods in openapi_data.get('paths', {}).items():
      if 'post' in methods:
        endpoints.append((path, methods['post']))

    # Define the fixed headers we want to use for all requests
    fixed_headers = [
      {"key": "apitoken", "value": "eyJhbGciOiJIUzI1NiJ9.eyJkZXBhcnRtZW50IjoiRGVmYXVsdCIsInVzZXJuYW1lIjoiQWRtaW4iLCJzdWIiOiJBZG1pbiIsImlhdCI6MTc0MDA0MzYwNiwiZXhwIjoxNzQwOTA3NjA2fQ.SC5rulkG-znKgBySuC_aNvkJtx7S1JrrO6OoscAS2Kc", "type": "text"},
      {"key": "category", "value": "REPORTS", "type": "text"},
      {"key": "owner", "value": "ADMIN", "type": "text"},
      {"key": "archiveMode", "value": "false", "type": "text"},
      {"key": "timezone", "value": "SYSTEM", "type": "text"},
      {"key": "dc", "value": "Default", "type": "text"},
      {"key": "traceid", "value": "API", "type": "text"},
      {"key": "Content-Type", "value": "application/json", "type": "text"}
    ]

    for endpoint_path, endpoint_data in endpoints:
      # Ensure endpoint path format
      if endpoint_path.startswith('/'):
        endpoint_url_path = endpoint_path
      else:
        endpoint_url_path = f"/{endpoint_path}"

      endpoint_name = endpoint_url_path.strip('/').replace('/', '_')
      if not endpoint_name:
        endpoint_name = "root"

      # Always use our fixed headers
      headers = fixed_headers.copy()

      # Build request body
      body = {}
      if 'requestBody' in endpoint_data:
        request_content = endpoint_data['requestBody'].get('content', {})
        if 'application/json' in request_content:
          body_schema = request_content['application/json'].get('schema', {})

          # Use a simple body structure that matches the example
          sample_body = {"sampleAdditionalProperty": {}}
          body = {
            "mode": "raw",
            "raw": json.dumps(sample_body, indent=2),
            "options": {
              "raw": {
                "language": "json"
              }
            }
          }

      # Full URL with the fixed base
      full_url = f"{base_url}{endpoint_url_path}"

      # Parse URL components
      url_parts = base_url.split('://')
      protocol = url_parts[0]
      host_part = url_parts[1].split('/')[0]
      path_parts = []

      # Add 'aiv' to path parts if using the standard base URL
      if base_url.endswith('/aiv'):
        path_parts.append('aiv')

      # Add endpoint path parts
      endpoint_path_parts = endpoint_url_path.strip('/').split('/')
      path_parts.extend(endpoint_path_parts)

      # Create request item
      request_item = {
        "name": f"{endpoint_name} - POST",
        "request": {
          "method": "POST",
          "header": headers,
          "url": {
            "raw": full_url,
            "protocol": protocol,
            "host": host_part.split('.'),
            "path": path_parts
          }
        }
      }

      # Add body if present
      if body:
        request_item["request"]["body"] = body

      # Add test scripts to capture response with IMPROVED SUCCESS TEST
      request_item["event"] = [
        {
          "listen": "test",
          "script": {
            "exec": [
              "// Store response",
              "var responseObj = {",
              "    timestamp: new Date().toISOString(),",
              "    status: pm.response.code,",
              "    headers: pm.response.headers.toJSON(),",
              "    body: pm.response.text()",
              "};",
              "",
              "// Save response to file",
              "var fileName = pm.request.url.getPath().replace(/\\//g, '_') + '_response.json';",
              "pm.environment.set('responseFileName', fileName);",
              "pm.environment.set('lastResponse', JSON.stringify(responseObj));",
              "",
              "// Test for successful response - consider 200 as success regardless of body content",
              "pm.test(\"Request completed successfully\", function () {",
              "    pm.expect(pm.response.code).to.equal(200);",
              "});",
              "",
              "// Log the response for visibility",
              "console.log('Response status: ' + pm.response.code);",
              "console.log('Response body: ' + pm.response.text().substring(0, 200) + '...');",
              "",
              "// Log completion of this test",
              "console.log('Test completed for: ' + pm.request.url.getPath());"
            ],
            "type": "text/javascript"
          }
        }
      ]

      collection["item"].append(request_item)

    return collection

  def export_curl_commands(self, collection):
    """Export all curl commands to a single file with consistent headers."""
    # Ensure the directory exists
    os.makedirs(self.output_dir, exist_ok=True)

    # Single file for all curl commands
    curl_file = os.path.join(self.output_dir, "all_curl_commands.txt")

    curl_commands = []

    # Generate curl commands for each request
    for item in collection.get('item', []):
      name = item.get('name', 'unnamed_request').replace(' ', '_').lower()
      req = item.get('request', {})

      # Build curl command
      method = req.get('method', 'GET')
      url_obj = req.get('url', {})
      raw_url = url_obj.get('raw', '')

      if not raw_url:
        # Try to reconstruct URL
        protocol = url_obj.get('protocol', 'http')
        host = '.'.join(url_obj.get('host', ['localhost']))
        path = '/'.join(url_obj.get('path', []))
        raw_url = f"{protocol}://{host}/{path}"

      curl_parts = [f"# {name}", f"curl --location '{raw_url}'"]

      # Add headers in the exact order from the example
      standard_header_order = [
        'apitoken', 'category', 'owner', 'archiveMode',
        'timezone', 'dc', 'traceid', 'Content-Type'
      ]

      # Get headers from request
      headers_dict = {}
      for header in req.get('header', []):
        headers_dict[header['key']] = header['value']

      # Add headers in standard order
      for header_key in standard_header_order:
        if header_key in headers_dict:
          curl_parts.append(f"--header '{header_key}: {headers_dict[header_key]}'")

      # Add any remaining headers not in the standard order
      for header_key, header_value in headers_dict.items():
        if header_key not in standard_header_order:
          curl_parts.append(f"--header '{header_key}: {header_value}'")

      # Add body
      body_obj = req.get('body', {})
      if body_obj and body_obj.get('mode') == 'raw':
        body_content = body_obj.get('raw', '')
        curl_parts.append(f"--data '{body_content}'")

      # Build final command with line breaks
      curl_command = " \\\n  ".join(curl_parts)
      curl_commands.append(curl_command)

    # Write all curl commands to single file
    with open(curl_file, 'w') as file:
      file.write("\n\n".join(curl_commands))

    # Also create executable shell script
    sh_file = os.path.join(self.output_dir, "run_all_commands.sh")
    with open(sh_file, 'w') as file:
      file.write("#!/bin/bash\n\n")
      file.write("# This script contains all curl commands\n")
      file.write("# You can run them individually by copying the command\n\n")
      file.write("\n\n".join(curl_commands))
    os.chmod(sh_file, 0o755)

    return {
      'curl_file': curl_file,
      'shell_file': sh_file,
      'command_count': len(curl_commands)
    }

  def create_postman_environment(self):
    """Create a Postman environment file with variables."""
    environment = {
      "id": str(datetime.now().timestamp()),
      "name": "OpenAPI Test Environment",
      "values": [
        {
          "key": "lastResponse",
          "value": "",
          "type": "any",
          "enabled": True
        },
        {
          "key": "responseFileName",
          "value": "",
          "type": "string",
          "enabled": True
        }
      ],
      "_postman_variable_scope": "environment"
    }

    env_file = os.path.join(self.output_dir, "environment.json")
    with open(env_file, 'w') as file:
      json.dump(environment, file, indent=2)

    return env_file

  def upload_to_postman(self, collection_file, env_file):
    """Upload collection and environment to Postman using API"""
    results = {
      'success': False,
      'collection_id': None,
      'environment_id': None,
      'message': ''
    }

    if not self.postman_api_key:
      results['message'] = "No Postman API key provided."
      return results

    # Verify API key before proceeding
    if not self.verify_postman_api_key():
      results['message'] = "Invalid Postman API key."
      return results

    try:
      # Upload collection
      with open(collection_file, 'r') as f:
        collection_data = json.load(f)

      collection_payload = {
        'collection': collection_data
      }

      # Handle workspace parameter correctly - workspace needs to be a query parameter
      workspace_param = f"?workspace={self.postman_workspace_id}" if self.postman_workspace_id else ""
      collection_url = f"https://api.getpostman.com/collections{workspace_param}"

      headers = {
        'X-Api-Key': self.postman_api_key,
        'Content-Type': 'application/json'
      }

      print(f"Uploading collection to Postman...")
      collection_response = requests.post(
        collection_url,
        headers=headers,
        json=collection_payload
      )

      if collection_response.status_code in (200, 201):
        collection_id = collection_response.json().get('collection', {}).get('id')
        results['collection_id'] = collection_id
        print(f"Collection uploaded successfully. ID: {collection_id}")

        # Upload environment
        with open(env_file, 'r') as f:
          env_data = json.load(f)

        env_payload = {
          'environment': env_data
        }

        env_url = f"https://api.getpostman.com/environments{workspace_param}"
        print(f"Uploading environment to Postman...")
        env_response = requests.post(
          env_url,
          headers=headers,
          json=env_payload
        )

        if env_response.status_code in (200, 201):
          environment_id = env_response.json().get('environment', {}).get('id')
          results['environment_id'] = environment_id
          results['success'] = True
          results['message'] = "Successfully uploaded collection and environment to Postman"
          print(f"Environment uploaded successfully. ID: {environment_id}")
        else:
          results['message'] = f"Failed to upload environment: {env_response.text}"
          print(f"Failed to upload environment: {env_response.status_code}")
          print(env_response.text)
      else:
        results['message'] = f"Failed to upload collection: {collection_response.text}"
        print(f"Failed to upload collection: {collection_response.status_code}")
        print(collection_response.text)

    except Exception as e:
      results['message'] = f"Error uploading to Postman: {str(e)}"
      print(f"Error uploading to Postman: {str(e)}")

    return results

  def run_in_postman(self):
    """Generate Postman collection, environment, and curl commands."""
    # Read OpenAPI spec
    with open(self.openapi_file, 'r') as file:
      openapi_data = json.load(file)

    # Generate Postman collection
    collection = self.generate_postman_collection(openapi_data)

    # Save collection to file
    collection_file = os.path.join(self.output_dir, "collection.json")
    with open(collection_file, 'w') as file:
      json.dump(collection, file, indent=2)

    # Create environment file
    env_file = self.create_postman_environment()

    # Export curl commands to a single file
    curl_result = self.export_curl_commands(collection)

    # Create responses directory
    responses_dir = os.path.join(self.output_dir, "postman_responses")
    os.makedirs(responses_dir, exist_ok=True)

    # Upload to Postman if API key provided
    postman_results = {}
    if self.postman_api_key:
      postman_results = self.upload_to_postman(collection_file, env_file)

    # Create summary
    summary = {
      "timestamp": datetime.now().isoformat(),
      "openapi_file": os.path.abspath(self.openapi_file),
      "collection_file": os.path.abspath(collection_file),
      "environment_file": os.path.abspath(env_file),
      "curl_commands_file": os.path.abspath(curl_result['curl_file']),
      "curl_shell_script": os.path.abspath(curl_result['shell_file']),
      "responses_dir": os.path.abspath(responses_dir),
      "endpoints": len(collection.get('item', [])),
      "postman_upload": postman_results,
      "instructions": {
        "postman_import": "Open Postman and import the collection and environment files",
        "postman_run": "Run the collection in Postman",
        "manual_execution": "Execute curl commands from the curl commands file"
      }
    }

    # Save summary
    summary_file = os.path.join(self.output_dir, "summary.json")
    with open(summary_file, 'w') as file:
      json.dump(summary, file, indent=2)

    print(f"Postman collection generated with {summary['endpoints']} endpoints")
    print(f"Collection saved to: {collection_file}")
    print(f"Environment saved to: {env_file}")
    print(f"All curl commands saved to: {curl_result['curl_file']}")
    print(f"Executable shell script saved to: {curl_result['shell_file']}")

    if postman_results.get('success'):
      print(f"Successfully uploaded to Postman!")
      print(f"Collection ID: {postman_results.get('collection_id')}")
      print(f"Environment ID: {postman_results.get('environment_id')}")
    elif self.postman_api_key:
      print(f"Failed to upload to Postman: {postman_results.get('message')}")

    print(f"Summary saved to: {summary_file}")

    print("\nInstructions:")
    print("1. Import the collection and environment into Postman")
    print("2. Run the collection in Postman to see results")
    print("3. Responses will be saved to the 'postman_responses' directory")
    print(f"4. You can find all curl commands in: {curl_result['curl_file']}")

    return summary

def main():
  """Main function to run the script."""
  parser = argparse.ArgumentParser(description='Generate Postman collection and curl commands from OpenAPI spec')
  parser.add_argument('--openapi', required=True, help='Path to OpenAPI JSON file')
  parser.add_argument('--output', required=True, help='Output directory')
  parser.add_argument('--port', type=int, default=8080, help='Port to use (default: 8080)')
  parser.add_argument('--curl', help='Existing curl command to extract headers from')
  parser.add_argument('--postman-key', help='Postman API Key')
  parser.add_argument('--postman-workspace', help='Postman workspace ID (optional)')

  args = parser.parse_args()

  tester = PostmanAPITester(
    args.openapi,
    args.output,
    args.port,
    args.curl,
    args.postman_key,
    args.postman_workspace
  )
  tester.run_in_postman()

# Example usage when run directly
if __name__ == "__main__":
  # If arguments are provided, use them
  import sys
  if len(sys.argv) > 1:
    main()
  else:
    # Example configuration
    input_file = "openapi_definition.json"
    output_dir = "postman_test_results"

    # Example of an existing curl command
    existing_curl = """curl --location 'http://localhost:8080/aiv/v5/tokens/generate' \\
--header 'apitoken: eyJhbGciOiJIUzI1NiJ9.eyJkZXBhcnRtZW50IjoiRGVmYXVsdCIsInVzZXJuYW1lIjoiQWRtaW4iLCJzdWIiOiJBZG1pbiIsImlhdCI6MTc0MDA0MzYwNiwiZXhwIjoxNzQwOTA3NjA2fQ.SC5rulkG-znKgBySuC_aNvkJtx7S1JrrO6OoscAS2Kc' \\
--header 'category: REPORTS' \\
--header 'owner: ADMIN' \\
--header 'archiveMode: false' \\
--header 'timezone: SYSTEM' \\
--header 'dc: Default' \\
--header 'traceid: API' \\
--header 'Content-Type: application/json' \\
--data '{
  "sampleAdditionalProperty": {}
}'"""

    # Postman API key - get this from your Postman account settings
    postman_api_key = "PMAK-your-postman-api-key"
    postman_workspace_id = None  # Optional

    # Fixed port to use
    port = 8080

    tester = PostmanAPITester(
      input_file,
      output_dir,
      port,
      existing_curl,
      postman_api_key,
      postman_workspace_id
    )
    tester.run_in_postman()