package ongoing.backend.controller;


import ongoing.backend.service.log.LogsWebSocketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/keyword")
public class ChangeKeywordController {
  private final LogsWebSocketService logsWebSocketService;

  public ChangeKeywordController(LogsWebSocketService logsWebSocketService) {
    this.logsWebSocketService = logsWebSocketService;
  }

  @PostMapping
  public void updateKeyword(@RequestBody String newKeyword) {
    logsWebSocketService.setKeyword(newKeyword);
  }
}
