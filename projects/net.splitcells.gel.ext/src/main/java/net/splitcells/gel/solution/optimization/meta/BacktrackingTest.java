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
package net.splitcells.gel.solution.optimization.meta;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.meta.Backtracking.backtracking;
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
            testData.unorderedLines().requireSizeOf(3);
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
            testData.unorderedLines().requireSizeOf(3);
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
