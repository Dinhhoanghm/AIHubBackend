package ongoing.backend.data.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NestParamData {
  private String nestString;
  private String columnName;
  private String dataType;

  public String getColumnName() {
    String[] elements = this.nestString.split("\\.");
    if (elements.length == 0) {
      return this.columnName;
    } else {
      return elements[elements.length - 1];
    }
  }
}
