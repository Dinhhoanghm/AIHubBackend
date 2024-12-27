package ongoing.backend.service.readFile;

import lombok.extern.log4j.Log4j2;
import ongoing.backend.data.dto.file.JsonOutput;
import ongoing.backend.data.dto.file.ReadOptions;
import ongoing.backend.data.response.ColumnDataResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ongoing.backend.utils.ReadExcelUtil.writeJsonToFile;


@Service
@Log4j2
public class ReadExcelService {
  private static final Integer BATCH_SIZE = 10000;
  private final FastExcelService fastExcelService;

  public ReadExcelService(FastExcelService fastExcelService) {
    this.fastExcelService = fastExcelService;
  }

  public String readExcelToJsonOutput(MultipartFile multipartFile, Integer headerDept, String sheetName) throws IOException, InvalidFormatException, XMLStreamException {
    InputStream initialStream = multipartFile.getInputStream();
    byte[] buffer = new byte[initialStream.available()];
    ReadOptions readOptions = new ReadOptions();
    readOptions.setSheetIdx(7);
    readOptions.setHeaderRowIdx(headerDept.toString());
    readOptions.setDataRowIdx(String.valueOf(headerDept + 1));
    readOptions.setSheetName(sheetName);
    String path = "/home/hoangdd/Downloads/";
    initialStream.read(buffer);
    File file = new File(path, multipartFile.getOriginalFilename());
    multipartFile.transferTo(file);
    String filePath = file.getPath();
    List<ColumnDataResponse> columnData = getColumn(file, headerDept, sheetName);
    String jsonFilePath = "output.json";
    List<Object[]> value = fastExcelService.readExcel(filePath, sheetName, headerDept )
      .stream().map(s -> s.toArray())
      .collect(Collectors.toList());
    List<Object> data = new ArrayList<>();
    data.add(columnData.toArray());
    data.add(value.toArray());
    JsonOutput jsonOutput = new JsonOutput();
    jsonOutput.setData(data.toArray());
    writeJsonToFile(jsonOutput, jsonFilePath);
    return "Successfull";
  }


  public JsonOutput readExcelToJsonOutput(MultipartFile multipartFile, Integer offset, Integer limit, Integer headerDept, String sheetName) throws IOException, InvalidFormatException {
    InputStream initialStream = multipartFile.getInputStream();
    byte[] buffer = new byte[initialStream.available()];
    initialStream.read(buffer);
    String path = "src/main/resources/targetFile.xlsx";
    File file = new File(path);
    try (OutputStream outStream = new FileOutputStream(file)) {
      outStream.write(buffer);
    }
    List<ColumnDataResponse> columnData = getColumn(file, headerDept, sheetName);
    List<Object[]> value = fastExcelService.readExcel(file.getPath(), sheetName, headerDept ).stream().map(s -> s.toArray())
      .collect(Collectors.toList());
    List<Object> data = new ArrayList<>();
    data.add(columnData.toArray());
    data.add(value.toArray());
    JsonOutput jsonOutput = new JsonOutput();
    jsonOutput.setData(data.toArray());
    Files.deleteIfExists(file.toPath());
    return jsonOutput;
  }


  public List<ColumnDataResponse> getColumn(File file, Integer headerDepth, String sheetName) throws IOException, InvalidFormatException {
    return fastExcelService.getHeader(file.getPath(), sheetName, headerDepth);
  }
}
