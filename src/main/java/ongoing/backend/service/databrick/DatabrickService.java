package ongoing.backend.service.databrick;

import lombok.extern.log4j.Log4j2;
import ongoing.backend.config.jackson.json.JsonObject;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

@Service
@Log4j2
public class DatabrickService {
  private static Connection connection;

  public String connectDatabricks(DatabrickCommon databricksCommon) {
    try {
      connection = getDriverConnection(databricksCommon);
      connection.close();
      return "Success";
    } catch (SQLException ex) {
      log.error("Error connecting to Databricks: ", ex);
      throw new RuntimeException("Failed to connect to Databricks", ex);
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        log.error("Error closing connection: ", ex);
      }
    }
  }

  public Map<String, Object> executeStatement(DatabrickCommon databricksCommon, String sql) throws SQLException {
    connection = getDriverConnection(databricksCommon);
    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

      log.info("Executing SQL: {}", sql);
      JsonObject result = new JsonObject(DSL.using(connection)
        .fetch(resultSet)
        .formatJSON());
      return result.getMap();
    }
  }

  public Connection getDriverConnection(DatabrickCommon databricksCommon) {
    try {
      Class.forName("com.databricks.client.jdbc.Driver");

      Properties props = new Properties();
      props.setProperty("user", databricksCommon.getUser());
      props.setProperty("password", databricksCommon.getPassword());
      props.setProperty("httpPath", databricksCommon.getHttpPath());
      props.setProperty("AuthMech", "3"); // 3 is for username/password authentication
      connection = DriverManager.getConnection(databricksCommon.getUrl(), props);

    } catch (ClassNotFoundException e) {
      log.error("Databricks JDBC driver not found: ", e);
      throw new RuntimeException("Databricks JDBC driver not found", e);
    } catch (SQLException e) {
      log.error("Error establishing Databricks connection: ", e);
      throw new RuntimeException("Failed to establish Databricks connection", e);
    }
    return connection;
  }
}