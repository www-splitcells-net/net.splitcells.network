/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.dataset;

import net.splitcells.dem.data.set.list.ListView;

/**
 * TODO This code is experimental and not used.
 *
 * @param <T>
 */
public interface Dataset<T> {
    String name();

    ListView<Property<? extends Object>> headerView();
}
