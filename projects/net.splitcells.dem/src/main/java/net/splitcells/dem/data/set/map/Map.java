package net.splitcells.dem.data.set.map;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface Map<Key, Value> extends java.util.Map<Key, Value> {

    default Map<Key, Value> with(Key key, Value value) {
        put(key, value);
        return this;
    }

    default Map<Key, Value> withMerged(Map<Key, Value> args, BiFunction<Value, Value, Value> mergeFunction) {
        args.forEach((aKey, aVal) -> this.merge(aKey, aVal, mergeFunction));
        return this;
    }

    /**
     * RENAME
     */
    default Value addIfAbsent(Key key, Supplier<Value> valueSupplier) {
        Value rVal = get(key);
        if (!containsKey(key)) {
            rVal = valueSupplier.get();
            put(key, rVal);
        }
        return rVal;
    }
}
