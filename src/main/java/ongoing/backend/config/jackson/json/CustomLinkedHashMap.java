package ongoing.backend.config.jackson.json;

import java.util.LinkedHashMap;

class CustomLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 3801124242820219132L;

    @Override
    public V put(K key, V value) {
        V oldValue = this.get(key);
        if (oldValue == null)
            return super.put(key, value);
        if (value != null) {
            if (value.toString().length() > oldValue.toString().length()) {
                return super.put(key, value);
            }
        }
        return oldValue;
    }
}
