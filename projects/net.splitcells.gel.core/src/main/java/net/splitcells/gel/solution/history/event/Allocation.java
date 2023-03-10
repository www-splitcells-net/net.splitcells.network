/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history.event;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.data.table.Line;
import org.w3c.dom.Node;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
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
    public Perspective toPerspective() {
        return perspective(ALLOCATION.value())
                .withProperty(TYPE.value(), type.name())
                .withProperty(DEMAND2.value(), demand.toPerspective())
                .withProperty(SUPPLY.value(), supply.toPerspective());
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
