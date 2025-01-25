package ongoing.backend.data.rapidApi;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RapidEndpointData {
  private String route;
  private String method;
}
