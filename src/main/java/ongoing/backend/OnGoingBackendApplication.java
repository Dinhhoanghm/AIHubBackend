package ongoing.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ongoing.backend")
public class OnGoingBackendApplication {
  public static void main(String[] args) {
    SpringApplication.run(OnGoingBackendApplication.class, args);
  }
}