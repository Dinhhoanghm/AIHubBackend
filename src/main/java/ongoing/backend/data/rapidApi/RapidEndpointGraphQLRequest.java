package ongoing.backend.data.rapidApi;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RapidEndpointGraphQLRequest {
  private String query = "query getApiVersion($apiVersionId: ID!) {\n" +
    "  apiVersion(apiVersionId: $apiVersionId) {\n" +
    "    id\n" +
    "    name\n" +
    "    spec\n" +
    "    current\n" +
    "    versionStatus\n" +
    "    apiVersionType\n" +
    "    assets(visible: true) {\n" +
    "      ...AssetInfo\n" +
    "    }\n" +
    "    endpoints(pagingArgs: {limit: -1}) {\n" +
    "      ...EndpointInfo\n" +
    "    }\n" +
    "    targetGroup {\n" +
    "      targetUrls {\n" +
    "        url\n" +
    "      }\n" +
    "    }\n" +
    "    accessControl {\n" +
    "      security {\n" +
    "        ...SecurityInfo\n" +
    "      }\n" +
    "    }\n" +
    "    groups {\n" +
    "      ...EndpointGroupInfo\n" +
    "    }\n" +
    "    publicdns {\n" +
    "      ...PublicDnsInfo\n" +
    "    }\n" +
    "  }\n" +
    "}\n" +
    "\n" +
    "fragment AssetInfo on Asset {\n" +
    "  id\n" +
    "  filename\n" +
    "  title\n" +
    "  description\n" +
    "  visible\n" +
    "  fileSizeBytes\n" +
    "  createdAt\n" +
    "}\n" +
    "\n" +
    "fragment EndpointInfo on Endpoint {\n" +
    "  id\n" +
    "  isGraphQL\n" +
    "  route\n" +
    "  method\n" +
    "  name\n" +
    "  description\n" +
    "  apiversion\n" +
    "  group\n" +
    "  index\n" +
    "}\n" +
    "\n" +
    "fragment SecurityInfo on BaseAuthentication {\n" +
    "  apiVersionId\n" +
    "  name\n" +
    "  description\n" +
    "  securityType\n" +
    "  requirements {\n" +
    "    id\n" +
    "    scope\n" +
    "  }\n" +
    "  ... on HttpAuthentication {\n" +
    "    scheme\n" +
    "  }\n" +
    "  ... on ApiKeyAuthentication {\n" +
    "    authParam {\n" +
    "      name\n" +
    "      description\n" +
    "    }\n" +
    "    in\n" +
    "  }\n" +
    "  ... on Oauth2Authentication {\n" +
    "    accessTokenUrl\n" +
    "    authorizationUrl\n" +
    "    grantType\n" +
    "    separator\n" +
    "    clientSecretRequired\n" +
    "    clientAuthentication\n" +
    "    handleOauthTokenAtFrontend\n" +
    "    scopes {\n" +
    "      name\n" +
    "      description\n" +
    "    }\n" +
    "    extraMetadata {\n" +
    "      pkceEnabled\n" +
    "      codeChallengeMethod\n" +
    "      customOAuth2AuthPrefix\n" +
    "      JWTProfile\n" +
    "    }\n" +
    "  }\n" +
    "}\n" +
    "\n" +
    "fragment EndpointGroupInfo on EndpointsGroup {\n" +
    "  id\n" +
    "  name\n" +
    "  index\n" +
    "  description\n" +
    "  externalDocs {\n" +
    "    description\n" +
    "    url\n" +
    "  }\n" +
    "}\n" +
    "\n" +
    "fragment PublicDnsInfo on Publicdns {\n" +
    "  proxyMode\n" +
    "  address\n" +
    "  current\n" +
    "  id\n" +
    "}";

  private Object variables;
}
