/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.queue;

import net.splitcells.dem.lang.annotations.JavaLegacy;

@JavaLegacy
public interface LegacySetFactory {
    <T> java.util.Set<T> set();

    <T> java.util.Set<T> set(java.util.Collection<T> arg);
}
