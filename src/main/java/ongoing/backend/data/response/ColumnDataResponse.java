package ongoing.backend.data.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ColumnDataResponse {
  private String alias;
  private String type;
  private String key;
}
