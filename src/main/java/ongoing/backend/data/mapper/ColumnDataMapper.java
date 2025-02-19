package ongoing.backend.data.mapper;

import ongoing.backend.data.dto.file.ColumnData;
import ongoing.backend.data.response.ColumnDataResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ColumnDataMapper {
  ColumnDataResponse toColumnDataResponse(ColumnData columnData);
}
