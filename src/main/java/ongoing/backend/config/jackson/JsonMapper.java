package ongoing.backend.config.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ongoing.backend.config.jackson.config.JSONDeserializer;
import ongoing.backend.config.jackson.config.JSONSerializer;
import org.jooq.JSON;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE;
import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE;

@Configuration
public class JsonMapper {
  private static ObjectMapper objectMapper;

  public static ObjectMapper getObjectMapper() {
    if (objectMapper == null) new JsonMapper().resetJsonConfig();
    return objectMapper;
  }

  public static ObjectMapper getObjectMapperCamelCase() {
    ObjectMapper mapper = new ObjectMapper();
    camelCaseJsonConfig(mapper);
    return mapper;
  }


  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    if (objectMapper == null) new JsonMapper().resetJsonConfig();
    return objectMapper;
  }

  public void resetJsonConfig() {
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    objectMapper = new ObjectMapper();
    objectMapper
      .registerModule(new JavaTimeModule())
      .registerModule(JSONModule())
      .setDateFormat(df)
      .setPropertyNamingStrategy(SNAKE_CASE)
      .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(JSONModule());
  }

  public static void camelCaseJsonConfig(ObjectMapper objectMapper) {
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    objectMapper
      .registerModule(new JavaTimeModule())
      .registerModule(JSONModule())
      .setDateFormat(df)
      .setPropertyNamingStrategy(LOWER_CAMEL_CASE)
      .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(JSONModule());
  }

  public static SimpleModule JSONModule() {
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addDeserializer(JSON.class, new JSONDeserializer());
    simpleModule.addSerializer(JSON.class, new JSONSerializer());
    simpleModule.addSerializer(Date.class, new DateSerializer(false, df));
    return simpleModule;
  }
}