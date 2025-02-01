package ongoing.backend.controller;

import ongoing.backend.config.exception.ApiException;
import ongoing.backend.service.dataEndpoint.DataEndpointDataSource;
import ongoing.backend.service.rapidApi.RapidApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class DataEndpointController {
  private final DataEndpointDataSource dataSource;
  private final RapidApiService rapidApiService;

  public DataEndpointController(DataEndpointDataSource dataSource, RapidApiService rapidApiService) {
    this.dataSource = dataSource;
    this.rapidApiService = rapidApiService;
  }

  @PostMapping("/data")
  public ResponseEntity<Map<String, Object>> getAllData(@RequestBody Map<String, Object> data) {
    String endpoint = data.getOrDefault("endpoint", "").toString();
    String slugName = data.getOrDefault("slugName", "").toString();
    String params = data.getOrDefault("params", "").toString();
    String nestParams = data.getOrDefault("nestParams", "").toString();
    try {
      Map<String, Object> cart = new HashMap<String, Object>();
      cart.putAll(dataSource.getData(data));
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
      cart.putAll(dataSource.getMetaData(data));

      if (cart.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(cart, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/extraInfo")
  public ResponseEntity<Map<String, Object>> getApiExtraInfo(@RequestBody Map<String, Object> data) {
    try {
      Map<String, Object> cart = new HashMap<String, Object>();
      cart.putAll(dataSource.getDataApiExtraInfo(data));

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
      return new ResponseEntity<>(dataSource.testConnection(data), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/getobjects")
  public ResponseEntity<Map<String, Object>> getObjects(@RequestBody Map<String, Object> data) {
    try {
      Map<String, Object> response = dataSource.getObjects(data);
      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/getResponse")
  public ResponseEntity<Map<String, Object>> getObjects(@RequestParam("endpoint") String endPoint,
                                                        @RequestParam("slugName") String slugName,
                                                        @RequestParam("params") String params,
                                                        @RequestParam("nestParams") String nestParams
  ) throws IOException, ApiException {
    return ResponseEntity.ok(rapidApiService.getRapidDataResponse(endPoint, slugName, params, nestParams));
  }
}
