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
package net.splitcells.dem.data.set.factory;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Optional;

import static net.splitcells.dem.data.set.factory.SetFactoryDeterministic.setFactoryDeterministic;
import static net.splitcells.dem.data.set.factory.SetFactoryDefault.setFactoryDefault;

@JavaLegacy
public class SetFactoryConfigured implements SetFactory {
    public static SetFactory setFactoryConfigured() {
        return new SetFactoryConfigured();
    }

    private SetFactory setFactory;

    private SetFactoryConfigured() {
        final var isDeterministic = Dem.configValue(IsDeterministic.class);
        if (isDeterministic.isPresent() && isDeterministic.get().isTrue()) {
            setFactory = setFactoryDeterministic();
        } else {
            setFactory = setFactoryDefault();
        }
    }

    @Override
    public <T> java.util.Set<T> lagacySet() {
        return setFactory.lagacySet();
    }

    @Override
    public <T> java.util.Set<T> legacySet(java.util.Collection<T> arg) {
        return setFactory.legacySet(arg);
    }

    /**
     * TODO Prevent unnecessary object construction.
     */
    @Deprecated
    private void update(Optional<Bool> oldValue, Optional<Bool> newValue) {
        if (newValue.isEmpty()) {
            setFactory = setFactoryDefault();
        } else if (newValue.get().isTrue()) {
            setFactory = setFactoryDeterministic();
        } else {
            setFactory = setFactoryDefault();
        }
    }

    @Override
    public <T> Set<T> set() {
        return setFactory.set();
    }

    @Override
    public <T> Set<T> set(java.util.Collection<T> arg) {
        return setFactory.set(arg);
    }
}
