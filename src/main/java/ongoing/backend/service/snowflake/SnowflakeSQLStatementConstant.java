package ongoing.backend.service.snowflake;

public class SnowflakeSQLStatementConstant {
  public static String getGetAllColumnFromTable(String database, String schemaName, String tableName) {
    return String.format("SHOW COLUMNS IN TABLE %s.%s.%s;", database, schemaName, tableName);
  }

  public static String getGetAllTableFromSchema(String database, String schemaName) {
    return String.format("SHOW TABLES IN SCHEMA %s.%s;", database, schemaName);
  }

  public static String getGetAllSchemaFromDatabase(String database) {
    return String.format("SHOW SCHEMAS IN DATABASE %s;", database);
  }

  public static String getAllDatabase() {
    return "SHOW DATABASES;";
  }
}
