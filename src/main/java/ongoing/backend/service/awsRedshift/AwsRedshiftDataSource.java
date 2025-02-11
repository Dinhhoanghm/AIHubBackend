package ongoing.backend.service.awsRedshift;

import com.aiv.datasource.IEDatasource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static ongoing.backend.service.awsRedshift.sql.RedshiftSQLStatementConstant.*;

@Service
public class AwsRedshiftDataSource implements IEDatasource {
  private final RedShiftService redShiftService;

  public AwsRedshiftDataSource(RedShiftService redShiftService) {
    this.redShiftService = redShiftService;
  }

  @Override
  public Map<String, Object> getData(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String sql = map.getOrDefault("sql", "").toString();
    try {
      return redShiftService.executeStatement(url, username, password, sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Object> getMetaData(Map<String, Object> map) {
    String tableName = (String) map.getOrDefault("tableName", "");
    String schemaName = (String) map.getOrDefault("schemaName", "");
    String databaseName = (String) map.getOrDefault("databaseName", "");
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    try {
      return redShiftService.executeStatement(url, username, password, getGetAllColumnFromTable(databaseName, schemaName, tableName));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public Map<String, Object> getObjects(Map<String, Object> map) {
    String type = (String) map.getOrDefault("type", "");
    String tableName = (String) map.getOrDefault("tableName", "");
    String schemaName = (String) map.getOrDefault("schemaName", "");
    String databaseName = (String) map.getOrDefault("databaseName", "");
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String sql = map.getOrDefault("sql", "").toString();
    if (type.equalsIgnoreCase("GET_DATABASES")) {
      try {
        return redShiftService.executeStatement(url, username, password, getAllDatabase());
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    if (type.equalsIgnoreCase("GET_SCHEMAS")) {
      try {
        return redShiftService.executeStatement(url, username, password, getGetAllSchemaFromDatabase(databaseName));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    if (type.equalsIgnoreCase("GET_TABLES")) {
      try {
        return redShiftService.executeStatement(url, username, password, getGetAllTableFromSchema(databaseName, schemaName));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    if (type.equalsIgnoreCase("GET_COLUMNS")) {
      try {
        return redShiftService.executeStatement(url, username, password, getGetAllColumnFromTable(databaseName, schemaName, tableName));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  @Override
  public Map<String, Object> testConnection(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    try {
      String connection = redShiftService.connectRedshift(url, username, password);
      Map<String, Object> result = new HashMap<>();
      result.put("connection", connection);
      return result;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }
}
