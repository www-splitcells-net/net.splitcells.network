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
package net.splitcells.gel.solution.optimization.space;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.testing.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.enumerable.Initializer.initializer;
import static net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpaceI.enumerableOptimizationSpace;

public class EnumerableOptimizationSpaceTest {
    @Test
    public void testSimpleHistory() {
        final var testData = defineProblem("testSimpleHistory")
                .withDemandAttributes()
                .withDemands(list
                        (list()
                                , list()
                                , list()))
                .withSupplyAttributes()
                .withSupplies(list
                        (list()
                                , list()
                                , list()))
                .withConstraint(forAll())
                .toProblem()
                .asSolution();
        testData.history().processWithHistory(() -> {
            var testSubject = enumerableOptimizationSpace(testData, initializer());
            testSubject.currentState().lines().requireSizeOf(0);
            testSubject = testSubject.child(0);
            testSubject.currentState().lines().requireSizeOf(1);
            testSubject = testSubject.child(0);
            testSubject.currentState().lines().requireSizeOf(2);
            testSubject = testSubject.parent().get();
            testSubject.currentState().lines().requireSizeOf(1);
            testSubject = testSubject.parent().get();
            testSubject.currentState().lines().requireSizeOf(0);
            bool(testSubject.parent().isEmpty()).required();
        });
    }

    @Test
    public void testDiscoveryPath() {
        final var demandAttribute = integerAttribute("d");
        final var supplyAttribute = integerAttribute("s");
        final var testData = defineProblem("testDiscoveryPath")
                .withDemandAttributes(demandAttribute)
                .withDemands(list
                        (list(1)
                                , list(2)
                                , list(3)
                                , list(4)
                        ))
                .withSupplyAttributes(supplyAttribute)
                .withSupplies(list
                        (list(5)
                                , list(6)
                                , list(7)
                                , list(8)
                        ))
                .withConstraint(forAll())
                .toProblem()
                .asSolution();
        testData.history().processWithHistory(() -> {
            var testSubject = enumerableOptimizationSpace(testData, initializer());
            testSubject.currentState().lines().requireSizeOf(0);

            // Test allocation of first free demand and supply.
            testSubject = testSubject.child(0);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(0), 1);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(0), 5);
            testSubject.currentState().lines().requireSizeOf(1);

            // Test allocation of first free demand and last free supply.
            testSubject = testSubject.child(2);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(0), 1);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(0), 5);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(1), 2);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(1), 8);
            testSubject.currentState().lines().requireSizeOf(2);

            // Test allocation of last free demand  and first free supply.
            testSubject = testSubject.child(2);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(0), 1);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(0), 5);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(1), 2);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(1), 8);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(2), 4);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(2), 6);
            testSubject.currentState().lines().requireSizeOf(3);

            testSubject = testSubject.child(0);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(0), 1);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(0), 5);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(1), 2);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(1), 8);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(2), 4);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(2), 6);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(3), 3);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(3), 7);
            testSubject.currentState().lines().requireSizeOf(4);

            testSubject = testSubject.parent().orElseThrow();
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(0), 1);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(0), 5);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(1), 2);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(1), 8);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(2), 4);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(2), 6);
            testSubject.currentState().lines().requireSizeOf(3);

            testSubject = testSubject.parent().get();
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(0), 1);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(0), 5);
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(1), 2);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(1), 8);
            testSubject.currentState().lines().requireSizeOf(2);

            testSubject = testSubject.parent().get();
            requireEquals(testSubject.currentState().columnView(demandAttribute).get(0), 1);
            requireEquals(testSubject.currentState().columnView(supplyAttribute).get(0), 5);
            testSubject.currentState().lines().requireSizeOf(1);

            testSubject = testSubject.parent().get();
            testSubject.currentState().lines().requireSizeOf(0);
            bool(testSubject.parent().isEmpty()).required();
        });
    }
}
