package ongoing.backend.data.dto.file;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReadOptions {
  private String headerRowIdx = "1";
  private String dataRowIdx = "2";
  private int sheetIdx;
  private String sheetName;
  private boolean skipNull = true;
}
