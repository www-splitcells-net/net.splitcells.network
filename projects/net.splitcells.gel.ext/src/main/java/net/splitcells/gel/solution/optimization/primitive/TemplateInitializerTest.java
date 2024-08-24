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
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.gel.data.database.Databases;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.TemplateInitializer.templateInitializer;

public class TemplateInitializerTest {
    @Test
    public void testTemplate() {
        final var demandNumber = attribute(Integer.class, "demand-number");
        final var supplyNumber = attribute(Integer.class, "supply-number");
        final var testProduct = defineProblem()
                .withDemandAttributes(demandNumber)
                .withDemands(list(
                        list(1)
                        , list(1)
                        , list(1)
                        , list(1)
                        , list(2)
                        , list(2)
                ))
                .withSupplyAttributes(supplyNumber)
                .withSupplies(list(
                        list(1)
                        , list(1)
                        , list(2)
                        , list(2)
                        , list(1)
                        , list(1)))
                .withConstraint(forAll())
                .toProblem()
                .asSolution();
        testProduct.optimize(templateInitializer(
                Databases.database(list(demandNumber, supplyNumber)
                        , list
                                (list(2, 1)
                                        , list(2, 1)
                                        , list(1, 2)
                                        , list(1, 2)
                                        , list(1, 1)
                                        , list(1, 1)))));
        testProduct.unorderedLines().get(0).values().requireEquals(list(2, 1));
        testProduct.unorderedLines().get(1).values().requireEquals(list(2, 1));
        testProduct.unorderedLines().get(2).values().requireEquals(list(1, 2));
        testProduct.unorderedLines().get(3).values().requireEquals(list(1, 2));
        testProduct.unorderedLines().get(4).values().requireEquals(list(1, 1));
        testProduct.unorderedLines().get(5).values().requireEquals(list(1, 1));
    }
}
