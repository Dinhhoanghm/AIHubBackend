package ongoing.backend.service;

import lombok.extern.log4j.Log4j2;
import ongoing.backend.config.jackson.json.JsonObject;
import ongoing.backend.data.dto.AttributionNameResponse;
import ongoing.backend.data.dto.JsonOutput;
import ongoing.backend.data.response.ColumnDataResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static ongoing.backend.utils.ReadExcelUtil.*;


@Service
@Log4j2
public class ReadExcelService {

  public JsonOutput readExcelToJsonOutput(MultipartFile multipartFile, Integer offset, Integer limit, Integer headerDept, String sheetName) throws IOException, InvalidFormatException {
    InputStream initialStream = multipartFile.getInputStream();
    byte[] buffer = new byte[initialStream.available()];
    initialStream.read(buffer);
    String path = "src/main/resources/targetFile.xlsx";
    File file = new File(path);
    try (OutputStream outStream = new FileOutputStream(file)) {
      outStream.write(buffer);
    }
    List<ColumnDataResponse> columnData = getColumn(file, headerDept, sheetName);
    List<Object[]> value = readExcel(file, offset, limit, sheetName,headerDept).stream().map(s -> s.toArray())
      .collect(Collectors.toList());
    List<Object> data = new ArrayList<>();
    data.add(columnData.toArray());
    data.add(value.toArray());
    JsonOutput jsonOutput = new JsonOutput();
    jsonOutput.setData(data.toArray());
    Files.deleteIfExists(file.toPath());
    return jsonOutput;
  }

  public List<List<Object>> readExcel(File file, Integer offset, Integer limit, String sheetName,Integer headerDepth) throws IOException, InvalidFormatException {
    InputStream is;
    Workbook xssfWorkbook;
    try {
      is = new FileInputStream(file);
      xssfWorkbook = new XSSFWorkbook(is);
      is.close();
      return transToObject(xssfWorkbook, offset, limit, sheetName,headerDepth);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Có lỗi xãy ra khi đọc file excel ：" + e.getMessage());
    }
  }


  public List<ColumnDataResponse> getColumn(File file, Integer headerDepth, String sheetName) throws IOException, InvalidFormatException {
    Workbook workbook = new XSSFWorkbook(file);
    Sheet sheet;
    if (!StringUtils.isBlank(sheetName)) {
      sheet = workbook.getSheet(sheetName);
    } else {
      sheet = workbook.getSheetAt(0);
    }
    List<ColumnDataResponse> columnDataList = new ArrayList<>();
    Row dataRow = sheet.getRow(headerDepth);
    for (int col = 0; col < sheet.getRow(headerDepth).getLastCellNum(); col++) {
      for (int row = 0; row < headerDepth; row++) {
        Row headerRow = sheet.getRow(row);
        if (headerRow != null) {
          Cell cell = headerRow.getCell(col);
          Cell cellType = dataRow.getCell(col);
          String attributeName = getCellValue(cell); // Get cell value
          String attributeType = getCellType(cellType);  // Get cell type
          if (attributeName != null && StringUtils.isNotBlank(attributeName) && ! attributeName.equals("")) {
            ColumnDataResponse columnData = new ColumnDataResponse()
              .setAlias(attributeName)
              .setKey(attributeName)
              .setType(attributeType); // Add data type
            columnDataList.add(columnData);
          }
        }
      }
    }

    return columnDataList;
  }


  private List<List<Object>> transToObject(Workbook xssfWorkbook, Integer offset, Integer limit, String sheetName,Integer headerDepth) throws IOException, InvalidFormatException {
    Sheet xssfSheet;
    if (!StringUtils.isBlank(sheetName)) {
      xssfSheet = xssfWorkbook.getSheet(sheetName);
    } else {
      xssfSheet = xssfWorkbook.getSheetAt(0); // Get the first sheet
    }
    int totalRows = xssfSheet.getLastRowNum();
    int startRow = Math.max(offset, headerDepth);
    int endRow = Math.min(offset + limit - 1, totalRows);
    return IntStream.rangeClosed(startRow, endRow)
      .boxed()
      .map(rowNum -> {
        Row row = xssfSheet.getRow(rowNum);
        return row != null ? collectRowValues(row) : Collections.emptyList();
      })
      .filter(rowData -> !rowData.isEmpty())
      .collect(Collectors.toList());
  }

  private JsonObject convertToJsonObject(List<AttributionNameResponse> attributions, Row xssfRow) {
    if (xssfRow == null) return null;
    Map<String, Object> objectMap = attributions.stream()
      .map(response -> {
        String value;
        try {
          value = getValue(xssfRow.getCell(response.getOrder()));
        } catch (Exception e) {
          log.error("Fail to real value excel: {}", response, e);
          return null;
        }
        if (StringUtils.isEmpty(value)) return null;
        return Pair.of(response.getName(), value);
      })
      .filter(Objects::nonNull)
      .collect(toMap(Pair::getKey, Pair::getValue));
    JsonObject jsonObject = new JsonObject(objectMap);
    return jsonObject;
  }
}
