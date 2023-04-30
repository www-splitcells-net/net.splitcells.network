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
package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.PerspectiveI;
import org.w3c.dom.Node;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

public class AllocationNaturalArgumentation implements MetaData<String> {
    public static AllocationNaturalArgumentation allocationNaturalArgumentation(String allocationNaturalArgumentation) {
        return new AllocationNaturalArgumentation(allocationNaturalArgumentation);
    }

    private final String allocationNaturalArgumentation;

    private AllocationNaturalArgumentation(String allocationNaturalArgumentation) {
        this.allocationNaturalArgumentation = allocationNaturalArgumentation;
    }

    @Override
    public Node toDom() {
        return perspective(allocationNaturalArgumentation).toDom();
    }

    @Override
    public Perspective toPerspective() {
        return perspective(allocationNaturalArgumentation);
    }

    @Override
    public String value() {
        return allocationNaturalArgumentation;
    }
}
