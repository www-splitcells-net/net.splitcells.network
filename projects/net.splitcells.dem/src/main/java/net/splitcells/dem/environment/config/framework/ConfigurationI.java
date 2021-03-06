package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * TODO {@link Configuration} consistency check could be implemented via {@link #subscribers}.
 */
public class ConfigurationI implements Configuration {
    private final Map<Object, Object> config_store;
    private final Map<Class<?>, Set<OptionSubscriber<Object>>> subscribers;

    public static Configuration configuration() {
        return new ConfigurationI();
    }

    private ConfigurationI() {
        config_store = new HashMap<>();
        subscribers = new HashMap<>();
    }

    @Override
    public <T extends Object> Configuration withConfigValue(Class<? extends Option<T>> key, T new_value) {
        try {
            final Set<OptionSubscriber<Object>> key_subscribers;
            if (!config_store.containsKey(key)) {
                assert !subscribers.containsKey(key);
                final Option<T> option = key.getDeclaredConstructor().newInstance();
                key_subscribers = new HashSet<>();
                subscribers.put(key, key_subscribers);
            } else {
                key_subscribers = subscribers.get(key);
            }
            final Object old_value = config_store.get(key);
            config_store.put(key, new_value);
            subscribers.get(key).stream().forEach(subscriber -> {
                subscriber.accept(old_value, new_value);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T configValue(Class<? extends Option<T>> key) {
        if (!this.config_store.containsKey(key)) {
            with_inited_option(key);
        }
        return (T) this.config_store.get(key);
    }

    @Override
    public <T> void subscribe(Class<? extends Option<T>> option, OptionSubscriber<Object> consumer) {
        if (!this.subscribers.containsKey(option)) {
            this.subscribers.put(option, setOfUniques());
        }
        assert !this.subscribers.get(option).contains(consumer);
        this.subscribers.get(option).add(consumer);
    }

    @Override
    public <T> void process(Class<? extends T> type, Function<T, T> processor) {
        config_store.entrySet().forEach(entry -> {
            if (type.isAssignableFrom(entry.getValue().getClass())) {
                entry.setValue(
                        processor.apply(((T) entry.getValue()))
                );
            }
        });
    }
}
