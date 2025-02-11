package ongoing.backend.service.awsRedshift.sql;

public class RedshiftSQLStatementConstant {
  public static String getGetAllColumnFromTable(String database, String schemaName, String tableName) {
    return String.format("SHOW COLUMNS FROM TABLE %s.%s.%s;", database, schemaName, tableName);
  }

  public static String getGetAllTableFromSchema(String database, String schemaName) {
    return String.format("SHOW TABLES FROM %s.%s;", database, schemaName);
  }

  public static String getGetAllSchemaFromDatabase(String database) {
    return String.format("SHOW SCHEMAS FROM %s;", database);
  }

  public static String getAllDatabase() {
    return "SHOW DATABASES;";
  }
}
