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
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.atom.Integers;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.meta.Backtracking.backtracking;
import static net.splitcells.gel.solution.optimization.primitive.enumerable.Initializer.initializer;
import static net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpaceI.enumerableOptimizationSpace;

public class BacktrackingTest {
    @Test
    public void testSimplestCase() {
        final var demandAttribute = integerAttribute("d");
        final var supplyAttribute = integerAttribute("s");
        final var testData = defineProblem()
                .withDemandAttributes(demandAttribute)
                .withDemands(list
                        (list(1)
                                , list(2)
                                , list(3)))
                .withSupplyAttributes(supplyAttribute)
                .withSupplies(list
                        (list(4)
                                , list(5)
                                , list(6)))
                .withConstraint(forAll())
                .toProblem()
                .asSolution();
        testData.history().processWithHistory(() -> {
            backtracking().optimize(testData);
            testData.lines().requireSizeOf(3);
            requireEqualInts(testData.columnView(demandAttribute).get(0), 1);
            requireEqualInts(testData.columnView(supplyAttribute).get(0), 4);
            requireEqualInts(testData.columnView(demandAttribute).get(1), 2);
            requireEqualInts(testData.columnView(supplyAttribute).get(1), 5);
            requireEqualInts(testData.columnView(demandAttribute).get(2), 3);
            requireEqualInts(testData.columnView(supplyAttribute).get(2), 6);
        });
    }

    @Test
    public void testBranching() {
        final var demandAttribute = integerAttribute("d");
        final var supplyAttribute = integerAttribute("s");
        final var testData = defineProblem("testBranching")
                .withDemandAttributes(demandAttribute)
                .withDemands(list
                        (list(1)
                                , list(2)
                                , list(3)))
                .withSupplyAttributes(supplyAttribute)
                .withSupplies(list
                        (list(4)
                                , list(5)
                                , list(6)))
                .withConstraint(c -> {
                    c.forAll(lineValueRater(l -> l.value(demandAttribute).equals(2)))
                            .then(lineValueRater(l -> l.value(supplyAttribute).equals(6)));
                    return c;
                })
                .toProblem()
                .asSolution();
        testData.history().processWithHistory(() -> {
            backtracking().optimize(testData);
            testData.lines().requireSizeOf(3);
            requireEqualInts(testData.columnView(demandAttribute).get(0), 1);
            requireEqualInts(testData.columnView(supplyAttribute).get(0), 4);
            requireEqualInts(testData.columnView(demandAttribute).get(1), 2);
            requireEqualInts(testData.columnView(supplyAttribute).get(1), 6);
            requireEqualInts(testData.columnView(demandAttribute).get(2), 3);
            requireEqualInts(testData.columnView(supplyAttribute).get(2), 5);
            require(testData.isOptimal());
        });
    }
}
