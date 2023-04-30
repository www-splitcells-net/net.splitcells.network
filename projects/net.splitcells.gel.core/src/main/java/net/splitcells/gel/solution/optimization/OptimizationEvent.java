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
package net.splitcells.gel.solution.optimization;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.LinePointer;
import org.w3c.dom.Node;

import static net.splitcells.dem.lang.Xml.attribute;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

/**
 * To have whatsoever he wishes is in no man’s power;
 * it is in his power not to wish for what he has not,
 * but cheerfully to employ what comes to him. - Lucius Annaeus Seneca
 */
public final class OptimizationEvent implements Domable {

    private final LinePointer demand;
    private final LinePointer supply;
    private final StepType stepType;

    public static OptimizationEvent optimizationEvent(StepType stepType, LinePointer demand, LinePointer supply) {
        return new OptimizationEvent(stepType, demand, supply);
    }

    private OptimizationEvent(StepType stepType, LinePointer demand, LinePointer supply) {
        this.stepType = stepType;
        this.demand = demand;
        this.supply = supply;

    }

    public StepType stepType() {
        return stepType;
    }

    public LinePointer supply() {
        return supply;
    }

    public LinePointer demand() {
        return demand;
    }

    @Override
    public Node toDom() {
        final var dom = Xml.elementWithChildren(getClass().getSimpleName());
        dom.setAttribute(StepType.class.getSimpleName(), stepType.name());
        dom.appendChild(Xml.elementWithChildren(Language.DEMAND.value(), demand.toDom()));
        dom.appendChild(Xml.elementWithChildren(Language.SUPPLY.value(), supply.toDom()));
        return dom;
    }

    @Override
    public Perspective toPerspective() {
        return perspective(getClass().getSimpleName())
                .withProperty(StepType.class.getSimpleName(), stepType.name())
                .withProperty(Language.DEMAND.value(), demand.toPerspective())
                .withProperty(Language.SUPPLY.value(), supply.toPerspective());
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(demand.index(), supply.index(), stepType);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof OptimizationEvent) {
            final var other = (OptimizationEvent) arg;
            return demand.equals(other.demand())
                    && supply.equals(other.supply())
                    && stepType.equals(other.stepType());
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return stepType.name() + ": " + demand.index() + ", " + supply.index();
    }
}
