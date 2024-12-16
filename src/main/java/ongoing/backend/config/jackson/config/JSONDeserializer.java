package ongoing.backend.config.jackson.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jooq.JSON;

import java.io.IOException;

public class JSONDeserializer extends JsonDeserializer<JSON> {
    @Override
    public JSON deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String t = p.getValueAsString();
        return t == null ? null : JSON.json(t);
    }
}
