package ongoing.backend.data.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import static ongoing.backend.config.jackson.json.Json.encode;

@Data
@Accessors(chain = true)
public class JsonOutput {
  private Object[] data;

  @Override
  public String toString() {
    return encode(data);
  }
}