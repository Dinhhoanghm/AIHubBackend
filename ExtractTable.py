import os
import shutil
import camelot
from fastapi import FastAPI, File, UploadFile
import uvicorn

app = FastAPI()

# Create a temporary directory for uploads
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

def extract_all_tables(pdf_path: str) -> list:
  """
  Uses Camelot to extract all tables from a PDF file.
  For each table, converts the pandas DataFrame to a list-of-lists
  (which represents the original format with the header row included)
  and returns a list of dictionaries with a unique table_id.
  """
  # Read all tables from the PDF (default flavor 'lattice'; adjust flavor if needed)
  tables = camelot.read_pdf(pdf_path, pages="all")
  result = []

  for index, table in enumerate(tables, start=1):
    df = table.df
    # Convert the DataFrame to a list of lists (preserving the original structure)
    rows = df.values.tolist()
    result.append({
      "table_id": f"Table_{index}",
      "rows": rows
    })
  return result

@app.post("/extract-tables/")
async def extract_tables(file: UploadFile = File(...)):
  """
  Endpoint to extract tables from an uploaded PDF file.
  Returns JSON in the format:

    {
      "status": "success",
      "tables": [
        {
          "table_id": "Table_1",
          "rows": [
            ["Header1", "Header2", ...],
            ["Row1Cell1", "Row1Cell2", ...],
            ...
          ]
        },
        {
          "table_id": "Table_2",
          "rows": [ ... ]
        },
        ...
      ]
    }
  """
  try:
    # Save the uploaded file temporarily
    file_path = os.path.join(UPLOAD_DIR, file.filename)
    with open(file_path, "wb") as buffer:
      shutil.copyfileobj(file.file, buffer)

    # Extract tables from the PDF in their original format
    tables_info = extract_all_tables(file_path)

    # Clean up the temporary file
    os.remove(file_path)

    return {"status": "success", "tables": tables_info}
  except Exception as e:
    return {"status": "error", "message": str(e)}

if __name__ == "__main__":
  # Run the FastAPI application on port 9090
  uvicorn.run(app, host="0.0.0.0", port=9090)
