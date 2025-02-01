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
  public static void main(String[] args) {
    String folderPath = "/home/hoangdd/aiv";
    SpringApplication.run(OnGoingBackendApplication.class, args);
  }
}