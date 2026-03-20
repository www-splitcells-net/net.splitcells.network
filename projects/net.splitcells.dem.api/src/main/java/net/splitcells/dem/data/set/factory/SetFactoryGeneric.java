/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.factory;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.legacy.LegacySetFactory;

import static net.splitcells.dem.data.set.legacy.LegacySetWrapper.legacySetWrapper;

public class SetFactoryGeneric implements SetFactory {
    public static SetFactory setFactoryGeneric(LegacySetFactory argLegacySetFactory) {
        return new SetFactoryGeneric(argLegacySetFactory);
    }

    private final LegacySetFactory setLegacyFactory;

    private SetFactoryGeneric(LegacySetFactory argLegacySetFactory) {
        setLegacyFactory = argLegacySetFactory;
    }

    @Override public <T> java.util.Set<T> lagacySet() {
        return setLegacyFactory.legacySet();
    }

    @Override public <T> java.util.Set<T> legacySet(java.util.Collection<T> arg) {
        return setLegacyFactory.legacySet(arg);
    }

    @Override public <T> Set<T> set() {
        return legacySetWrapper(setLegacyFactory.legacySet());
    }

    @Override public <T> Set<T> set(java.util.Collection<T> arg) {
        return legacySetWrapper(setLegacyFactory.legacySet(arg));
    }
}

