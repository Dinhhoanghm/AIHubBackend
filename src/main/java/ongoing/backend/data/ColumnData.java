package ongoing.backend.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ColumnData {
  private String alias;
  private String type;
  private String key;
  private List<Object> data;
}
