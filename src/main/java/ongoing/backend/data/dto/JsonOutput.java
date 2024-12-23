package ongoing.backend.data.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JsonOutput {
  private Object[] data;
}
