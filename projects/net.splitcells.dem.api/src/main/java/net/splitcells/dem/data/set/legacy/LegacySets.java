/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.legacy;

import static net.splitcells.dem.data.set.legacy.LegacySetTrove.legacySetTrove;
import static net.splitcells.dem.data.set.legacy.LegacySetWrapper.legacySetWrapper;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class LegacySets {

    private static final LegacySetFactory SET_FACTORY = legacySetTrove();

    private LegacySets() {
        throw constructorIllegal();
    }

    public static <T> java.util.Set<T> legacySet() {
        return SET_FACTORY.legacySet();
    }

    public static <T> java.util.Set<T> legacySet(java.util.Collection<T> arg) {
        return SET_FACTORY.legacySet(arg);
    }

}
