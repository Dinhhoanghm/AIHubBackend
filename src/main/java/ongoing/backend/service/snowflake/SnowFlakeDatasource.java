package ongoing.backend.service.snowflake;

import com.aiv.datasource.IEDatasource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static ongoing.backend.service.snowflake.SnowflakeSQLStatementConstant.*;


@Service
public class SnowFlakeDatasource implements IEDatasource {
  private final SnowFlakeService snowflakeService;

  public SnowFlakeDatasource(SnowFlakeService snowflakeService) {
    this.snowflakeService = snowflakeService;
  }

  @Override
  public Map<String, Object> getData(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String database = map.get("database").toString();
    String schema = map.get("schema").toString();
    String role = map.get("role").toString();
    String warehouse = map.get("warehouse").toString();

    String sql = map.getOrDefault("sql", "").toString();
    SnowFlakeCommon snowFlakeCommon = new SnowFlakeCommon();
    snowFlakeCommon.setUrl(url);
    snowFlakeCommon.setPassword(password);
    snowFlakeCommon.setDatabase(database)
      .setRole(role)
      .setSchema(schema)
      .setUser(username)
      .setWarehouse(warehouse);
    try {
      return snowflakeService.executeStatement(snowFlakeCommon, sql);
    } catch (SQLException e) {
      throw new RuntimeException("Error executing query: " + e.getMessage(), e);
    }
  }

  @Override
  public Map<String, Object> getMetaData(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String database = map.get("database").toString();
    String table = map.get("table").toString();
    String schema = map.get("schema").toString();
    String role = map.get("role").toString();
    String warehouse = map.get("warehouse").toString();

    String sql = map.getOrDefault("sql", "").toString();
    SnowFlakeCommon snowFlakeCommon = new SnowFlakeCommon();
    snowFlakeCommon.setUrl(url);
    snowFlakeCommon.setPassword(password);
    snowFlakeCommon.setDatabase(database)
      .setRole(role)
      .setSchema(schema)
      .setUser(username)
      .setWarehouse(warehouse);
    try {
      return snowflakeService.executeStatement(snowFlakeCommon,
        getGetAllColumnFromTable(database, schema, table));
    } catch (SQLException e) {
      throw new RuntimeException("Error getting metadata: " + e.getMessage(), e);
    }
  }

  @Override
  public Map<String, Object> getObjects(Map<String, Object> map) {
    String type = (String) map.getOrDefault("type", "");
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String database = map.get("database").toString();
    String table = map.get("table").toString();
    String schema = map.get("schema").toString();
    String role = map.get("role").toString();
    String warehouse = map.get("warehouse").toString();

    String sql = map.getOrDefault("sql", "").toString();
    SnowFlakeCommon snowFlakeCommon = new SnowFlakeCommon();
    snowFlakeCommon.setUrl(url);
    snowFlakeCommon.setPassword(password);
    snowFlakeCommon.setDatabase(database)
      .setRole(role)
      .setSchema(schema)
      .setUser(username)
      .setWarehouse(warehouse);

    try {
      switch (type.toUpperCase()) {
        case "GET_DATABASES":
          return snowflakeService.executeStatement(snowFlakeCommon,
            getAllDatabase());

        case "GET_SCHEMAS":
          return snowflakeService.executeStatement(snowFlakeCommon,
            getGetAllSchemaFromDatabase(database));

        case "GET_TABLES":
          return snowflakeService.executeStatement(snowFlakeCommon,
            getGetAllTableFromSchema(database, schema));

        case "GET_COLUMNS":
          return snowflakeService.executeStatement(snowFlakeCommon, getGetAllColumnFromTable(database, schema, table));

        default:
          return null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error getting objects: " + e.getMessage(), e);
    }
  }

  @Override
  public Map<String, Object> testConnection(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String database = map.get("database").toString();
    String table = map.get("table").toString();
    String schema = map.get("schema").toString();
    String role = map.get("role").toString();
    String warehouse = map.get("warehouse").toString();

    String sql = map.getOrDefault("sql", "").toString();
    SnowFlakeCommon snowFlakeCommon = new SnowFlakeCommon();
    snowFlakeCommon.setUrl(url);
    snowFlakeCommon.setPassword(password);
    snowFlakeCommon.setDatabase(database)
      .setRole(role)
      .setSchema(schema)
      .setUser(username)
      .setWarehouse(warehouse);

    try {
      String connection = snowflakeService.connectSnowflake(snowFlakeCommon);
      Map<String, Object> result = new HashMap<>();
      result.put("connection", connection);
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Connection test failed: " + e.getMessage(), e);
    }
  }
}