/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.object;

/**
 * TODO Find a better name.
 *
 * @param <T> The type, that can be cloned.
 */
public interface DeepCloneable2<T> {
    T deepClone();
}
