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
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.DeterministicMapFactory;

import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
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
        dependencies
                .computeIfAbsent(from, f -> setLegacyWrapper(new java.util.LinkedHashSet<>()))
                .add(to);
    }

    public Map<Class<? extends Option<? extends Object>>, Set<Class<? extends Option<? extends Object>>>> dependencies() {
        return dependencies;
    }
}
