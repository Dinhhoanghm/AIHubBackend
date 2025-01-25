package ongoing.backend.config.jackson.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import ongoing.backend.config.jackson.JsonMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@Component
public class JsonObject {
    private Map<String, Object> map;
    private String json;

    /**
     * Create an instance from a string of JSON
     *
     * @param json the string of JSON
     */
    public JsonObject(String json) {
        this.json = json;
        fromJson(json);
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    /**
     * Create a new, empty instance
     */
    public JsonObject() {
        map = new LinkedHashMap<>();
    }

    public JsonObject put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    /**
     * Create an instance from a Map. The Map is not copied.
     *
     * @param map the map to create the instance from.
     */
    public JsonObject(Map<String, Object> map) {
        this.map = map;
    }


    public static JsonObject from(Object o) throws JsonProcessingException {
        try {
            String value = JsonMapper.getObjectMapper().writeValueAsString(o);
            return new JsonObject(value);
        } catch (JsonProcessingException e) {
            log.error("[JsonObject] from object cause{}", e);
            throw e;
        }
    }

    /**
     * Create a JsonObject from the fields of a Java object.
     * Faster than calling `new JsonObject(Json.encode(obj))`.
     *
     * @param obj The object to convert to a JsonObject.
     * @throws IllegalArgumentException if conversion fails due to an incompatible type.
     */
    @SuppressWarnings("unchecked")
    public static JsonObject mapFrom(Object obj) {
        return new JsonObject((Map<String, Object>) JsonMapper.getObjectMapper().convertValue(obj, Map.class));
    }
  public static JsonObject mapFromCamelCase(Object obj) {
    return new JsonObject((Map<String, Object>) JsonMapper.getObjectMapperCamelCase().convertValue(obj, Map.class));
  }

    public <T> T mapTo(Class<T> type) {
        if (map.isEmpty()) return null;
        return JsonMapper.getObjectMapper().convertValue(map, type);
    }

  public <T> T mapToCamelCase(Class<T> type) {
    if (map.isEmpty()) return null;
    return JsonMapper.getObjectMapperCamelCase().convertValue(map, type);
  }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    private void fromJson(String json) {
        try {
            map = Json.decodeValue(json, Map.class);
        } catch (Exception exception) {
            log.error("[JSON-OBJECT] can't decode value ", exception);
            map = new HashMap<>();
        }
    }


    /**
     * Get the string value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws ClassCastException if the value is not a String
     */
    public String getString(String key) {
        Objects.requireNonNull(key);
        CharSequence cs = (CharSequence) map.get(key);
        return cs == null ? null : cs.toString();
    }

    /**
     * Get the Integer value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws ClassCastException if the value is not an Integer
     */
    public Integer getInteger(String key) {
        Objects.requireNonNull(key);
        Object o = map.get(key);
        if (o instanceof Integer integer) {
            return integer;
        } else if (o instanceof Number number) {
            return number.intValue();
        } else {
            return null;
        }
    }

    /**
     * Get the Long value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws ClassCastException if the value is not a Long
     */
    public Long getLong(String key) {
        Objects.requireNonNull(key);
        Object o = map.get(key);
        if (o instanceof Long aLong) {
            return aLong;
        } else if (o instanceof Number number) {
            return number.longValue();
        } else {
            return null;
        }
    }

    /**
     * Get the Double value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws ClassCastException if the value is not a Double
     */
    public Double getDouble(String key) {
        Objects.requireNonNull(key);
        Object o = map.get(key);
        if (o instanceof Double aDouble) {
            return aDouble;
        } else if (o instanceof Number number) {
            return number.doubleValue();
        } else {
            return null;
        }
    }

    /**
     * Get the Float value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws ClassCastException if the value is not a Float
     */
    public Float getFloat(String key) {
        Objects.requireNonNull(key);
        Object o = map.get(key);
        if (o instanceof Float aFloat) {
            return aFloat;
        } else if (o instanceof Number number) {
            return number.floatValue();
        } else {
            return null;
        }
    }

    /**
     * Get the Boolean value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws ClassCastException if the value is not a Boolean
     */
    public Boolean getBoolean(String key) {
        Objects.requireNonNull(key);
        return (Boolean) map.get(key);
    }


    /**
     * Get the JsonObject value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws ClassCastException if the value is not a JsonObject
     */
    public JsonObject getJsonObject(String key) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        if (val instanceof Map map) {
            return new JsonObject(map);
        }
        return (JsonObject) val;
    }

    public JsonArray getJsonArray(String key) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        if (val instanceof List<?> objects) {
            return new JsonArray(objects);
        }
        return null;
    }

    /**
     * Get the value with the specified key, as an Object
     *
     * @param key the key to lookup
     * @return the value
     */
    public Object getValue(String key) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        if (val instanceof Map map) {
            val = new JsonObject(map);
        } else if (val instanceof List list) {
            val = new JsonArray(list);
        }
        return val;
    }

    /**
     * Like {@link #getString(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public String getString(String key, String def) {
        Objects.requireNonNull(key);
        CharSequence cs = (CharSequence) map.get(key);
        return cs != null || map.containsKey(key) ? cs == null ? null : cs.toString() : def;
    }

    /**
     * Like {@link #getInteger(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Integer getInteger(String key, Integer def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        if (val == null) {
            if (map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Integer) {
            return (Integer) val;  // Avoids unnecessary unbox/box
        } else {
            return val.intValue();
        }
    }

    /**
     * Like {@link #getLong(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Long getLong(String key, Long def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        if (val == null) {
            if (map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Long) {
            return (Long) val;  // Avoids unnecessary unbox/box
        } else {
            return val.longValue();
        }
    }

    /**
     * Like {@link #getDouble(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Double getDouble(String key, Double def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        if (val == null) {
            if (map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Double) {
            return (Double) val;  // Avoids unnecessary unbox/box
        } else {
            return val.doubleValue();
        }
    }

    /**
     * Like {@link #getFloat(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Float getFloat(String key, Float def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        if (val == null) {
            if (map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Float) {
            return (Float) val;  // Avoids unnecessary unbox/box
        } else {
            return val.floatValue();
        }
    }

    /**
     * Like {@link #getBoolean(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Boolean getBoolean(String key, Boolean def) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        return val != null || map.containsKey(key) ? (Boolean) val : def;
    }

    /**
     * Encode this JSON object as a string.
     *
     * @return the string encoding.
     */
    public String encode() {
        return Json.encode(map);
    }

    public static <T> T convertToObject(String s, Class<T> tClass) {
        try {
            return new JsonObject(s).mapTo(tClass);
        } catch (Exception exception) {
            log.error("[PARSER] fail to parser to object {} -class {}", s, tClass.getName());
            return null;
        }
    }

    public Object remove(String key) {
        return this.map.remove(key);
    }
}
