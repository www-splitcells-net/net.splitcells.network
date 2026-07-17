/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history.meta;

import net.splitcells.dem.lang.dom.Domable;

import java.util.Optional;

public interface MetaDataView extends Domable {

	<T> Optional<T> value(Class<T> type);

}
