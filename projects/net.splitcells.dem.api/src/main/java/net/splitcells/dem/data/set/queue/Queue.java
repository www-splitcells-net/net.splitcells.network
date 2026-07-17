/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.queue;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Optional;

@JavaLegacy
public interface Queue<T> extends java.util.Queue<T> {
    Optional<T> pollNext();
}
