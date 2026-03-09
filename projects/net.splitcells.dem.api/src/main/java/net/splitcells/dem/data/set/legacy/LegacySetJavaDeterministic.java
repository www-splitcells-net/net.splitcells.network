/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.legacy;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Collection;
import java.util.LinkedHashSet;

import static net.splitcells.dem.data.set.legacy.LegacySetWrapper.legacySetWrapper;

@JavaLegacy
public class LegacySetJavaDeterministic implements LegacySetFactory {
    public static LegacySetJavaDeterministic legacySetJavaDeterministic() {
        return new LegacySetJavaDeterministic();
    }

    private LegacySetJavaDeterministic() {

    }

    @Override public <T> Set<T> legacySet() {
        return legacySetWrapper(new LinkedHashSet<>());
    }

    @Override public <T> Set<T> legacySet(Collection<T> arg) {
        return legacySetWrapper(new LinkedHashSet<>(arg));
    }
}
