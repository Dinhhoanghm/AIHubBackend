package ongoing.backend.controller;

import ongoing.backend.config.jackson.json.JsonObject;
import ongoing.backend.service.webScraping.WebDataSource;
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
@RequestMapping("/api/webSource")
public class WebSourceDataController {
  @Autowired
  private WebDataSource webDataSource;

  @PostMapping("/data")
  public ResponseEntity<Map<String, Object>> getAllData(@RequestBody Map<String, Object> data) {
    String params = data.getOrDefault("query", "").toString();
    try {
      Map<String, Object> cart = new HashMap<String, Object>();
      if (StringUtils.isBlank(params)) {
        cart.putAll(webDataSource.getData(data));
      } else {
        Map<String, Object> mapParams = new JsonObject(params).getMap();
        cart.putAll(webDataSource.getData(mapParams));
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
        cart.putAll(webDataSource.getMetaData(data));
      } else {
        Map<String, Object> mapParams = new JsonObject(params).getMap();
        cart.putAll(webDataSource.getMetaData(mapParams));
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
      return new ResponseEntity<>(webDataSource.testConnection(data), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/getobjects")
  public ResponseEntity<Map<String, Object>> getObjects(@RequestBody Map<String, Object> data) {
    try {
      Map<String, Object> response = webDataSource.getObjects(data);
      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
