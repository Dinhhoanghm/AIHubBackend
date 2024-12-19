package ongoing.backend.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class JsonOutput {
  private Object[] data;
}
