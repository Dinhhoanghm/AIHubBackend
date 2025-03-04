package ongoing.backend.service.databrick.sql;

public class DatabrickSQLStatementConstant {

  public static String getAllCatalogs() {
    return "SHOW CATALOGS;";
  }

  public static String getGetAllSchemaFromCatalog(String catalog) {
    return String.format("SHOW SCHEMAS IN %s;", catalog);
  }

  public static String getGetAllTableFromSchema(String catalog, String schema) {
    return String.format("SHOW TABLES IN %s.%s;", catalog, schema);
  }

  public static String getGetAllColumnFromTable(String catalog, String schema, String table) {
    return String.format("DESCRIBE TABLE %s.%s.%s;", catalog, schema, table);
  }
}