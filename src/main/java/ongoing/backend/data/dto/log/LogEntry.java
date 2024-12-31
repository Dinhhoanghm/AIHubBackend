package ongoing.backend.data.dto.log;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LogEntry {
  private final String filePath;
  private final String line;

  public LogEntry(String filePath, String line) {
    this.filePath = filePath;
    this.line = line;
  }


  @Override
  public String toString() {
    return "LogEntry{" +
      "filePath=" + filePath +
      ", line='" + line + '\'' +
      '}';
  }
}
