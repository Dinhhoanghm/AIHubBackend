package ongoing.backend.service.databrick;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DatabrickCommon {
  private String url;
  private String user;
  private String password;
  private String httpPath;
  private String catalog;
  private String schema;
}
