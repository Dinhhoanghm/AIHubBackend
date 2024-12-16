package ongoing.backend.controller;

import ongoing.backend.service.ReadFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class ReadFileController {
  private final ReadFileService readFileService;

  public ReadFileController(ReadFileService readFileService) {
    this.readFileService = readFileService;
  }

  @GetMapping("/download")
  public ResponseEntity<String> readFile(@RequestParam("url") String url,
                                         @RequestParam("fileName") String fileName) throws IOException {
    return ResponseEntity.ok(readFileService.convertFileToAvro(url, fileName));
  }
}
