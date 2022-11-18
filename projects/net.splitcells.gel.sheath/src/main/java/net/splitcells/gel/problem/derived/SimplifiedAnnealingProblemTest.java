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
package net.splitcells.gel.problem.derived;

import net.splitcells.gel.constraint.type.Then;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.random.Randomness.assertPlausibility;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.problem.derived.SimplifiedAnnealingProblem.simplifiedAnnealingProblem;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO Make tests deterministic, otherwise the test can cause a error from time to time."
 */
public class SimplifiedAnnealingProblemTest {

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_host_start_rating_with_single_line() {
        final var rating = cost(7);
        final var solution = defineProblem()
                .withDemandAttributes()
                .withEmptyDemands(1)
                .withSupplyAttributes()
                .withEmptySupplies(1)
                .withConstraint(
                        Then.then(constantRater(rating)))
                .toProblem()
                .asSolution();
        assertThat(solution.constraint().rating()).isEqualTo(noCost());
        final var testSubject = simplifiedAnnealingProblem(solution, i -> 1f);
        assertThat(testSubject.constraint().rating()).isEqualTo(noCost());
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_cold_start_rating_with_single_line() {
        final var rating = cost(7);
        final var solution = defineProblem()
                .withDemandAttributes()
                .withEmptyDemands(1)
                .withSupplyAttributes()
                .withEmptySupplies(1)
                .withConstraint(
                        Then.then(constantRater(rating)))
                .toProblem()
                .asSolution();
        assertThat(solution.constraint().rating()).isEqualTo(noCost());
        final var testSubject = simplifiedAnnealingProblem(solution, i -> 0f);
        assertThat(testSubject.constraint().rating()).isEqualTo(noCost());
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_host_start_rating_with_multiple_lines() {
        final var defianceCost = 7d;
        final var lineCount = 3;
        final var solution = defineProblem()
                .withDemandAttributes()
                .withEmptyDemands(lineCount)
                .withSupplyAttributes()
                .withEmptySupplies(lineCount)
                .withConstraint(
                        Then.then(constantRater(cost(defianceCost))))
                .toProblem()
                .asSolution();
        solution.optimize(linearInitialization());
        assertThat(solution.constraint().rating()).isEqualTo(cost(defianceCost * lineCount));
        final var testSubject = simplifiedAnnealingProblem(solution, i -> 1f);
        assertThat(testSubject.constraint().rating()).isEqualTo(noCost());
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void test_cold_start_rating_with_multiple_lines() {
        final var defianceCost = 7d;
        final var lineCount = 3;
        final var solution = defineProblem()
                .withDemandAttributes()
                .withEmptyDemands(lineCount)
                .withSupplyAttributes()
                .withEmptySupplies(lineCount)
                .withConstraint(
                        Then.then(constantRater(cost(defianceCost))))
                .toProblem()
                .asSolution();
        solution.optimize(linearInitialization());
        assertThat(solution.constraint().rating()).isEqualTo(cost(defianceCost * lineCount));
        final var testSubject = simplifiedAnnealingProblem(solution, i -> 0f);
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(defianceCost * lineCount));
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testProbability() {
        final var probability = .5f;
        final var tryCount = 100;
        final var defianceCost = 7d;
        final var lineCount = 3;
        final var solution = defineProblem()
                .withDemandAttributes()
                .withEmptyDemands(lineCount)
                .withSupplyAttributes()
                .withEmptySupplies(lineCount)
                .withConstraint(
                        Then.then(constantRater(cost(defianceCost))))
                .toProblem()
                .asSolution();
        solution.optimize(linearInitialization());
        assertThat(solution.constraint().rating()).isEqualTo(cost(defianceCost * lineCount));
        final var testSubject = simplifiedAnnealingProblem(solution, i -> probability, randomness(1L));
        assertPlausibility(probability, tryCount,
                (int) rangeClosed(1, 100)
                        .mapToObj(i -> testSubject.constraint().rating().equalz(cost(defianceCost * lineCount)))
                        .filter(result -> result)
                        .count());
    }
}
