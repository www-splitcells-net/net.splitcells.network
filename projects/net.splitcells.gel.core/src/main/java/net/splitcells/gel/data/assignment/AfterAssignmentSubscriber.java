/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.assignment;

import net.splitcells.dem.data.set.SetT;
import net.splitcells.gel.data.table.TableEvent;
import net.splitcells.gel.data.view.Line;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.data.table.history.TableEventType.ADDITION;

@FunctionalInterface
public interface AfterAssignmentSubscriber {

    void registerAddition(AssignmentEvent event);

    default void registerAddition(SetT<AssignmentEvent> additions) {
        additions.forEach(addition -> registerAddition(addition));
    }

    default void registerAddition(AssignmentEvent... additions) {
        registerAddition(listWithValuesOf(additions));
    }
}
