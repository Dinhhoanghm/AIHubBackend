package ongoing.backend.controller;

import ongoing.backend.config.jackson.json.JsonObject;
import ongoing.backend.service.awsRedshift.AwsRedshiftDataSource;
import ongoing.backend.service.snowflake.SnowFlakeDatasource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/redshift")
public class RedshiftController {
  @Autowired
  private AwsRedshiftDataSource redShiftDatasource;

  @PostMapping("/data")
  public ResponseEntity<Map<String, Object>> getAllData(@RequestBody Map<String, Object> data) {
    String params = data.getOrDefault("query", "").toString();
    try {
      Map<String, Object> cart = new HashMap<String, Object>();
      if (StringUtils.isBlank(params)) {
        cart.putAll(redShiftDatasource.getData(data));
      } else {
        Map<String, Object> mapParams = new JsonObject(params).getMap();
        cart.putAll(redShiftDatasource.getData(mapParams));
      }
      if (cart.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(cart, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/metadata")
  public ResponseEntity<Map<String, Object>> getAllMetadata(@RequestBody Map<String, Object> data) {
    try {
      Map<String, Object> cart = new HashMap<String, Object>();
      String params = data.getOrDefault("query", "").toString();
      if (StringUtils.isBlank(params)) {
        cart.putAll(redShiftDatasource.getMetaData(data));
      } else {
        Map<String, Object> mapParams = new JsonObject(params).getMap();
        cart.putAll(redShiftDatasource.getMetaData(mapParams));
      }
      if (cart.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(cart, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @PostMapping("/connection")
  public ResponseEntity<Map<String, Object>> getConnection(@RequestBody Map<String, Object> data) {
    try {
      return new ResponseEntity<>(redShiftDatasource.testConnection(data), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/getobjects")
  public ResponseEntity<Map<String, Object>> getObjects(@RequestBody Map<String, Object> data) {
    try {
      Map<String, Object> response = redShiftDatasource.getObjects(data);
      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
