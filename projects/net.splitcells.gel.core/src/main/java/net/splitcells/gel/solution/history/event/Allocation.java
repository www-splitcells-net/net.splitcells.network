/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution.history.event;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.data.table.Line;
import org.w3c.dom.Node;

import static net.splitcells.gel.common.Language.*;

public class Allocation implements Domable {
    private final AllocationChangeType type;
    private final Line demand;
    private final Line supply;

    public static Allocation allocations(AllocationChangeType type, Line demand, Line supply) {
        return new Allocation(type, demand, supply);
    }

    private Allocation(AllocationChangeType type, Line demand, Line supply) {
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
    public Node toDom() {
        final var allocation = Xml.elementWithChildren(ALLOCATION.value());
        allocation.appendChild
                (Xml.elementWithChildren(TYPE.value()).appendChild(Xml.textNode(type.name())));
        allocation.appendChild
                (Xml.elementWithChildren(DEMAND2.value()).appendChild(demand.toDom()));
        allocation.appendChild
                (Xml.elementWithChildren(SUPPLY.value()).appendChild(supply.toDom()));
        return allocation;
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
