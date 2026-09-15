/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.assignment;

import net.splitcells.dem.data.set.SetT;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ExecutionException.execException;

@FunctionalInterface
public interface AssignmentSubscriber {

    void registerAddition(AssignmentEvent event);

    default void registerAddition(SetT<AssignmentEvent> additions) {
        additions.forEach(addition -> registerAddition(addition));
    }

    default void registerAddition(AssignmentEvent... additions) {
        registerAddition(listWithValuesOf(additions));
    }
}
