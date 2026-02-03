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
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Optional;

import static net.splitcells.dem.data.set.map.DeterministicMapFactory.deterministicMapFactory;
import static net.splitcells.dem.data.set.map.MapFactoryRandom.mapFI_random;

public class MapFactoryConfigured implements MapFactory {

    public static MapFactoryConfigured mapFI_configured() {
        return new MapFactoryConfigured();
    }

    private MapFactory mapFactory;

    private MapFactoryConfigured() {
        final var isDeterministic = Dem.configValue(IsDeterministic.class);
        if (isDeterministic.isPresent() && isDeterministic.get().isTrue()) {
            mapFactory = deterministicMapFactory();
        } else {
            mapFactory = mapFI_random();
        }
    }

    @Override
    public <K, V> Map<K, V> map() {
        return mapFactory.map();
    }

    @JavaLegacy
    @Override
    public <K, V> Map<K, V> map(java.util.Map<K, V> arg) {
        return mapFactory.map(arg);
    }

    @Deprecated
    private void update(Optional<Bool> oldValue, Optional<Bool> newValue) {
        if (newValue.isEmpty()) {
            mapFactory = mapFI_random();
        } else if (newValue.get().isTrue()) {
            mapFactory = deterministicMapFactory();
        } else {
            mapFactory = mapFI_random();
        }
    }

    @Override
    public void close() {
        // Nothing needs to be done.
    }

}
