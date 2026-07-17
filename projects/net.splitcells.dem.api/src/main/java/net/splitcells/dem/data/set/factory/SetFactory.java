/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.factory;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.JavaLegacy;

/**
 * TODO Specify expected min/average/max size
 * <p>
 * TODO Specify required performance signature.
 */
@JavaLegacy
public interface SetFactory extends Resource {

    <T> Set<T> set();

    @JavaLegacy
    <T> Set<T> set(java.util.Collection<T> arg);

    @Deprecated
    @JavaLegacy
    <T> java.util.Set<T> lagacySet();

    @Deprecated
    @JavaLegacy
    <T> java.util.Set<T> legacySet(java.util.Collection<T> arg);

    /**
     * Most implementations won't have to do anything.
     */
    @Override
    default void close() {
        // Nothing needs to be done, by default.
    }

    /**
     * Most implementations won't have to do anything.
     */
    @Override
    default void flush() {
        // Nothing needs to be done, by default.
    }
}
