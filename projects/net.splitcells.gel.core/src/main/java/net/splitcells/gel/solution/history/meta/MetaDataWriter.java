/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history.meta;

public interface MetaDataWriter {

	<A> MetaDataWriter with(Class<A> type, A value);
}
