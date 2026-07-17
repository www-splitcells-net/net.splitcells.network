/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

import net.splitcells.dem.data.set.SetT;
import net.splitcells.gel.data.view.Line;

@FunctionalInterface
public interface AfterAdditionSubscriber {

    void registerAddition(Line addition);

    default void register_addition(SetT<Line> additions) {
        additions.forEach(addition -> registerAddition(addition));
    }

    default void register_addition(Line... additions) {
        register_addition(listWithValuesOf(additions));
    }
}
