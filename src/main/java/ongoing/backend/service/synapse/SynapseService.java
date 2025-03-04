package ongoing.backend.service.synapse;

import lombok.extern.log4j.Log4j2;
import ongoing.backend.config.jackson.json.JsonObject;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

@Service
@Log4j2
public class SynapseService {
  private static Connection connection;

  public String connectSynapse(String connectionUrl, String userName, String password) {
    try {
      connection = getDriverConnection(connectionUrl, userName, password);
      connection.close();
      return "Success";
    } catch (SQLException ex) {
      log.error("Failed to connect to Azure Synapse: ", ex);
      return "Failed: " + ex.getMessage();
    } finally {
      try {
        if (connection != null)
          connection.close();
          connection = null;
      } catch (SQLException ex) {
        log.error("Failed to close connection: ", ex);
      }
    }
  }

  public Map<String, Object> executeStatement(String connectionUrl, String userName, String password, String sql) throws SQLException {
    connection = getDriverConnection(connectionUrl, userName, password);
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
    log.info("Executing SQL query: {}", sql);
    JsonObject result = new JsonObject(DSL.using(connection)
      .fetch(resultSet)
      .formatJSON());
    if (connection != null)
      connection.close();
    connection = null;
    return result.getMap();
  }

  public Connection getDriverConnection(String connectionUrl, String userName, String password) {
    if (connection == null) {
      try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        Properties props = new Properties();
        props.setProperty("user", userName);
        props.setProperty("password", password);
        connection = DriverManager.getConnection(connectionUrl, props);
      } catch (ClassNotFoundException e) {
        log.error("JDBC Driver not found: ", e);
      } catch (SQLException sq) {
        log.error("SQLException during connection: ", sq);
      }
    }
    return connection;
  }

  public String buildConnectionUrl(String server, String database) {
    return String.format(
      "jdbc:sqlserver://%s.sql.azuresynapse.net:1433;database=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
      server, database
    );
  }
  public void closeConnection() {
    if (connection != null) {
      try {
        connection.close();
        connection = null;
      } catch (SQLException ex) {
        log.error("Error closing connection: ", ex);
      }
    }
  }
}