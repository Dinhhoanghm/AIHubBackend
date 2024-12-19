package ongoing.backend.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class NestParamData {
  private String nestString;
  private String columnName;
  private String dataType;

}
