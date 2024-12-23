package ongoing.backend.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.FORMULA;

public class ReadExcelUtil {
  public static Map<Integer, Object> getCellRowMap(Row xssfRow) {
    Map<Integer, Object> cellMap = new HashMap<>();
    if (xssfRow != null) {
      for (int cellNum = 0; cellNum < xssfRow.getLastCellNum(); cellNum++) {
        Cell xssfCell = xssfRow.getCell(cellNum);
        cellMap.put(cellNum, getValue(xssfCell));
      }
    }
    return cellMap;
  }


  public static String getValue(Cell cell) {
    if (cell == null) return null;
    return getValueFromCell(cell);
  }

  private static String getValueFromCell(Cell cell) {
    if (null == cell) {
      return "";
    } else if (cell.getCellType() == CellType.BOOLEAN) {
      return String.valueOf(cell.getBooleanCellValue());
    } else if (cell.getCellType() == CellType.NUMERIC) {
      return getNumberValue(cell);
    } else if (cell.getCellType() == FORMULA) {
      return getFormulaValue(cell);
    } else if (cell.getCellType() == CellType.BLANK) return "";
    else return String.valueOf(cell.getStringCellValue());
  }

  @NotNull
  private static String getFormulaValue(Cell cell) {
    String cellText = cell.getCellFormula();
    if (cellText.equalsIgnoreCase("TRUE()")) {
      return "true";
    } else if (cellText.equalsIgnoreCase("FALSE()")) {
      return "false";
    }
    if (DateUtil.isCellDateFormatted(cell)) {
      DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
      Date date = cell.getDateCellValue();
      return df.format(date);
    }
    return cellText;
  }

  private static String getNumberValue(Cell cell) {
    if (DateUtil.isCellDateFormatted(cell)) {
      DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
      Date date = cell.getDateCellValue();
      return df.format(date);
    }
    double numericCellValue = cell.getNumericCellValue();
    NumberFormat numberFormatter = new DecimalFormat("##.000");
    String value = numberFormatter.format(numericCellValue);
    return value.replace(".000", "");
  }

  public static List<String> getRowAsList(Row row) {
    List<String> rowData = new ArrayList<>();
    if (row != null) {
      for (Cell cell : row) {
        rowData.add(getCellValue(cell));
      }
    }
    return rowData;
  }

  // Utility method to get the value of a cell as a String
  public static String getCellValue(Cell cell) {
    if (cell == null) {
      return "";
    }
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getDateCellValue().toString();
        }
        return Double.toString(cell.getNumericCellValue());
      case BOOLEAN:
        return Boolean.toString(cell.getBooleanCellValue());
      case FORMULA:
        return cell.getCellFormula();
      default:
        return "";
    }
  }

  public static List<Object> collectRowValues(Row row) {
    List<Object> rowData = new ArrayList<>();
    for (Cell cell : row) {
      rowData.add(getCellValue(cell));
    }
    return rowData;
  }


  // Utility method to get the cell type as a String
  public static String getCellType(Cell cell) {
    if (cell == null) {
      return "Unknown";
    }
    switch (cell.getCellType()) {
      case STRING:
        return "String";
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return "Date";
        }
        return "Numeric";
      case BOOLEAN:
        return "Boolean";
      case FORMULA:
        return "Formula";
      default:
        return "Unknown";
    }
  }
}
