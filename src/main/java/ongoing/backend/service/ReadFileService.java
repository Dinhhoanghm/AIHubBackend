package ongoing.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ongoing.backend.config.exception.ApiException;
import ongoing.backend.config.jackson.json.JsonArray;
import ongoing.backend.config.jackson.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ongoing.backend.config.jackson.json.Json.encode;

@Service
public class ReadFileService {

  public String convertFileToAvro(String fileUrl, String fileName, String fileType) throws IOException, ApiException {
    String filePath = downloadFile(fileUrl, fileName, fileType);
    return readFile(filePath);
  }

  @SneakyThrows
  public String readFile(String filePath) {
    SparkConf conf = new SparkConf().setAppName("Java Spark").setMaster("local[*]");

    SparkSession spark = SparkSession.builder()
      .config(conf)
      .getOrCreate();
    Dataset<Row> df = null;

    if (filePath.endsWith(".csv")) {

      Dataset<Row> firstRow = spark.read()
        .option("header", "false")
        .csv(filePath)
        .limit(1);

      Row row = firstRow.first();
      String[] columnNames = new String[row.size()];
      for (int i = 0; i < row.size(); i++) {
        columnNames[i] = row.getString(i);
      }

      StructType schema = new StructType();
      for (String columnName : columnNames) {
        schema = schema.add(new StructField(columnName, DataTypes.StringType, true, Metadata.empty()));
      }

      df = spark.read()
        .option("header", "true")
        .schema(schema)
        .csv(filePath);
    }
    if (filePath.endsWith(".xml")) {
      df = spark.read()
        .format("xml")
        .option("rowTag", "row")
        .option("attributePrefix", "_")
        .load(filePath);


      df = df.selectExpr("explode(row) as nestedRow")
        .select("nestedRow.*");

    }
    if (filePath.endsWith(".json")) {
      ObjectMapper objectMapper = new ObjectMapper();
      String content = encode(objectMapper.readValue(new File(filePath), Object.class));
      JsonObject data = new JsonObject(content);
      JsonObject jsonObject = data
        .getJsonObject("meta")
        .getJsonObject("view");
      List<Object> object = jsonObject.getJsonArray("columns")
        .getList();
      List<String> columnNames = object
        .stream()
        .map(s -> new JsonObject(encode(s)).getString("name"))
        .collect(Collectors.toList());
      StructType schema = new StructType();
      for (String columnName : columnNames) {
        schema = schema.add(new StructField(columnName, DataTypes.StringType, true, Metadata.empty()));
      }
      JsonArray value = data.getJsonArray("data");
      List<Row> dataList = new ArrayList<>();
      List<List<?>> dataObjects = value.getList();

      for ( List<?> dataObject : dataObjects) {
        dataObject = dataObject.stream().map(s -> encode(s)).collect(Collectors.toList());
        dataList.add(RowFactory.create(dataObject.toArray()));
      }
      df = spark.createDataFrame(dataList, schema);
    }

    String outputFilePath = "/home/hoangdd/Downloads/output";

    df.write()
      .mode(SaveMode.Overwrite)
      .parquet(outputFilePath);

    spark.stop();

    return "Parquet file created successfully";

  }


  public String downloadFile(String fileNameUrl, String fileName, String fileType) throws IOException, ApiException {
    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fileNameUrl).openStream());
    String currentDir = System.getProperty("user.dir");
    String fileEndPoint = null;
    if (fileType.equalsIgnoreCase("csv")) {
      fileEndPoint = ".csv";
    }
    if (fileType.equalsIgnoreCase("xml")) {
      fileEndPoint = ".xml";
    }
    if (fileType.equalsIgnoreCase("json")) {
      fileEndPoint = ".json";
    }
    if (fileType.equalsIgnoreCase("xls")) {
      fileEndPoint = ".xls";
    }
    if (StringUtils.isBlank(fileType)) {
      throw new ApiException("File Type Must Be Defined Before Download");
    }
    String filePath = currentDir + File.separator + fileName + fileEndPoint;
    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
         FileChannel fileChannel = fileOutputStream.getChannel()) {

      // Use a buffer with a larger size to improve the download speed
      ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024); // 1MB buffer

      // Transfer data from the URL to the file in chunks
      while (readableByteChannel.read(buffer) != -1) {
        buffer.flip(); // Prepare buffer for writing to the file
        fileChannel.write(buffer);
        buffer.clear(); // Clear buffer for the next read
      }
    } finally {
      // Ensure the readableByteChannel is closed
      readableByteChannel.close();
    }
    return filePath;
  }
}
