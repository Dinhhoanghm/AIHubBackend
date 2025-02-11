package ongoing.backend.service.webScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class WebScrapingService {
  private static Integer HEADER_DEPTH = 10;

  public Map<String, Object> getTableData(String url, Integer index, Integer headerDepth) {
    Document doc = null;
    try {
      doc = Jsoup.connect(url).get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Elements tables = doc.select("table");
    Element table = tables.get(index);
    List<Map<String, String>> metadata = new ArrayList<>();
    List<List<String>> headerRows = new ArrayList<>();

    Elements headerElements = table.select("thead tr");
    if (headerElements.isEmpty()) {
      headerElements = table.select("tbody tr:lt(" + headerDepth + ")");
    }
    for (Element row : headerElements) {
      List<String> headerRow = new ArrayList<>();
      for (Element th : row.select("th, td")) {
        headerRow.add(th.text().trim());
      }
      headerRows.add(headerRow);
    }
    List<String> finalHeaders = flattenHeaders(headerRows);
    int columnCount = finalHeaders.size();
    for (String columnName : finalHeaders) {
      Map<String, String> columnInfo = new HashMap<>();
      columnInfo.put("name", columnName);
      columnInfo.put("alias", columnName.toLowerCase().replace(" ", "_"));
      columnInfo.put("datatype", guessDataType(columnName));
      columnInfo.put("key", columnName.toLowerCase().contains("id") ? "primary" : "");

      metadata.add(columnInfo);
    }
    List<List<String>> data = new ArrayList<>();
    List<Element> rows = table.select("tbody tr").subList(headerDepth, table.select("tbody tr").size());

    for (Element row : rows) {
      List<String> rowData = new ArrayList<>();
      Elements cells = row.select("td, th");

      for (int i = 0; i < columnCount; i++) {
        String cellValue = i < cells.size() ? cells.get(i).text().trim() : "";
        rowData.add(cellValue.isEmpty() ? null : cellValue);
      }

      if (!rowData.isEmpty()) {
        data.add(rowData);
      }
    }
    List<List<String>> cleanedData = removeEmptyColumns(data);
    Map<String, Object> result = new HashMap<>();
    List<Object> finalData = new ArrayList<>();
    finalData.add(metadata.toArray());
    finalData.add(cleanedData.toArray());
    result.put("data", finalData);
    return result;
  }

  public Map<String, Object> getTableColumn(String url, Integer index, Integer depthHeader) {
    Document doc = null;
    try {
      doc = Jsoup.connect(url).get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Elements tables = doc.select("table");
    Element table = tables.get(index);
    Elements headerElements = table.select("thead tr");
    List<List<String>> headerRows = new ArrayList<>();
    if (headerElements.isEmpty()) {
      headerElements = table.select("tbody tr:lt(" + depthHeader + ")");
    }

    for (Element row : headerElements) {
      List<String> headerRow = new ArrayList<>();
      for (Element th : row.select("th, td")) {
        headerRow.add(th.text().trim());
      }
      headerRows.add(headerRow);
    }
    List<Map<String, String>> metadata = new ArrayList<>();
    List<String> finalHeaders = flattenHeaders(headerRows);
    for (String columnName : finalHeaders) {
      Map<String, String> columnInfo = new HashMap<>();
      columnInfo.put("name", columnName);
      columnInfo.put("alias", columnName.toLowerCase().replace(" ", "_"));
      columnInfo.put("datatype", guessDataType(columnName));
      columnInfo.put("key", columnName);

      metadata.add(columnInfo);
    }
    Map<String, Object> result = new HashMap<>();
    result.put("metadata", metadata);
    return result;
  }


  public Map<String, Object> getAllTablesInfo(String url) {
    Document doc = null;
    try {
      doc = Jsoup.connect(url).get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<Map<String, Object>> tableList = new ArrayList<>();
    Elements tables = doc.select("table");

    int tableIndex = 1;
    for (Element table : tables) {
      Elements headerElements = table.select("thead tr th");
      List<List<String>> firstTenRows = new ArrayList<>();
      if (headerElements.isEmpty()) {
        headerElements = table.select("tbody tr:lt(" + HEADER_DEPTH + ")");
      }

      for (Element row : headerElements) {
        List<String> headerRow = new ArrayList<>();
        for (Element th : row.select("th, td")) {
          headerRow.add(th.text().trim());
        }
        firstTenRows.add(headerRow);
      }

      String tableId = "table_" + tableIndex;
      Map<String, Object> tableInfo = new HashMap<>();
      tableInfo.put("table_name", "Table_" + tableIndex);
      tableInfo.put("first_ten_row", firstTenRows);
      tableInfo.put("table_id", tableId);
      tableList.add(tableInfo);
      tableIndex++;
    }
    return Map.of("data", tableList);
  }

  private static List<String> flattenHeaders(List<List<String>> headerRows) {
    List<String> flattenedHeaders = new ArrayList<>();
    int maxColumns = headerRows.stream().mapToInt(List::size).max().orElse(0);
    String[] mergedHeaders = new String[maxColumns];

    for (List<String> row : headerRows) {
      for (int i = 0; i < row.size(); i++) {
        mergedHeaders[i] = (mergedHeaders[i] == null ? "" : mergedHeaders[i] + " ") + row.get(i);
      }
    }
    for (String header : mergedHeaders) {
      flattenedHeaders.add(header != null ? header.trim() : "");
    }
    return flattenedHeaders;
  }

  // Guess the datatype of the column based on its name
  private static String guessDataType(String columnName) {
    columnName = columnName.toLowerCase();
    if (columnName.contains("date") || columnName.contains("year")) {
      return "date";
    } else if (columnName.contains("id") || columnName.contains("number")) {
      return "integer";
    } else if (columnName.matches(".*\\d+.*")) {
      return "numeric";
    } else {
      return "string";
    }
  }

  private static List<List<String>> removeEmptyColumns(List<List<String>> data) {
    if (data.isEmpty()) return data;

    int columnCount = data.get(0).size();
    boolean[] isColumnEmpty = new boolean[columnCount];
    Arrays.fill(isColumnEmpty, true);

    // Check if each column has at least one non-null value
    for (List<String> row : data) {
      for (int i = 0; i < columnCount; i++) {
        if (row.get(i) != null) {
          isColumnEmpty[i] = false;
        }
      }
    }

    // Remove empty columns from data
    List<List<String>> cleanedData = new ArrayList<>();
    for (List<String> row : data) {
      List<String> newRow = new ArrayList<>();
      for (int i = 0; i < columnCount; i++) {
        if (!isColumnEmpty[i]) {
          newRow.add(row.get(i));
        }
      }
      cleanedData.add(newRow);
    }

    return cleanedData;
  }
}
