package ongoing.backend.controller;

import ongoing.backend.config.exception.ApiException;
import ongoing.backend.config.logback.LoggerConfig;
import ongoing.backend.service.log.LogbackCreateXmlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/log")
public class CreateLoggerController {
  private final LogbackCreateXmlService logbackCreateXmlService;

  public CreateLoggerController(LogbackCreateXmlService logbackCreateXmlService) {
    this.logbackCreateXmlService = logbackCreateXmlService;
  }

  @PostMapping("/create/logxml")
  public ResponseEntity<String> createLog(@RequestBody LoggerConfig loggerConfig) throws IOException, ApiException {
    String logfilePath =  logbackCreateXmlService.generateLogbackConfig(loggerConfig);
    logbackCreateXmlService.reloadLogbackConfiguration(logfilePath);
    return ResponseEntity.ok(logfilePath);
  }
}
