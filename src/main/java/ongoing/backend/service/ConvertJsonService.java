package ongoing.backend.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ongoing.backend.config.exception.ApiException;
import ongoing.backend.data.ColumnData;
import ongoing.backend.data.JsonNestedRequest;
import ongoing.backend.data.JsonOutput;
import ongoing.backend.data.NestParamData;
import ongoing.backend.data.mapper.ColumnDataMapper;
import ongoing.backend.data.response.ColumnDataResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        .setKey(nestParamData.getColumnName())
        .setType(nestParamData.getDataType());
      columnData.add(columnDataMapper.toColumnDataResponse(column));
      List<Object> value = null;
      try {
        value = jsonContext.read(nestParamData.getNestString());
      } catch (Exception e) {
        throw new ApiException(String.format("Failed to parse json %s", nestParamData.getNestString()));
      }
      if (value != null) {
        if (value.size() > size) {
          size = value.size();
        }
        column.setData(value);
      }
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
}
