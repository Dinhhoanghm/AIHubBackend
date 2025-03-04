package ongoing.backend.service.synapse;

import com.aiv.datasource.IEDatasource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static ongoing.backend.service.synapse.sql.SynapseSQLStatementConstant.*;

@Service
public class SynapseDataSource implements IEDatasource {
  private final SynapseService synapseService;

  public SynapseDataSource(SynapseService synapseService) {
    this.synapseService = synapseService;
  }

  @Override
  public Map<String, Object> getData(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    String sql = map.getOrDefault("sql", "").toString();
    try {
      return synapseService.executeStatement(url, username, password, sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Object> getMetaData(Map<String, Object> map) {
    String tableName = (String) map.getOrDefault("table", "");
    String schemaName = (String) map.getOrDefault("schema", "");
    String databaseName = (String) map.getOrDefault("database", "");
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();
    try {
      return synapseService.executeStatement(url, username, password, getGetAllColumnFromTable(schemaName, tableName));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Object> getObjects(Map<String, Object> map) {
    String type = (String) map.getOrDefault("type", "");
    String tableName = (String) map.getOrDefault("table", "");
    String schemaName = (String) map.getOrDefault("schema", "");
    String databaseName = (String) map.getOrDefault("database", "");
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();

    try {
      switch (type.toUpperCase()) {
        case "GET_DATABASES":
          return synapseService.executeStatement(url, username, password, getAllDatabase());
        case "GET_SCHEMAS":
          return synapseService.executeStatement(url, username, password, getGetAllSchemaFromDatabase(databaseName));
        case "GET_TABLES":
          return synapseService.executeStatement(url, username, password, getGetAllTableFromSchema(schemaName));
        case "GET_COLUMNS":
          return synapseService.executeStatement(url, username, password, getGetAllColumnFromTable(schemaName, tableName));
        default:
          return null;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Object> testConnection(Map<String, Object> map) {
    String url = map.get("connectionUrl").toString();
    String username = map.get("username").toString();
    String password = map.get("password").toString();

    Map<String, Object> result = new HashMap<>();
    try {
      String connection = synapseService.connectSynapse(url, username, password);
      result.put("connection", connection);
      return result;
    } catch (Exception e) {
      result.put("connection", "Failed: " + e.getMessage());
      return result;
    }
  }
}