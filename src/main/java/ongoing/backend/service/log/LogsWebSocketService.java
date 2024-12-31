package ongoing.backend.service.log;

import ongoing.backend.config.websocket.WebSocketTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LogsWebSocketService {
  private static final Logger log = LoggerFactory.getLogger(LogsWebSocketService.class);

  private static final String TOPIC = "logs";
  private final WebSocketTemplate template;
  private final Map<Path, AtomicLong> fileOffsets = new ConcurrentHashMap<>();
  private final AtomicReference<String> keyword = new AtomicReference<>("error"); // Default keyword

  public LogsWebSocketService(WebSocketTemplate template, MonitoringFileService monitoringFileService) {
    this.template = template;
    monitoringFileService.listen(this::initializeAndProcessFileChange);
  }

  public void setKeyword(String newKeyword) {
    keyword.set(newKeyword);
    log.info("Log keyword updated to: {}", newKeyword);
  }


  private void initializeAndProcessFileChange(Path file) {
    fileOffsets.computeIfAbsent(file, f -> {
      try {
        long initialOffset = Files.lines(f).count();
        return new AtomicLong(initialOffset);
      } catch (IOException e) {
        return new AtomicLong(0);
      }
    });
    processFileChange(file);
  }

  private void processFileChange(Path file) {
    AtomicLong counter = fileOffsets.get(file);

    try {
      long currentOffset = counter.get();
      long linesCount = Files.lines(file).count();

      if (linesCount > currentOffset) {
        Files.lines(file)
          .skip(currentOffset)
          .forEach(line -> {
            long lineNumber = counter.incrementAndGet();
            if (line.toLowerCase().contains(keyword.get())) {
              String message = String.format("File: %s, Line: %d, Content: %s", file.toString(), lineNumber, line);
              template.broadcast(TOPIC, message); // Broadcast the log message
            }
          });
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
