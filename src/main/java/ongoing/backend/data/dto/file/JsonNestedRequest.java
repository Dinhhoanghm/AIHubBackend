package ongoing.backend.data.dto.file;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class JsonNestedRequest {
  private List<NestParamData> nestParams;
  private String json;
}
