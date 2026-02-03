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

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.map.typed.TypedMap;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.framework.ConfigDependencyRecorder.dependencyRecorder;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>This class is synchronized, as multiple {@link Dem#process(Runnable)} can be started at once,
 * that use a parent {@link Dem#process(Runnable)} for its config.
 * See the web server, where one {@link Dem#process(Runnable)} is done per worker.</p>
 * <p>TODO There should be a general way to add aspects to this,
 * that work during {@link Dem#process(Runnable)} etc. initialization.
 * See {@link #dependencyRecorder} for such a feature.</p>
 * <p>TODO {@link Configuration} consistency check could be implemented via {@link #subscribers}.
 * Automatic {@link Option} update based on other {@link Option} updates should not be done via these {@link #subscribers}.</p>
 * <p>TODO Use {@link TypedMap}, instead of {@link Map}, in order to reduce the amount of reflection usage.</p>
 * <p>TODO Ensure, that config value's identity cannot be replaced after executing the environment consumer at {@link Dem#process(Runnable, Consumer)}.
 * Config values can be mutable, in order to manage states inside the config,
 * but the users of {@link Configuration} can rely on the fact,
 * that the config values are final after the environment consumer's execution.</p>
 */
@JavaLegacy
public class ConfigurationImpl implements Configuration {
    private final Map<Object, Object> configStore;
    @Deprecated
    private final Map<Class<?>, Set<OptionSubscriber<Object>>> subscribers;
    private final ConfigDependencyRecorder dependencyRecorder;
    private final List<Class<? extends Option<?>>> dependencyStack = new ArrayList<>();

    public static Configuration configuration() {
        return new ConfigurationImpl();
    }

    private ConfigurationImpl() {
        configStore = new HashMap<>();
        subscribers = new HashMap<>();
        dependencyRecorder = dependencyRecorder();
        configStore.put(ConfigDependencyRecording.class, dependencyRecorder);
    }

    private void recordConfigAccess(Class<? extends Option<? extends Object>> key) {
        if (!dependencyStack.isEmpty()) {
            if (dependencyStack.get(dependencyStack.size() - 1).equals(key)) {
                if (dependencyStack.size() > 1) {
                    dependencyRecorder.recordDependency(dependencyStack.get(dependencyStack.size() - 2), key);
                }
            } else {
                dependencyRecorder.recordDependency(dependencyStack.get(dependencyStack.size() - 1), key);
            }
        }
    }

    @Override
    public synchronized <T extends Object> Configuration withConfigValue(Class<? extends Option<T>> key, T newValue) {
        try {
            recordConfigAccess(key);
            final Set<OptionSubscriber<Object>> keySubscribers;
            if (configStore.containsKey(key)) {
                keySubscribers = subscribers.get(key);
            } else {
                if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
                    Bools.require(!subscribers.containsKey(key));
                }
                keySubscribers = new HashSet<>();
                subscribers.put(key, keySubscribers);
            }
            final Object oldValue = configStore.get(key);
            configStore.put(key, newValue);
            keySubscribers.stream().forEach(subscriber -> {
                subscriber.accept(oldValue, newValue);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public synchronized <T> Configuration withInitedOption(Class<? extends Option<T>> key) {
        try {
            dependencyStack.add(key);
            return withConfigValue(key, key.getDeclaredConstructor().newInstance().defaultValue());
        } catch (Throwable e) {
            throw ExecutionException.execException(tree("Could not initialize config with default value.")
                            .withProperty("key", key.getName())
                    , e);
        } finally {
            dependencyStack.removeLast();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T> T configValue(Class<? extends Option<T>> key) {
        if (!this.configStore.containsKey(key)) {
            withInitedOption(key);
        }
        recordConfigAccess(key);
        return (T) this.configStore.get(key);
    }

    @Override
    public synchronized Object configValueUntyped(Object key) {
        recordConfigAccess((Class<? extends Option<? extends Object>>) key);
        return configStore.get(key);
    }

    @Override
    public net.splitcells.dem.data.set.Set<Class<? extends Option<?>>> keys() {
        return configStore.keySet().stream()
                .map(arg -> (Class<? extends Option<?>>) arg)
                .collect(Sets.toSetOfUniques());
    }

    @Override
    public <T> void process(Class<? extends T> type, Function<T, T> processor) {
        listWithValuesOf(configStore.entrySet()).forEach(entry -> {
            if (type.isAssignableFrom(entry.getValue().getClass())) {
                entry.setValue(
                        processor.apply(((T) entry.getValue()))
                );
            }
        });
    }

    @Override
    public <K extends Class<? extends Option<V>>, V> void consume(K type, BiConsumer<K, V> consumer) {
        listWithValuesOf(configStore.entrySet()).forEach(entry -> {
            if (type.isAssignableFrom((K) entry.getKey())) {
                consumer.accept((K) entry.getKey(), (V) entry.getValue());
            }
        });
    }
}
