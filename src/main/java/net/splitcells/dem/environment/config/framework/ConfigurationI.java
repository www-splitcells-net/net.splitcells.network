package net.splitcells.dem.environment.config.framework;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

public class ConfigurationI implements Configuration {
    /**
     * TODOC actor -> reactor -> enforcer
     * <p>
     * TODOC old reactor value, new actor value -> reactor value change
     */
    private final Map<Class<?>, Map<Class<? extends Option<?>>, BiFunction<Object, Object, Object>>> config_consistency_enforcers;
    /**
     * TODO Keys are Options and not only Objects.
     * <p>
     * HACK Make private
     */
    public final Map<Object, Object> config_store;
    /**
     * TODOC old actor value, new actor value
     */
    private final Map<Class<?>, Set<BiConsumer<Object, Object>>> subscribers;

    public ConfigurationI() {
        config_consistency_enforcers = map();
        config_store = map();
        subscribers = map();
    }

    /**
     * REFACTOR Split into smaller functions.
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public <T extends Object> Configuration withConfigValue(Class<? extends Option<T>> key, T proposed_new_value) {
        final Map<Class<? extends Option<?>>, BiFunction<Object, Object, Object>> arg_consistency_enforcers;
        final Set<BiConsumer<Object, Object>> arg_subsribers;
        final T final_new_value;
        if (!config_store.containsKey(key)) {
            assert !config_consistency_enforcers.containsKey(key);
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
            {
                final Map<Class<? extends Option<?>>, BiFunction<Object, Object, Object>> reactors
                        = option.consistencyEnforcers();
                reactors.entrySet().stream().forEach(enforcer -> {
                    if (!config_consistency_enforcers.containsKey(enforcer.getKey())) {
                        config_consistency_enforcers.put(enforcer.getKey(), map());
                    }
                    config_consistency_enforcers.get(enforcer.getKey()).put(key, enforcer.getValue());
                });
                if (!config_consistency_enforcers.containsKey(key)) {
                    config_consistency_enforcers.put(key, map());
                }
            }
            arg_subsribers = setOfUniques();
            subscribers.put(key, arg_subsribers);
        } else {
            arg_subsribers = subscribers.get(key);
            final_new_value = proposed_new_value;
        }
        arg_consistency_enforcers = config_consistency_enforcers.get(key);
        final Object old_value = config_store.get(key);
        config_store.put(key, final_new_value);

        arg_consistency_enforcers.entrySet().forEach(consistency_enforcer -> {
            withConfigValue
                    ((Class<Option<Object>>) consistency_enforcer.getKey()
                            , consistency_enforcer.getValue().apply(
                            config_store.get(consistency_enforcer.getKey()), final_new_value)
                    );
        });
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
}
