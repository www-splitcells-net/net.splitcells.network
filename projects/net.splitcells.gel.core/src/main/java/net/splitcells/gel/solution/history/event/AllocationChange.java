/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history.event;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.view.Line;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.common.Language.*;

public class AllocationChange implements Domable {
    private final AllocationChangeType type;
    private final Line demand;
    private final Line supply;

    public static AllocationChange allocations(AllocationChangeType type, Line demand, Line supply) {
        return new AllocationChange(type, demand, supply);
    }

    private AllocationChange(AllocationChangeType type, Line demand, Line supply) {
        this.type = type;
        this.demand = demand;
        this.supply = supply;
    }

    public AllocationChangeType type() {
        return type;
    }

    public Line demand() {
        return demand;
    }

    public Line supply() {
        return supply;
    }

    @Override
    public Tree toTree() {
        return tree(ALLOCATION.value())
                .withProperty(TYPE.value(), type.name())
                .withProperty(DEMAND2.value(), demand.toTree())
                .withProperty(SUPPLY.value(), supply.toTree());
    }

    @Override
    public String toString() {
        return toTree().toXmlString();
    }
}
