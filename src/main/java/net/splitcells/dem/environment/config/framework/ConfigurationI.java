package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.Map;
import java.util.Set;
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
    /**
     * TODO Keys are Options and not only Objects.
     * <p>
     * HACK Make private
     */
    private final Map<Object, Object> config_store;
    /**
     * TODOC old actor value, new actor value
     */
    private final Map<Class<?>, Set<BiConsumer<Object, Object>>> subscribers;

    public static Configuration configuration() {
        return new ConfigurationI();
    }

    private ConfigurationI() {
        config_store = map();
        subscribers = map();
    }

    /**
     * REFACTOR Split into smaller functions.
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public <T extends Object> Configuration withConfigValue(Class<? extends Option<T>> key, T proposed_new_value) {
        final Set<BiConsumer<Object, Object>> arg_subsribers;
        final T final_new_value;
        if (!config_store.containsKey(key)) {
            assert !subscribers.containsKey(key);
            final Option<T> option;
            try {
                option = (Option<T>) key.newInstance();
                assert option != null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (proposed_new_value == null) {
                final_new_value = (T) option.defaultValue();
            } else {
                final_new_value = proposed_new_value;
            }
            arg_subsribers = setOfUniques();
            subscribers.put(key, arg_subsribers);
        } else {
            arg_subsribers = subscribers.get(key);
            final_new_value = proposed_new_value;
        }
        final Object old_value = config_store.get(key);
        config_store.put(key, final_new_value);

        subscribers.get(key).stream().forEach(subscriber -> {
            subscriber.accept(old_value, final_new_value);
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T configValue(Class<? extends Option<T>> key) {
        if (!this.config_store.containsKey(key)) {
            withConfigValue(key, null);
        }
        return (T) this.config_store.get(key);
    }

    @Override
    public <T> void subscribe(Class<? extends Option<T>> option, BiConsumer<Object, Object> consumer) {
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
