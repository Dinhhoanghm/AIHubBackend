package ongoing.backend.service.dataEndpoint;

import com.aiv.datasource.IEDatasource;
import ongoing.backend.config.exception.ApiException;
import ongoing.backend.config.jackson.json.JsonObject;
import ongoing.backend.data.rapidApi.CategoryRequest;
import ongoing.backend.service.rapidApi.RapidApiService;
import ongoing.backend.service.readFile.ConvertJsonService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ongoing.backend.config.jackson.json.Json.encodeCamelCase;

@Service
public class DataEndpointDataSource implements IEDatasource {
  private final RapidApiService rapidApiService;
  private final ConvertJsonService convertJsonService;

  public DataEndpointDataSource(RapidApiService rapidApiService,
                                ConvertJsonService convertJsonService) {
    this.rapidApiService = rapidApiService;

    this.convertJsonService = convertJsonService;
  }

  @Override
  public Map<String, Object> getData(Map<String, Object> data) {
    String endpoint = data.getOrDefault("endpoint", "").toString();
    String slugName = data.getOrDefault("slugName", "").toString();
    String params = data.getOrDefault("params", "").toString();
    String nestParams = data.getOrDefault("nestParams", "").toString();
    try {
      return rapidApiService.getRapidDataResponse(endpoint,slugName,params,nestParams);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Object> getMetaData(Map<String, Object> data) {
    String endpoint = data.getOrDefault("endpoint", "").toString();
    String slugName = data.getOrDefault("slugName", "").toString();
    String params = data.getOrDefault("params", "").toString();
    String nestParams = data.getOrDefault("nestParams", "").toString();
    try {
      return rapidApiService.getRapidMetaDataResponse(endpoint, slugName, params, nestParams);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Object> testConnection(Map<String, Object> map) {
    try {
      Map<String, Object> cart = new HashMap<String, Object>();
      cart.put("message", "Connection Successfully.");
      return cart;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Map<String, Object> getObjects(Map<String, Object> map) {
    Map<String, Object> result = new HashMap<>();
    try {
      rapidApiService.getRapidCategories(new JsonObject(map).mapToCamelCase(CategoryRequest.class))
        .forEach(
          category -> {
            Map<String, Object> data = new JsonObject(encodeCamelCase(category)).getMap();
            result.put(category.getApiSlug(), data);
          }
        );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
