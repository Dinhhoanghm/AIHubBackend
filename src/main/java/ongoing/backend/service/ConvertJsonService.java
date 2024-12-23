package ongoing.backend.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ongoing.backend.config.exception.ApiException;
import ongoing.backend.data.dto.ColumnData;
import ongoing.backend.data.dto.JsonNestedRequest;
import ongoing.backend.data.dto.JsonOutput;
import ongoing.backend.data.dto.NestParamData;
import ongoing.backend.data.mapper.ColumnDataMapper;
import ongoing.backend.data.response.ColumnDataResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConvertJsonService {
  private final ColumnDataMapper columnDataMapper;

  public ConvertJsonService(ColumnDataMapper columnDataMapper) {
    this.columnDataMapper = columnDataMapper;
  }

  public JsonOutput convertJsonToList(JsonNestedRequest jsonNestedRequest) throws ApiException {
    List<NestParamData> nestParams = jsonNestedRequest.getNestParams();
    DocumentContext jsonContext = JsonPath.parse(jsonNestedRequest.getJson());
    List<Object> data = new ArrayList<>();
    List<ColumnData> columnDataToHandle = new ArrayList<>();
    List<ColumnDataResponse> columnData = new ArrayList<>();
    int size = 0;
    for (NestParamData nestParamData : nestParams) {
      ColumnData column = new ColumnData().setAlias(nestParamData.getColumnName())
        .setKey(nestParamData.getColumnName());
      List<Object> value = null;
      try {
        Object val = jsonContext.read(nestParamData.getNestString());
        if (val instanceof List) {
          value = (List<Object>) val;
        } else {
          value = Collections.singletonList(val);
        }
      } catch (Exception e) {
        throw new ApiException(String.format("Failed to parse json %s", nestParamData.getNestString()));
      }
      if (value != null) {
        if (value.size() > size) {
          size = value.size();
        }
        column.setData(value);
      }
      String dataType = determineDataType(value);
      column.setType(dataType);
      columnData.add(columnDataMapper.toColumnDataResponse(column));
      columnDataToHandle.add(column);
    }
    data.add(columnData.toArray());
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        List<Object> dataElement = new ArrayList<>();
        for (ColumnData column : columnDataToHandle) {
          if (column.getData().size() > i) {
            dataElement.add(column.getData().get(i));
          } else {
            dataElement.add(null);
          }
        }
        data.add(dataElement.toArray());
      }
    }
    JsonOutput output = new JsonOutput().setData(data.toArray());
    return output;
  }

  private String determineDataType(List<Object> values) {
    if (values == null || values.isEmpty()) {
      return "Unknown"; // Default for empty or null values
    }
    Set<String> types = new HashSet<>();
    for (Object value : values) {
      if (value instanceof Number) {
        types.add("Number");
      } else if (value instanceof String) {
        types.add("String");
      } else if (value instanceof Boolean) {
        types.add("Boolean");
      } else {
        types.add("Object");
      }
    }
    // If all values have the same type, return it; otherwise, return "Mixed"
    return types.size() == 1 ? types.iterator().next() : "Mixed";
  }
}
