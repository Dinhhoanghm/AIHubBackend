package ongoing.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication(scanBasePackages = "ongoing.backend")
@EnableWebSocketMessageBroker
@EnableWebSocket
public class OnGoingBackendApplication {
  public static void main(String[] args) {
    SpringApplication.run(OnGoingBackendApplication.class, args);
  }
}