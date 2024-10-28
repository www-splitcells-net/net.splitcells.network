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
package net.splitcells.gel.problem.derived;

import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.view.attribute.AttributeI;
import net.splitcells.gel.rating.type.Cost;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueBasedOnRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;

public class DerivedSolutionTest {

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_manipulation_from_start() {
        final var derivedCostManipulationFactor = 11d;
        final var complianceValue = 5;
        final var defianceValue = 7;
        final var defianceCost = 3d;
        final var attribute = AttributeI.attribute(Integer.class);
        final var rootSolution = defineProblem()
                .withDemandAttributes()
                .withDemands(
                        list()
                        , list()
                        , list()
                        , list())
                .withSupplyAttributes(attribute)
                .withSupplies(
                        list(complianceValue)
                        , list(defianceValue)
                        , list(complianceValue)
                        , list(defianceValue))
                .withConstraint(
                        Then.then(lineValueBasedOnRater(line -> {
                            if (line.value(attribute).equals(complianceValue)) {
                                return noCost();
                            } else {
                                return cost(defianceCost);
                            }
                        })))
                .toProblem()
                .asSolution();
        final var testSubject
                = rootSolution
                .derived(rating ->
                        cost(rating.asMetaRating().getContentValue(Cost.class).value()
                                * derivedCostManipulationFactor).asMetaRating());
        rootSolution.constraint().rating().requireEqualsTo(cost(0));
        testSubject.constraint().rating().requireEqualsTo(cost(0));
        rootSolution.optimize(offlineLinearInitialization());
        rootSolution.constraint().rating().requireEqualsTo(cost(2d * defianceCost));
        testSubject.constraint().rating()
                .requireEqualsTo(cost(2d * defianceCost * derivedCostManipulationFactor));
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_manipulation_after_solution_optimization() {
        final var derivedCostManipulationFactor = 11d;
        final var complianceValue = 5;
        final var defianceValue = 7;
        final var defianceCost = 3d;
        final var attribute = AttributeI.attribute(Integer.class);
        final var rootSolution = defineProblem()
                .withDemandAttributes()
                .withDemands(
                        list()
                        , list()
                        , list()
                        , list())
                .withSupplyAttributes(attribute)
                .withSupplies(
                        list(complianceValue)
                        , list(defianceValue)
                        , list(complianceValue)
                        , list(defianceValue))
                .withConstraint(
                        Then.then(lineValueBasedOnRater(rinda -> {
                            if (rinda.value(attribute).equals(complianceValue)) {
                                return noCost();
                            } else {
                                return cost(defianceCost);
                            }
                        })))
                .toProblem()
                .asSolution();
        rootSolution.optimize(offlineLinearInitialization());
        final var testSubject
                = rootSolution
                .derived(rating ->
                        cost(rating.asMetaRating().getContentValue(Cost.class).value()
                                * derivedCostManipulationFactor).asMetaRating());
        rootSolution.constraint().rating().requireEqualsTo(cost(2d * defianceCost));
        testSubject.constraint().rating()
                .requireEqualsTo(cost(2d * defianceCost * derivedCostManipulationFactor));
    }
}
