/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.map.typed.TypedMap;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.*;
import java.util.function.Function;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * <p>TODO {@link Configuration} consistency check could be implemented via {@link #subscribers}.
 * Automatic {@link Option} update based on other {@link Option} updates should not be done via these {@link #subscribers}.</p>
 * <p>TODO Use {@link TypedMap}, instead of {@link Map}, in order to reduce the amount of reflection usage.</p>
 */
@JavaLegacyArtifact
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
                    Bools.require(!subscribers.containsKey(key));
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
            withInitedOption(key);
        }
        return (T) this.config_store.get(key);
    }

    @Override
    public Object configValueUntyped(Object key) {
        return config_store.get(key);
    }

    @Override
    public net.splitcells.dem.data.set.Set<Class<? extends Option<?>>> keys() {
        return config_store.keySet().stream()
                .map(arg -> (Class<? extends Option<?>>) arg)
                .collect(Sets.toSetOfUniques());
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
