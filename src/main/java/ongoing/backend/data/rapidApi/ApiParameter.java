package ongoing.backend.data.rapidApi;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApiParameter {
  private String name;
  private String paramType;
  private String condition;
}
