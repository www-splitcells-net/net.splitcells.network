/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.legacy;

import gnu.trove.set.hash.THashSet;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Collection;

import static net.splitcells.dem.data.set.legacy.LegacySetWrapper.legacySetWrapper;

@JavaLegacy
public class LegacySetTrove implements LegacySetFactory {
    public static LegacySetTrove legacySetTrove() {
        return new LegacySetTrove();
    }

    private LegacySetTrove() {

    }

    @Override public <T> Set<T> set() {
        return legacySetWrapper(new THashSet<>());
    }

    @Override public <T> Set<T> set(Collection<T> arg) {
        return legacySetWrapper(new THashSet<>(arg));
    }
}
