/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view.column;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.AfterAdditionSubscriber;
import net.splitcells.gel.data.table.BeforeRemovalSubscriber;

public interface Column<T> extends List<T>, AfterAdditionSubscriber, BeforeRemovalSubscriber, ColumnView<T> {
}
