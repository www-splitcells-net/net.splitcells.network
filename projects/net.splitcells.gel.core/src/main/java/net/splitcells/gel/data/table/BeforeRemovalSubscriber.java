/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import java.util.Collection;

import net.splitcells.gel.data.view.Line;

@FunctionalInterface
public interface BeforeRemovalSubscriber {
	void registerBeforeRemoval(Line removal);

	default void registerBeforeRemoval(Collection<Line> removals) {
		removals.forEach(line -> registerBeforeRemoval(line));
	}

}
