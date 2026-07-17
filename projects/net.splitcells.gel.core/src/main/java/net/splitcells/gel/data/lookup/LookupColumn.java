/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.view.column.Column;
import net.splitcells.gel.data.view.column.ColumnSubscriber;

/**
 * Provides an API to look up values of a {@link Column}.
 *
 * @param <T> Value type being looked up.
 */
public interface LookupColumn<T> extends ColumnSubscriber<T>, LookupMethods<T>, Discoverable {

}
