/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.environment.config.StaticFlags;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * TODO {@link Configuration} consistency check could be implemented via {@link #subscribers}.
 */
public class ConfigurationI implements Configuration {
    private final Map<Object, Object> config_store;
    @Deprecated
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
                if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
                    assertThat(subscribers.containsKey(key)).isFalse();
                }
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
