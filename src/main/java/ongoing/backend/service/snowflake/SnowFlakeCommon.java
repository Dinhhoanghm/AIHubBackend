package ongoing.backend.service.snowflake;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SnowFlakeCommon {
  private String url;
  private String user;
  private String password;
  private String warehouse;
  private String database;
  private String schema;
  private String role;
}
