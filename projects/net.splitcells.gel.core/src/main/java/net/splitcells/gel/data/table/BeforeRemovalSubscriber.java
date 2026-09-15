/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import java.util.Collection;

import net.splitcells.gel.data.table.history.TableEventType;
import net.splitcells.gel.data.view.Line;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.data.table.history.TableEventType.REMOVAL;

@FunctionalInterface
public interface BeforeRemovalSubscriber {
	void registerBeforeRemoval(Line removal);
    default void registerBeforeRemoval(TableEvent event) {
        if (ENFORCING_UNIT_CONSISTENCY && event.getType() != REMOVAL) {
            throw execException("Only removal events are supported.");
        }
        registerBeforeRemoval(event.getLine());
    }

	default void registerBeforeRemoval(Collection<Line> removals) {
		removals.forEach(line -> registerBeforeRemoval(line));
	}

}
