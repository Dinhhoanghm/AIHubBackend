package ongoing.backend.service;

import lombok.SneakyThrows;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
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
import java.util.UUID;

@Service
public class ReadFileService {

  public String convertFileToAvro(String fileUrl, String fileName) throws IOException {
    String filePath = downloadFile(fileUrl, fileName);
    return readFile(filePath);
  }

  @SneakyThrows
  public String readFile(String filePath) {
    SparkConf conf = new SparkConf().setAppName("Java Spark").setMaster("local[*]");

    SparkSession spark = SparkSession.builder()
      .config(conf)
      .getOrCreate();
    Dataset<Row> firstRow = spark.read()
      .option("header", "false")  // Don't use the first row as headers
      .csv(filePath)
      .limit(1);  // Only get the first row

    Row row = firstRow.first();
    String[] columnNames = new String[row.size()];
    for (int i = 0; i < row.size(); i++) {
      columnNames[i] = row.getString(i); // Get each value as a string
    }

    StructType schema = new StructType();
    for (String columnName : columnNames) {
      schema = schema.add(new StructField(columnName, DataTypes.StringType, true, Metadata.empty()));
    }

    Dataset<Row> df = spark.read()
      .option("header", "true")
      .schema(schema)
      .csv(filePath);
    String currentDir = System.getProperty("user.dir");
    String outputFilePath = currentDir + File.separator + filePath + "output" + UUID.randomUUID();
    df.write()
      .mode(SaveMode.Overwrite)
      .parquet(outputFilePath);

    // Stop the Spark session
    spark.stop();

    return "Parquet file created successfully";
  }


  public String downloadFile(String fileNameUrl, String fileName) throws IOException {
    ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fileNameUrl).openStream());
    String currentDir = System.getProperty("user.dir");
    String filePath = currentDir + File.separator  + File.separator + fileName + ".csv";
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
