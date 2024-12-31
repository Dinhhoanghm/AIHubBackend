package ongoing.backend;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@SpringBootApplication(scanBasePackages = "ongoing.backend")
@EnableWebSocketMessageBroker
@EnableWebSocket
public class OnGoingBackendApplication {
  public static Map<Path, Integer> fileLatestLineIndexMap = new ConcurrentHashMap<>();

  @SneakyThrows
  public static void initialize(String folderPath) {
    try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
      paths.filter(Files::isRegularFile).forEach(filePath -> {
        try {
          long totalLines = Files.lines(filePath).count();
          fileLatestLineIndexMap.put(filePath, (int) totalLines);
        } catch (IOException e) {
          System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
        }
      });
    } catch (IOException e) {
      System.err.println("Error walking through folder: " + folderPath + " - " + e.getMessage());
    }
  }


  public static void main(String[] args) {
    String folderPath = "/home/hoangdd/aiv";
    initialize(folderPath);
    SpringApplication.run(OnGoingBackendApplication.class, args);
  }
}