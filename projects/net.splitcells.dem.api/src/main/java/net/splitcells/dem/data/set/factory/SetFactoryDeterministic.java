/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.factory;

import net.splitcells.dem.data.set.legacy.LegacySetJavaDeterministic;
import net.splitcells.dem.data.set.legacy.LegacySetWrapper;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import static net.splitcells.dem.data.set.legacy.LegacySetJavaDeterministic.legacySetJavaDeterministic;

public class SetFactoryDeterministic implements SetFactory {

    public static SetFactory setFactoryDeterministic() {
        return new SetFactoryDeterministic();
    }

    private final LegacySetJavaDeterministic legacyFactory = legacySetJavaDeterministic();

    private SetFactoryDeterministic() {

    }

    @JavaLegacy
    @Override
    public <T> java.util.Set<T> lagacySet() {
        return legacyFactory.legacySet();
    }

    @JavaLegacy
    @Override
    public <T> java.util.Set<T> legacySet(java.util.Collection<T> arg) {
        return legacyFactory.legacySet(arg);
    }

    @JavaLegacy
    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set() {
        return LegacySetWrapper.legacySetWrapper(legacyFactory.legacySet(), true);
    }

    @JavaLegacy
    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set(java.util.Collection<T> arg) {
        return LegacySetWrapper.legacySetWrapper(legacyFactory.legacySet(arg), true);
    }

}
