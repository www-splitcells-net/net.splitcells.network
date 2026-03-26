/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.legacy;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Collection;

import static net.splitcells.dem.data.set.legacy.LegacySetWrapper.legacySetWrapper;

@JavaLegacy
public class LegacySetEclipseFactory implements LegacySetFactory {
    public static LegacySetEclipseFactory legacySetEclipseFactory() {
        return new LegacySetEclipseFactory();
    }

    private LegacySetEclipseFactory() {

    }

    @Override public <T> Set<T> legacySet() {
        return legacySetWrapper(new UnifiedSet<>());
    }

    @Override public <T> Set<T> legacySet(Collection<T> arg) {
        return legacySetWrapper(new UnifiedSet<>(arg));
    }
}
