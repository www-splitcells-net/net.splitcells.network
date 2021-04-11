package net.splitcells.dem.data.set.map;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class MapLegacyWrapper<Key, Value> implements Map<Key, Value> {

    public static <Key, Value> Map<Key, Value> mapLegacyWrapper(java.util.Map<Key, Value> content) {
        return new MapLegacyWrapper(content, Optional.empty());
    }

    public static <Key, Value> Map<Key, Value> mapLegacyWrapper(java.util.Map<Key, Value> content, Boolean isDeterministic) {
        return new MapLegacyWrapper(content, Optional.of(isDeterministic));
    }

    private final java.util.Map<Key, Value> content;
    private final Optional<Boolean> isDeterministic;

    private MapLegacyWrapper(java.util.Map<Key, Value> content, Optional<Boolean> isDeterministic) {
        this.content = content;
        this.isDeterministic = isDeterministic;
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return content.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return content.containsValue(value);
    }

    @Override
    public Value get(Object key) {
        return content.get(key);
    }

    @Override
    public Value put(Key key, Value value) {
        return content.put(key, value);
    }

    @Override
    public Value remove(Object key) {
        return content.remove(key);
    }

    @Override
    public void putAll(java.util.Map<? extends Key, ? extends Value> m) {
        content.putAll(m);
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public Set<Key> keySet() {
        return content.keySet();
    }

    @Override
    public Collection<Value> values() {
        return content.values();
    }

    @Override
    public Set<Entry<Key, Value>> entrySet() {
        return content.entrySet();
    }

    @Override
    public Optional<Boolean> _isDeterministic() {
        return isDeterministic;
    }
}
