/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.utils.ExecutionException.execException;

import net.splitcells.dem.data.set.SetT;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.data.table.history.TableEventType;
import net.splitcells.gel.data.view.Line;

@FunctionalInterface
public interface AfterAdditionSubscriber {

    void registerAddition(Line addition);

    default void registerAddition(TableEvent event) {
        if (ENFORCING_UNIT_CONSISTENCY && event.getType() != TableEventType.ADDITION) {
            throw execException("Only addition events are supported.");
        }
        registerAddition(event.getLine());
    }

    default void registerAddition(SetT<Line> additions) {
        additions.forEach(addition -> registerAddition(addition));
    }

    default void registerAddition(Line... additions) {
        registerAddition(listWithValuesOf(additions));
    }
}
