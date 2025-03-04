package ongoing.backend.service.snowflake;

import lombok.extern.log4j.Log4j2;
import ongoing.backend.config.jackson.json.JsonObject;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

@Service
@Log4j2
public class SnowFlakeService {
  private static Connection connection;

  public String connectSnowflake(SnowFlakeCommon snowFlakeCommon) {
    try {
      connection = getDriverConnection(snowFlakeCommon);
      connection.close();
      return "Success";
    } catch (SQLException ex) {
      log.error("Error connecting to Snowflake: ", ex);
      throw new RuntimeException("Failed to connect to Snowflake", ex);
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

  public Map<String, Object> executeStatement(SnowFlakeCommon snowFlakeCommon, String sql) throws SQLException {
    connection = getDriverConnection(snowFlakeCommon);
    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

      log.info("Executing SQL: {}", sql);
      JsonObject result = new JsonObject(DSL.using(connection)
        .fetch(resultSet)
        .formatJSON());
      return result.getMap();
    }
  }

  public Connection getDriverConnection(SnowFlakeCommon snowFlakeCommon) {

    try {
      Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");

      Properties props = new Properties();
      props.setProperty("user", snowFlakeCommon.getUser());
      props.setProperty("password", snowFlakeCommon.getPassword());
      props.setProperty("warehouse", snowFlakeCommon.getWarehouse());  // Tên warehouse
      props.setProperty("db", snowFlakeCommon.getDatabase());         // Tên database
      props.setProperty("schema", snowFlakeCommon.getSchema());       // Tên schema
      props.setProperty("role", snowFlakeCommon.getRole());           // Role của user

      connection = DriverManager.getConnection(snowFlakeCommon.getUrl(), props);

    } catch (ClassNotFoundException e) {
      log.error("Snowflake JDBC driver not found: ", e);
      throw new RuntimeException("Snowflake JDBC driver not found", e);
    } catch (SQLException e) {
      log.error("Error establishing Snowflake connection: ", e);
      throw new RuntimeException("Failed to establish Snowflake connection", e);
    }

    return connection;
  }
}