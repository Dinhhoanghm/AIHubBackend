package ongoing.backend.config.jackson.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import ongoing.backend.config.exception.DecodeException;
import ongoing.backend.config.exception.EncodeException;
import ongoing.backend.config.jackson.JsonMapper;


import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Json {
    /**
     * Decode a given JSON string to a POJO of the given class type.
     *
     * @param str   the JSON string.
     * @param clazz the class to map to.
     * @param <T>   the generic type.
     * @return an instance of T
     * @throws DecodeException when there is a parsing or invalid mapping.
     */
    public static <T> T decodeValue(String str, Class<T> clazz) {
        try {
            return JsonMapper.getObjectMapper().readValue(str, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DecodeException("Failed to decode: " + e.getMessage());
        }
    }

    /**
     * Decode a given JSON string to a POJO of the given type.
     *
     * @param str  the JSON string.
     * @param type the type to map to.
     * @param <T>  the generic type.
     * @return an instance of T
     * @throws DecodeException when there is a parsing or invalid mapping.
     */
    public static <T> T decodeValue(String str, TypeReference<T> type) throws DecodeException {
        try {
            return JsonMapper.getObjectMapper().readValue(str, type);
        } catch (Exception e) {
            throw new DecodeException("Failed to decode: " + e.getMessage(), e);
        }
    }

    /**
     * Encode a POJO to JSON using the underlying Jackson mapper.
     *
     * @param obj a POJO
     * @return a String containing the JSON representation of the given POJO.
     * @throws EncodeException if a property cannot be encoded.
     */
    public static String encode(Object obj) throws EncodeException {
        try {
            return JsonMapper.getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new EncodeException("Failed to encode as JSON: " + e.getMessage());
        }
    }

  public static String encodeCamelCase(Object obj) throws EncodeException {
    try {
      return JsonMapper.getObjectMapperCamelCase().writeValueAsString(obj);
    } catch (Exception e) {
      throw new EncodeException("Failed to encode as JSON: " + e.getMessage());
    }
  }


    static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
