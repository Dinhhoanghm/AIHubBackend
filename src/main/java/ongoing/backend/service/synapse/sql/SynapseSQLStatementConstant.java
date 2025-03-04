package ongoing.backend.service.synapse.sql;

public class SynapseSQLStatementConstant {

  public static String getAllDatabase() {
    return "SELECT name AS database_name FROM sys.databases ORDER BY name";
  }

  public static String getGetAllSchemaFromDatabase(String databaseName) {
    return String.format(
      "USE %s;\n" +
        "SELECT \n" +
        "    s.name AS schema_name,\n" +
        "    s.schema_id,\n" +
        "    SCHEMA_OWNER = USER_NAME(s.principal_id),\n" +
        "    s.create_date,\n" +
        "    s.modify_date\n" +
        "FROM \n" +
        "    sys.schemas s\n" +
        "ORDER BY \n" +
        "    s.name;",

      databaseName
    );
  }
  public static String getGetAllTableFromSchema(String schemaName) {
    return String.format(
        "SELECT \n" +
        "    t.name AS table_name,\n" +
        "    CASE WHEN t.is_external = 1 THEN 'EXTERNAL' ELSE 'REGULAR' END AS table_type,\n" +
        "    t.create_date,\n" +
        "    t.modify_date\n" +
        "FROM \n" +
        "    sys.tables t\n" +
        "JOIN \n" +
        "    sys.schemas s ON t.schema_id = s.schema_id\n" +
        "WHERE \n" +
        "    s.name = '%s'\n" +
        "ORDER BY \n" +
        "    t.name;",
      schemaName
    );
  }

  public static String getGetAllColumnFromTable(String schemaName, String tableName) {
    return String.format(
      "SELECT \n" +
        "    COLUMN_NAME, \n" +
        "    DATA_TYPE, \n" +
        "    CHARACTER_MAXIMUM_LENGTH, \n" +
        "    NUMERIC_PRECISION, \n" +
        "    NUMERIC_SCALE, \n" +
        "    IS_NULLABLE, \n" +
        "    COLUMN_DEFAULT \n" +
        "FROM \n" +
        "    INFORMATION_SCHEMA.COLUMNS \n" +
        "WHERE \n" +
        "    TABLE_SCHEMA = '%s' AND \n" +
        "    TABLE_NAME = '%s' \n" +
        "ORDER BY \n" +
        "    ORDINAL_POSITION;",
      schemaName, tableName
    );
  }
}