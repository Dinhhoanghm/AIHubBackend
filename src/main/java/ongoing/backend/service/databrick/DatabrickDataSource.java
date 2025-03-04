package ongoing.backend.service.databrick;

import com.aiv.datasource.IEDatasource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static ongoing.backend.service.databrick.sql.DatabrickSQLStatementConstant.*;


@Service
public class DatabrickDataSource implements IEDatasource {
  private final DatabrickService databrickService;

  public DatabrickDataSource(DatabrickService databrickService) {
    this.databrickService = databrickService;
  }

  @Override
  public Map<String, Object> getData(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String httpPath = map.get("httpPath").toString();
    String catalog = map.getOrDefault("catalog", "").toString();
    String schema = map.getOrDefault("schema", "").toString();

    String sql = map.getOrDefault("sql", "").toString();
    DatabrickCommon databrickCommon = new DatabrickCommon();
    databrickCommon.setUrl(url);
    databrickCommon.setPassword(password);
    databrickCommon.setUser(username);
    databrickCommon.setHttpPath(httpPath);

    if (!catalog.isEmpty()) {
      databrickCommon.setCatalog(catalog);
    }
    if (!schema.isEmpty()) {
      databrickCommon.setSchema(schema);
    }

    try {
      return databrickService.executeStatement(databrickCommon, sql);
    } catch (SQLException e) {
      throw new RuntimeException("Error executing query: " + e.getMessage(), e);
    }
  }

  @Override
  public Map<String, Object> getMetaData(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String httpPath = map.get("httpPath").toString();
    String catalog = map.getOrDefault("catalog", "").toString();
    String schema = map.getOrDefault("schema", "").toString();
    String table = map.get("table").toString();

    DatabrickCommon databrickCommon = new DatabrickCommon();
    databrickCommon.setUrl(url);
    databrickCommon.setPassword(password);
    databrickCommon.setUser(username);
    databrickCommon.setHttpPath(httpPath);

    if (!catalog.isEmpty()) {
      databrickCommon.setCatalog(catalog);
    }
    if (!schema.isEmpty()) {
      databrickCommon.setSchema(schema);
    }

    try {
      return databrickService.executeStatement(databrickCommon,
        getGetAllColumnFromTable(catalog, schema, table));
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
    String httpPath = map.get("httpPath").toString();
    String catalog = map.getOrDefault("catalog", "").toString();
    String schema = map.getOrDefault("schema", "").toString();
    String table = map.getOrDefault("table", "").toString();

    DatabrickCommon databrickCommon = new DatabrickCommon();
    databrickCommon.setUrl(url);
    databrickCommon.setPassword(password);
    databrickCommon.setUser(username);
    databrickCommon.setHttpPath(httpPath);

    if (!catalog.isEmpty()) {
      databrickCommon.setCatalog(catalog);
    }
    if (!schema.isEmpty()) {
      databrickCommon.setSchema(schema);
    }

    try {
      switch (type.toUpperCase()) {
        case "GET_CATALOGS":
          return databrickService.executeStatement(databrickCommon,
            getAllCatalogs());

        case "GET_SCHEMAS":
          return databrickService.executeStatement(databrickCommon,
            getGetAllSchemaFromCatalog(catalog));

        case "GET_TABLES":
          return databrickService.executeStatement(databrickCommon,
            getGetAllTableFromSchema(catalog, schema));

        case "GET_COLUMNS":
          return databrickService.executeStatement(databrickCommon,
            getGetAllColumnFromTable(catalog, schema, table));

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
    String httpPath = map.get("httpPath").toString();
    String catalog = map.getOrDefault("catalog", "").toString();
    String schema = map.getOrDefault("schema", "").toString();

    DatabrickCommon databrickCommon = new DatabrickCommon();
    databrickCommon.setUrl(url);
    databrickCommon.setPassword(password);
    databrickCommon.setUser(username);
    databrickCommon.setHttpPath(httpPath);

    if (!catalog.isEmpty()) {
      databrickCommon.setCatalog(catalog);
    }
    if (!schema.isEmpty()) {
      databrickCommon.setSchema(schema);
    }

    try {
      String connection = databrickService.connectDatabricks(databrickCommon);
      Map<String, Object> result = new HashMap<>();
      result.put("connection", connection);
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Connection test failed: " + e.getMessage(), e);
    }
  }
}