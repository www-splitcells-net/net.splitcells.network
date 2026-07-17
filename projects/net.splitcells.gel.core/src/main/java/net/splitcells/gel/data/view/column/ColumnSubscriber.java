/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view.column;

public interface ColumnSubscriber<T> {

	void register_addition(T addition, int index);

	void register_removal(T removal, int index);

}
