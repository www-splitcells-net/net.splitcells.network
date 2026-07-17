/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.JavaLegacy;

public interface MapFactory extends Resource {

    <K, V> Map<K, V> map();

    @JavaLegacy
    <K, V> Map<K, V> map(java.util.Map<K, V> arg);

    /**
     * Usually nothing needs to be done.
     */
    default void flush() {

    }
}
