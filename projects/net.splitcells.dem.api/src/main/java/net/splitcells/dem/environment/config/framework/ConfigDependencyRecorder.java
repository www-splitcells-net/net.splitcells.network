/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.factory.SetFactory;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.DeterministicMapFactory;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.factory.SetFactoryDeterministic.setFactoryDeterministic;
import static net.splitcells.dem.data.set.map.DeterministicMapFactory.deterministicMapFactory;
import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * Records all {@link Option} dependencies for every given {@link Option},
 * so that the initialization order can be understood.
 * This class is used at the initialization of {@link Dem#process(Runnable)}.
 * Therefore, any reliance on {@link Dem#configValue(Class)} etc. has to be avoided.
 */
public class ConfigDependencyRecorder {
    public static ConfigDependencyRecorder dependencyRecorder() {
        return new ConfigDependencyRecorder();
    }

    private final SetFactory setFactory = setFactoryDeterministic();

    /**
     * Maps for every {@link Option}, which are set via {@link Configuration#withConfigValue(Class, Object)} etc.,
     * to a set of {@link Option}, that are required for its initialization.
     * <p>
     * {@link DeterministicMapFactory#deterministicMapFactory} is used,
     * in order to avoid duplicate {@link Dem#process} initialization.
     */
    private final Map<Class<? extends Option<? extends Object>>, Set<Class<? extends Option<? extends Object>>>>
            dependencies = deterministicMapFactory().map();

    private ConfigDependencyRecorder() {

    }

    public void recordDependency(Class<? extends Option<? extends Object>> from
            , Class<? extends Option<? extends Object>> to) {
        dependencies.computeIfAbsent(from, f -> setFactory.set()).add(to);
    }

    public Map<Class<? extends Option<? extends Object>>, Set<Class<? extends Option<? extends Object>>>> dependencies() {
        return dependencies;
    }
}
