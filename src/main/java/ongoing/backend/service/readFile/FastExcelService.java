package ongoing.backend.service.readFile;

import ongoing.backend.data.response.ColumnDataResponse;
import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class FastExcelService {
  public List<List<Object>> readExcel(String filePath, String sheetName, int headerIndex) throws IOException {
    List<List<Object>> data = new ArrayList<>();
    List<Integer> columnIndexes = new ArrayList<>();
    try (InputStream is = new FileInputStream(filePath);
         ReadableWorkbook workbook = new ReadableWorkbook(is)) {
      Sheet sheet;
      if (!StringUtils.isEmpty(sheetName)) {
        sheet = workbook.findSheet(sheetName).orElseThrow(() -> new IOException("Sheet not found: " + sheetName));
      } else {
        sheet = workbook.getFirstSheet();
      }
      try (Stream<Row> rows = sheet.openStream()) {
        rows
          .limit(headerIndex)
          .forEach(row -> {
            row.forEach(cell -> {
              if (cell != null) {
                int columnIndex = cell.getColumnIndex();
                columnIndexes.add(columnIndex);
              }
            });
          });
      }
      try (Stream<Row> rows = sheet.openStream()) {
        rows.skip(headerIndex).forEach(row -> {
          List<Object> rowData = new ArrayList<>();
          for (Integer columnIndex : columnIndexes) {
            Cell cell = row.getCell(columnIndex);
            if (cell != null) {
              Object value = cell.getValue(); // Retrieve the cell value
              rowData.add(value);
            } else {
              rowData.add(null); // Assign null if the cell does not exist
            }
          }
          data.add(rowData);
        });
      }
    }

    return data;
  }

  public List<ColumnDataResponse> getHeader(String filePath, String sheetName, int headerIndex) throws IOException {
    List<ColumnDataResponse> columnDataResponses = new ArrayList<>();
    Map<Integer, String> dataTypeMap = new HashMap<>();
    Map<Integer, String> columnNamesMap = new HashMap<>();
    try (InputStream is = new FileInputStream(filePath);
         ReadableWorkbook workbook = new ReadableWorkbook(is)) {
      Sheet sheet;
      if (!StringUtils.isEmpty(sheetName)) {
        sheet = workbook.findSheet(sheetName).orElseThrow(() -> new IOException("Sheet not found: " + sheetName));
      } else {
        sheet = workbook.getFirstSheet();
      }
      try (Stream<Row> rows = sheet.openStream()) {
        rows.skip(headerIndex).limit(1)
          .forEach(row -> {
            row.forEach(cell -> {
              if (cell != null) {
                int columnIndex = cell.getColumnIndex();
                String type = cell.getType().toString();
                dataTypeMap.put(columnIndex, type);
              }
            });
          });
      }
      try (Stream<Row> rows = sheet.openStream()) {
        rows
          .limit(headerIndex)
          .forEach(row -> {
            row.forEach(cell -> {
              if (cell != null) {
                String header = cell.getText();
                int columnIndex = cell.getColumnIndex();
                if (header != null && !header.contains("::") && !StringUtils.isBlank(header)) {
                  columnNamesMap.put(columnIndex, header.trim());
                }
              }
            });
          });
      }
    }
    for (Map.Entry<Integer, String> entry : columnNamesMap.entrySet()) {
      ColumnDataResponse columnData = new ColumnDataResponse()
        .setAlias(entry.getValue())
        .setKey(entry.getValue())
        .setType(dataTypeMap.get(entry.getKey())); // Add data type
      columnDataResponses.add(columnData);
    }
    return columnDataResponses;
  }
}
