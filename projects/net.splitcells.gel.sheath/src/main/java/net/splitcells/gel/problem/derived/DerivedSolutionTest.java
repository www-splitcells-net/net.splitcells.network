/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.problem.derived;

import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.table.attribute.AttributeI;
import net.splitcells.gel.rating.type.Cost;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueBasedOnRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

public class DerivedSolutionTest {

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_manipulation_from_start() {
        final var derivedCostManipulationFactor = 11;
        final var complianceValue = 5;
        final var defianceValue = 7;
        final var defianceCost = 3;
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
                .derived(novērtējums ->
                        cost(novērtējums.getContentValue(Cost.class).value()
                                * derivedCostManipulationFactor).asMetaRating());
        assertThat(rootSolution.constraint().rating()).isEqualTo(cost(0));
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(0));
        rootSolution.optimize(linearInitialization());
        assertThat(rootSolution.constraint().rating()).isEqualTo(cost(2 * defianceCost));
        assertThat(testSubject.constraint().rating()).isEqualTo(
                cost(2 * defianceCost * derivedCostManipulationFactor));
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_rating_manipulation_after_solution_optimization() {
        final var derivedCostManipulationFactor = 11;
        final var complianceValue = 5;
        final var defianceValue = 7;
        final var defianceCost = 3;
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
        rootSolution.optimize(linearInitialization());
        final var testSubject
                = rootSolution
                .derived(novērtējums ->
                        cost(novērtējums.getContentValue(Cost.class).value()
                                * derivedCostManipulationFactor).asMetaRating());
        assertThat(rootSolution.constraint().rating()).isEqualTo(cost(2 * defianceCost));
        assertThat(testSubject.constraint().rating()).isEqualTo(
                cost(2 * defianceCost * derivedCostManipulationFactor));
    }
}
