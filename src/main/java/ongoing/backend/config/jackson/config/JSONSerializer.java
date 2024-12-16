package ongoing.backend.config.jackson.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jooq.JSON;

import java.io.IOException;

public class JSONSerializer extends JsonSerializer<JSON> {
    @Override
    public void serialize(JSON json, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(json.data());
    }
}
