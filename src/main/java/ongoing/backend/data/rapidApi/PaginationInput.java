package ongoing.backend.data.rapidApi;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaginationInput {
  private Integer first;
  private String after="";
}
