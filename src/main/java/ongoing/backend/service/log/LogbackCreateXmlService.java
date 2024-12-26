package ongoing.backend.service.log;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ongoing.backend.config.logback.LoggerConfig;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class LogbackCreateXmlService {

  private static final String DEFAULT_LOGBACK_FILE = "src/main/resources/logback.xml";

  public String generateLogbackConfig(LoggerConfig config) throws IOException {
    String logbackXmlContent = generateLogbackXmlContent(config);

    String logbackFilePath = DEFAULT_LOGBACK_FILE;
    try (FileWriter writer = new FileWriter(logbackFilePath)) {
      writer.write(logbackXmlContent);
      System.out.println("Logback configuration generated at: " + Paths.get(logbackFilePath).toAbsolutePath());
    }
    reloadLogbackConfiguration(logbackFilePath);

    System.out.println("Logback configuration reloaded successfully!");
    return logbackFilePath;
  }

  private String generateLogbackXmlContent(LoggerConfig config) {
    StringBuilder xmlBuilder = new StringBuilder();

    xmlBuilder.append("<configuration>\n");

    String appenderType = config.get("appenderType");
    if ("console".equalsIgnoreCase(appenderType)) {
      xmlBuilder.append(generateConsoleAppender(config));
    } else if ("file".equalsIgnoreCase(appenderType)) {
      xmlBuilder.append(generateFileAppender(config));
    } else {
      throw new IllegalArgumentException("Unsupported appender type: " + appenderType);
    }

    Map<String, String> configMap = config.getConfig();
    String logLevel = configMap.getOrDefault("logLevel", "INFO");
    xmlBuilder.append("  <root level=\"").append(logLevel).append("\">\n");
    xmlBuilder.append("    <appender-ref ref=\"").append(appenderType.toUpperCase()).append("\" />\n");
    xmlBuilder.append("  </root>\n");
    xmlBuilder.append("</configuration>\n");

    return xmlBuilder.toString();
  }

  private String generateConsoleAppender(LoggerConfig config) {
    StringBuilder builder = new StringBuilder();

    builder.append("  <appender name=\"CONSOLE\" class=\"ch.qos.logback.core.ConsoleAppender\">\n");

    Map<String, String> configMap = config.getConfig();
    if ("json".equalsIgnoreCase(config.get("format"))) {
      builder.append(generateJsonEncoder(config));
    } else {
      builder.append("    <encoder>\n")
        .append("      <pattern>").append(configMap.getOrDefault("pattern", "%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n")).append("</pattern>\n")
        .append("    </encoder>\n");
    }

    builder.append("  </appender>\n");
    return builder.toString();
  }

  private String generateFileAppender(LoggerConfig config) {
    StringBuilder builder = new StringBuilder();

    builder.append("  <appender name=\"FILE\" class=\"ch.qos.logback.core.FileAppender\">\n")
      .append("    <file>").append(config.get("filePath")).append("</file>\n");
    Map<String, String> configMap = config.getConfig();

    if ("json".equalsIgnoreCase(config.get("format"))) {
      builder.append(generateJsonEncoder(config));
    } else {
      builder.append("    <encoder>\n")
        .append("      <pattern>").append(configMap.getOrDefault("pattern", "%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n")).append("</pattern>\n")
        .append("    </encoder>\n");
    }

    builder.append("  </appender>\n");
    return builder.toString();
  }

  private String generateJsonEncoder(LoggerConfig config) {
    StringBuilder builder = new StringBuilder();

    builder.append("    <encoder class=\"net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder\">\n")
      .append("      <providers>\n");

    for (Map.Entry<String, String> entry : config.getAll().entrySet()) {
      if (entry.getKey().startsWith("json.")) {
        String jsonField = entry.getKey().substring(5);
        if ("level".equalsIgnoreCase(jsonField)) {
          builder.append("        <logLevel>\n") // Correct provider name
            .append("          <fieldName>").append(entry.getValue()).append("</fieldName>\n")
            .append("        </logLevel>\n");
        } else {
          builder.append("        <").append(jsonField).append(">\n")
            .append("          <fieldName>").append(entry.getValue()).append("</fieldName>\n")
            .append("        </").append(jsonField).append(">\n");
        }
      }
    }

    builder.append("      </providers>\n")
      .append("    </encoder>\n");

    return builder.toString();
  }

  public void reloadLogbackConfiguration(String logbackFilePath) {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    context.getStatusManager().clear();
    context.reset();

    try {
      configurator.doConfigure(logbackFilePath);
      StatusManager statusManager = context.getStatusManager();
      for (Status status : statusManager.getCopyOfStatusList()) {
        if (status.getLevel() == Status.ERROR) {
          throw new RuntimeException("Logback configuration has errors: " + status.getMessage(), status.getThrowable());
        }
      }

    } catch (Exception e) {
      System.err.println("Failed to reload Logback configuration: " + e.getMessage());
      throw new RuntimeException("Failed to reload Logback configuration.", e);
    }
  }
}
