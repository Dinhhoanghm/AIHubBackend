package ongoing.backend.config.logback;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class LoggerConfig {
  private Map<String, String> config;


  public String get(String key) {
    return config.getOrDefault(key, "");
  }

  public boolean contains(String key) {
    return config.containsKey(key);
  }

  public Map<String, String> getAll() {
    return config;
  }


}
