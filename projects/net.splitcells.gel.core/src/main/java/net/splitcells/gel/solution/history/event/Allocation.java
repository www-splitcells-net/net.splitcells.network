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
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.view.Line;

import static net.splitcells.dem.lang.tree.TreeI.tree;
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
