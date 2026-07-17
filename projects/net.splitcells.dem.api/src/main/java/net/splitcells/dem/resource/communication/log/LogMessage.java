/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * TODO Remove {@link Discoverable}, as {@link #tags()} is replacing it.
 * 
 * @param <T>
 */
public interface LogMessage<T> extends Discoverable {
	T content();

	LogLevel priority();

	/**
	 * {@link Discoverable} is not returned,
	 * as this would require direct access to an object with {@link Discoverable#path()}.
	 * 
	 * @return
	 */
	default List<List<String>> tags() {
		return list();
	}
}
