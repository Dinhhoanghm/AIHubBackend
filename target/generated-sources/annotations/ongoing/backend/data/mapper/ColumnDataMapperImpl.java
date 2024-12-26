package ongoing.backend.data.mapper;

import javax.annotation.processing.Generated;
import ongoing.backend.data.dto.ColumnData;
import ongoing.backend.data.response.ColumnDataResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T09:14:32+0700",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class ColumnDataMapperImpl implements ColumnDataMapper {

    @Override
    public ColumnDataResponse toColumnDataResponse(ColumnData columnData) {
        if ( columnData == null ) {
            return null;
        }

        ColumnDataResponse columnDataResponse = new ColumnDataResponse();

        columnDataResponse.setAlias( columnData.getAlias() );
        columnDataResponse.setType( columnData.getType() );
        columnDataResponse.setKey( columnData.getKey() );

        return columnDataResponse;
    }
}
