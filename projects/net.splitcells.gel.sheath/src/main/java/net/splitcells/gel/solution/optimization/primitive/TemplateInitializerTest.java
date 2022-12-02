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
        testProduct.lines().get(0).values().assertEquals(list(2, 1));
        testProduct.lines().get(1).values().assertEquals(list(2, 1));
        testProduct.lines().get(2).values().assertEquals(list(1, 2));
        testProduct.lines().get(3).values().assertEquals(list(1, 2));
        testProduct.lines().get(4).values().assertEquals(list(1, 1));
        testProduct.lines().get(5).values().assertEquals(list(1, 1));
    }
}
