package ongoing.backend.service.log;

import ongoing.backend.config.websocket.WebSocketTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LogsWebSocketService {
  private static final Logger log = LoggerFactory.getLogger(LogsWebSocketService.class);

  private static final String TOPIC = "logs";
  private final WebSocketTemplate template;
  private static final AtomicLong COUNTER = new AtomicLong(0);

  public LogsWebSocketService(WebSocketTemplate template, MonitoringFileService monitoringFileService) {
    this.template = template;
    monitoringFileService.listen(file -> {
      try {
        Files.lines(file)
          .skip(COUNTER.get())
          .forEach(line -> {
            COUNTER.incrementAndGet();
            template.broadcast(TOPIC, line);
          });
      } catch (IOException e) {
        log.error("Error processing file changes", e);
      }
    });
  }
}
