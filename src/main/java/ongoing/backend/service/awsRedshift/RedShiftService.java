package ongoing.backend.service.awsRedshift;

import lombok.extern.log4j.Log4j2;
import ongoing.backend.config.jackson.json.JsonObject;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

@Service
@Log4j2
public class RedShiftService {
  private static Connection connection;

  public String connectRedshift(String connectionUrl, String userName, String password) throws ClassNotFoundException, SQLException {
    Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
    try {
      connection = getDriverConnection(connectionUrl, userName, password);
      connection.close();
      return "Success";
    } catch (SQLException ex) {
      ex.printStackTrace();
      System.exit(1);
    } finally {
      try {
        if (connection != null)
          connection.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
        System.exit(1);
      }
    }
    return "Success";
  }

  public Map<String, Object> executeStatement(String connectionUrl, String userName, String password, String sql) throws SQLException {
    connection = getDriverConnection(connectionUrl, userName, password);
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
    log.info("Printing result set : ", resultSet);
    JsonObject result = new JsonObject(DSL.using(connection)
      .fetch(resultSet)
      .formatJSON());
    return result.getMap();
  }

  public Connection getDriverConnection(String connectionUrl, String userName, String password) {
    if (connection == null) {
      try {
        Class.forName("com.amazon.redshift.jdbc42.Driver");
        String jdbcURL = connectionUrl;
        Properties props = new Properties();

        props.setProperty("user", userName);
        props.setProperty("password", password);
        connection = DriverManager.getConnection(jdbcURL);

      } catch (ClassNotFoundException e) {
        System.err.println("ClassNotFoundException: " + e.getMessage());
      } catch (SQLException sq) {
        System.err.println("SQLException: " + sq.getMessage());
      }
    }
    return connection;
  }
}
