package ongoing.backend.controller;

import ongoing.backend.config.exception.ApiException;
import ongoing.backend.data.JsonNestedRequest;
import ongoing.backend.data.JsonOutput;
import ongoing.backend.service.ConvertJsonService;
import ongoing.backend.service.ReadFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class ReadFileController {
  private final ReadFileService readFileService;
  private final ConvertJsonService convertJsonService;

  public ReadFileController(ReadFileService readFileService,
                            ConvertJsonService convertJsonService) {
    this.readFileService = readFileService;
    this.convertJsonService = convertJsonService;
  }

  @GetMapping("/download")
  public ResponseEntity<String> readFile(@RequestParam("url") String url,
                                         @RequestParam("fileName") String fileName,
                                         @RequestParam("fileType") String fileType) throws IOException, ApiException {
    return ResponseEntity.ok(readFileService.convertFileToAvro(url, fileName, fileType));
  }

  @PostMapping("/convertJson")
  public ResponseEntity<JsonOutput> convertJson(@RequestBody JsonNestedRequest jsonNestedRequest) throws IOException, ApiException {
    return ResponseEntity.ok(convertJsonService.convertJsonToList(jsonNestedRequest));
  }
}
