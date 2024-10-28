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
package net.splitcells.cin.deprecated.raters;

import static net.splitcells.cin.deprecated.raters.TemplateAdherence.templateAdherence;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.type.Cost.cost;

public class TemplateAdherenceTest {
    public void test() {
        final var demandValue = attribute(Integer.class, "demandValue");
        final var supplyValue = attribute(Integer.class, "supplyValue");
        final var template = database("template", demandValue, supplyValue);
        template.addTranslated(list(1, 3));
        template.addTranslated(list(2, 2));
        template.addTranslated(list(3, 1));
        final var testSubject = defineProblem("TemplateAdherenceTest")
                .withDemandAttributes(demandValue)
                .withDemands(list(list(1)
                        , list(2)
                        , list(3)))
                .withSupplyAttributes(supplyValue)
                .withSupplies(list(list(3)
                        , list(2)
                        , list(1)))
                .withConstraint(r -> r.forAll(templateAdherence(template, r.subject().orElseThrow())))
                .toProblem()
                .asSolution();
        testSubject.assign(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        testSubject.assign(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        testSubject.assign(testSubject.demandsFree().orderedLine(0), testSubject.suppliesFree().orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(2));
        testSubject.orderedLines().requireSizeOf(3);
        testSubject.remove(testSubject.orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        testSubject.remove(testSubject.orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(1));
        testSubject.remove(testSubject.orderedLine(0));
        testSubject.constraint().rating().requireEqualsTo(cost(0));
        testSubject.orderedLines().requireSizeOf(0);
    }
}
