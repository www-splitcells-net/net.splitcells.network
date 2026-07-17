/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.factory;

import net.splitcells.dem.data.set.legacy.LegacySetWrapper;
import net.splitcells.dem.data.set.legacy.LegacySets;
import net.splitcells.dem.lang.annotations.JavaLegacy;

@JavaLegacy
public class SetFactoryDefault implements SetFactory {

    public static SetFactory setFactoryDefault() {
        return new SetFactoryDefault();
    }

    private SetFactoryDefault() {

    }
    
    @Override
    public <T> java.util.Set<T> lagacySet() {
        return LegacySets.legacySet();
    }

    @Override
    public <T> java.util.Set<T> legacySet(java.util.Collection<T> arg) {
        return LegacySets.legacySet(arg);
    }
    
    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set() {
        return LegacySetWrapper.legacySetWrapper(LegacySets.legacySet(), false);
    }
    
    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set(java.util.Collection<T> arg) {
        return LegacySetWrapper.legacySetWrapper(LegacySets.legacySet(arg), false);
    }
}
