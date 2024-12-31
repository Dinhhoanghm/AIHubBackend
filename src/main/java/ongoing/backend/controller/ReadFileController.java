package ongoing.backend.controller;

import lombok.extern.slf4j.Slf4j;
import ongoing.backend.config.exception.ApiException;
import ongoing.backend.data.dto.file.JsonNestedRequest;
import ongoing.backend.data.dto.file.JsonOutput;
import ongoing.backend.service.readFile.ConvertJsonService;
import ongoing.backend.service.readFile.ReadExcelService;
import ongoing.backend.service.readFile.ReadFileService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

import static ongoing.backend.config.logback.LoggerUtility.logInfo;

@Slf4j
@RestController
@RequestMapping("/file")
public class ReadFileController {
  private final ReadFileService readFileService;
  private final ConvertJsonService convertJsonService;
  private final ReadExcelService readExcelService;

  public ReadFileController(ReadFileService readFileService,
                            ConvertJsonService convertJsonService, ReadExcelService readExcelService) {
    this.readFileService = readFileService;
    this.convertJsonService = convertJsonService;
    this.readExcelService = readExcelService;
  }

  @GetMapping("/download")
  public ResponseEntity<String> readFile(@RequestParam("url") String url,
                                         @RequestParam("fileName") String fileName,
                                         @RequestParam("fileType") String fileType) throws IOException, ApiException {
    return ResponseEntity.ok(readFileService.convertFileToAvro(url, fileName, fileType));
  }

  @PostMapping("/convertJson")
  public ResponseEntity<JsonOutput> convertJson(@RequestBody JsonNestedRequest jsonNestedRequest) throws IOException, ApiException {
    logInfo("Error");
    Files.write(Path.of("/home/hoangdd/aiv/data.log"), Collections.singletonList("New Line Content"), StandardOpenOption.APPEND);
    return ResponseEntity.ok(convertJsonService.convertJsonToList(jsonNestedRequest));
  }

  @PostMapping("/convert/excel")
  public ResponseEntity<JsonOutput> convertExcel(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("offset") Integer offset,
                                                 @RequestParam("limit") Integer limit,
                                                 @RequestParam(value = "headerDepth", defaultValue = "1") Integer headerDepth,
                                                 @RequestParam(value = "sheetName", defaultValue = "") String sheetName
  ) throws IOException, ApiException, InvalidFormatException {
    return ResponseEntity.ok(readExcelService.readExcelToJsonOutput(file, offset, limit, headerDepth, sheetName));
  }

  @PostMapping(value = "/convert/json")
  public ResponseEntity<String> convertToJson(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "headerDepth", defaultValue = "1") Integer headerDepth,
                                              @RequestParam(value = "sheetName", defaultValue = "") String sheetName
  ) throws IOException, ApiException, InvalidFormatException, XMLStreamException {
    return ResponseEntity.ok(readExcelService.readExcelToJsonOutput(file, headerDepth, sheetName));
  }
}
