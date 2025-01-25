package ongoing.backend.data.rapidApi;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SortFields {

  private String fieldName;
  private String by; // ASC, DES
}
