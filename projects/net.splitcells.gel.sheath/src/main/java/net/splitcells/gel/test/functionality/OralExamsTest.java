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
package net.splitcells.gel.test.functionality;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.dem.resource.host.interaction.MessageFilter;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.dem.utils.random.DeterministicRootSourceSeed;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber;
import net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair;
import org.junit.jupiter.api.*;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.Math.floorMod;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.testing.TestTypes.*;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.GelEnv.*;
import static net.splitcells.gel.constraint.type.ForAlls.*;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.MinimalDistance.has_minimal_distance_of;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.meta.Escalator.escalator;
import static net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber.functionalHillClimber;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO IDEA Test object orientation by making all people an instance of a certain
 * class.
 * <p>
 * TODO IDEA The number of days with exams for a teacher should be smaller or equals to a given number.
 * <p>
 * TODO Pupil and teachers are not available on certain days or at certain shifts in certain days.
 * <p>
 * TODO Prefered days and shifts for pupil and teachers.
 */
public class OralExamsTest extends TestSuiteI {
    public static final Attribute<Integer> STUDENTS = integerAttribute("students");
    public static final Attribute<Integer> EXAMINER = integerAttribute("examiner");
    public static final Attribute<Integer> OBSERVER = integerAttribute("observer");
    public static final Attribute<Integer> SHIFT = integerAttribute("shift");
    public static final Attribute<Integer> DATE = integerAttribute("date");
    public static final Attribute<Integer> ROOM_NUMBER = integerAttribute("room-number");

    @Tag(FUNCTIONAL_TEST)
    @Test
    public void testRandomInstanceSolving() {
        analyseProcess(() -> {
            final var testSubject = randomOralExams
                    (88
                            , 177
                            , 40
                            , 41
                            , 2
                            , 5
                            , 5
                            , 6
                            , randomness(0L))
                    .asSolution();
            testSubject.optimize(linearInitialization());
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedRepair(3)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedRepair(4, 2)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedRepair(4, 3)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedRepair(1), (currentSolution, step) ->
                    step <= 100 && !currentSolution.isOptimal());
            assertThat(testSubject.isOptimal()).isTrue();
        }, standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class, a -> false)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L);
        })).requireErrorFree();
    }

    /**
     * This test shows, that the {@link FunctionalHillClimber} is not able to solve this problem
     * as efficiently as the {@link ConstraintGroupBasedRepair}.
     * This is done by trying as many allocations via the {@link FunctionalHillClimber} as is done
     * in {@link #testRandomInstanceSolving} via the {@link ConstraintGroupBasedRepair}.
     */
    @Tag(FUNCTIONAL_TEST)
    @Test
    public void testComplexity() {
        analyseProcess(() -> {
            final var testSubject = randomOralExams
                    (88
                            , 177
                            , 40
                            , 41
                            , 2
                            , 5
                            , 5
                            , 6
                            , randomness(0L))
                    .asSolution();
            testSubject.optimize(functionalHillClimber(400 * 177));
            assertThat(testSubject.isOptimal()).isFalse();
        }, standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class, a -> false)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L);
        })).requireErrorFree();
    }

    @Disabled
    @Test
    @Deprecated
    public void testCurrentDevelopment() {
        final var testSubject = randomOralExams
                (88
                        , 177
                        , 40
                        , 41
                        , 2
                        , 5
                        , 5
                        , 6
                        , randomness(0L))
                .asSolution();
        final var initialSolutionTemplate = testSubject.dataContainer().resolve("previous").resolve("results.fods");
        /*
        if (Files.exists(initialSolutionTemplate)) {
            testSubject.optimize
                    (templateInitializer
                            (databaseOfFods(objectAttributes(testSubject.headerView())
                                    , Xml.parse(initialSolutionTemplate).getDocumentElement())));
        }*/
        testSubject.optimize(linearInitialization());
        IntStream.rangeClosed(1, 100).forEach(i -> {
            if (testSubject.isOptimal()) {
                return;
            }
            domsole().append(
                    perspective(i + ""
                            , STRING)
                    , () -> list("debugging")
                    , LogLevel.DEBUG);
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedRepair(3), (currentSolution, step) -> {
                domsole().append(
                        perspective(ZonedDateTime.now().toString()
                                        + testSubject.constraint().rating().getContentValue(Cost.class).value()
                                        + currentSolution.isComplete()
                                        + currentSolution.demandsFree().size()
                                , STRING)
                        , () -> list("debugging")
                        , LogLevel.DEBUG);
                return step <= 100 && !currentSolution.isOptimal();
            });
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedRepair(4, 2), (currentSolution, step) -> {
                domsole().append(
                        perspective(ZonedDateTime.now().toString()
                                        + testSubject.constraint().rating().getContentValue(Cost.class).value()
                                        + currentSolution.isComplete()
                                        + currentSolution.demandsFree().size()
                                , STRING)
                        , () -> list("debugging")
                        , LogLevel.DEBUG);
                return step <= 100 && !currentSolution.isOptimal();
            });
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedRepair(4, 3), (currentSolution, step) -> {
                domsole().append(
                        perspective(ZonedDateTime.now().toString()
                                        + testSubject.constraint().rating().getContentValue(Cost.class).value()
                                        + currentSolution.isComplete()
                                        + currentSolution.demandsFree().size()
                                , STRING)
                        , () -> list("debugging")
                        , LogLevel.DEBUG);
                return step <= 100 && !currentSolution.isOptimal();
            });
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedRepair(1), (currentSolution, step) -> {
                domsole().append(
                        perspective(ZonedDateTime.now().toString()
                                        + testSubject.constraint().rating().getContentValue(Cost.class).value()
                                        + currentSolution.isComplete()
                                        + currentSolution.demandsFree().size()
                                , STRING)
                        , () -> list("debugging")
                        , LogLevel.DEBUG);
                return step <= 100 && !currentSolution.isOptimal();
            });
        });
    }

    public Problem randomOralExams(int studentCount, int examCount, int examinerCount, int checkerCount,
                                   int weekCount
            , int examDayCountPerWeek, int shiftsPerDayCount, int roomCount, Randomness randomness) {
        final List<List<Object>> supplies = list();
        for (int room = 1; room <= roomCount; ++room) {
            for (int week = 1; week <= weekCount; ++week) {
                for (int examDay = 1; examDay <= examDayCountPerWeek; ++examDay) {
                    for (int shift = 1; shift <= shiftsPerDayCount; ++shift) {
                        supplies.add
                                (list
                                        (floorMod(examDay, examDayCountPerWeek) + 1
                                                        + (week - 1) * 7
                                                , shift
                                                , room));
                    }
                }
            }
        }
        final List<List<Object>> demands = list();
        for (int student = 1; student <= studentCount; ++student) {
            for (int exam = 1; exam <= examCount / studentCount; ++exam) {
                demands.add(list(student, randomness.integer(1, examinerCount), randomness.integer(1, checkerCount)));
            }
        }
        return oralExams(demands, supplies);
    }

    public Problem oralExams(List<List<Object>> demands, List<List<Object>> supplies) {
        return defineProblem()
                .withDemandAttributes(STUDENTS, EXAMINER, OBSERVER)
                .withDemands(demands)
                .withSupplyAttributes(DATE, SHIFT, ROOM_NUMBER)
                .withSupplies(supplies)
                .withConstraint
                        (forAll()
                                .withChildren(forEach(OBSERVER)
                                                .withChildren(forAllCombinationsOf(DATE, SHIFT)
                                                        .withChildren(then(hasSize(1))))
                                        , forEach(EXAMINER)
                                                .withChildren(forAllCombinationsOf(DATE, SHIFT)
                                                        .withChildren(then(hasSize(1))))
                                        , forEach(STUDENTS)
                                                .withChildren(forAllCombinationsOf(DATE, SHIFT)
                                                                .withChildren(then(hasSize(1)))
                                                        , then(has_minimal_distance_of(DATE, 3.0))
                                                        , then(has_minimal_distance_of(DATE, 5.0))
                                                )
                                        /** TODO Every examiner and observer wants to minimize the number of days with exams.
                                         * <p/>
                                         * TODO Every examiner and observer wants to minimize the pause between 2 exams of one day.
                                         * <p/>
                                         * TODO Every examiner and observer wants to minimize the number of room switches per day.
                                         */
                                        , forAllCombinationsOf(DATE, SHIFT, ROOM_NUMBER)
                                                .withChildren(then(hasSize(1)))
                                        , studentSpecificConstraints()
                                        , checkerSpecificConstraints()
                                        , examinerSpecificConstraints()
                                )
                        ).toProblem();
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testRatingsOfSingleOralExam() {
        Solution testSubject = oralExams(list(list(1, 1, 1)), list(list(1, 1, 1))).asSolution();
        testSubject.optimize(linearInitialization());
        assertThat(testSubject.constraint().rating()).isEqualTo(noCost());
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testRatingsOfPeopleWithMultipleExamClones() {
        Solution testSubject = oralExams
                (list
                                (list(1, 1, 1)
                                        , list(1, 1, 1))
                        , list(list(1, 1, 1), list(1, 1, 1)))
                .asSolution();
        testSubject.optimize(linearInitialization());
        {
            assertThat(testSubject.constraint().query()
                    .forAll(OBSERVER)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
            ).isEqualTo(cost(1));
            assertThat(testSubject.constraint().query()
                    .forAll(EXAMINER)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
            ).isEqualTo(cost(1));
            {
                assertThat
                        (testSubject.constraint().query()
                                .forAll(STUDENTS)
                                .forAllCombinationsOf(DATE, SHIFT)
                                .then(hasSize(1))
                                .rating()
                        ).isEqualTo(cost(1));
                assertThat
                        (testSubject.constraint().query()
                                .forAll(STUDENTS)
                                .then(has_minimal_distance_of(DATE, 3.0))
                                .rating()
                        ).isEqualTo(cost(3));
                assertThat
                        (testSubject.constraint().query()
                                .forAll(STUDENTS)
                                .then(has_minimal_distance_of(DATE, 5.0))
                                .rating()
                        ).isEqualTo(cost(5));
            }
        }
        {
            assertThat
                    (testSubject.constraint().query()
                            .forAll(OBSERVER)
                            .rating()
                    ).isEqualTo(cost(1));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(EXAMINER)
                            .rating()
                    ).isEqualTo(cost(1));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .rating()
                    ).isEqualTo(cost(9));
            assertThat(
                    testSubject.constraint().query()
                            .forAllCombinationsOf(DATE, SHIFT, ROOM_NUMBER)
                            .then(hasSize(1))
                            .rating()
            ).isEqualTo(cost(1));
        }
        assertThat(testSubject.constraint().query().rating()).isEqualTo(cost(12));
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(12));
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testRatingsOfExamsInSameTimeslot() {
        Solution testSubject = oralExams
                (list
                                (list(1, 1, 1)
                                        , list(1, 1, 1)
                                        , list(1, 1, 1)
                                        , list(1, 1, 1)
                                        , list(1, 1, 1))
                        , list
                                (list(1, 1, 1)
                                        , list(1, 1, 1)
                                        , list(1, 1, 2)
                                        , list(1, 2, 2)
                                        , list(2, 1, 2))
                ).asSolution();
        testSubject.optimize(linearInitialization());
        {
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .forAllCombinationsOf(DATE, SHIFT)
                            .then(hasSize(1)).rating()
                    ).isEqualTo(cost(2));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .then(has_minimal_distance_of(DATE, 3.0))
                            .rating()
                    ).isEqualTo(cost(26));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .then(has_minimal_distance_of(DATE, 5.0))
                            .rating()
                    ).isEqualTo(cost(46));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(EXAMINER)
                            .forAllCombinationsOf(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(2));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(OBSERVER)
                            .forAllCombinationsOf(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(2));
            assertThat
                    (testSubject.constraint().query()
                            .forAllCombinationsOf(DATE, SHIFT, ROOM_NUMBER)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(1));
        }
        assertThat(testSubject.constraint().query().rating()).isEqualTo(cost(79));
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(79));
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testRatingsOfStudentWithMultipleExamsInSameDay() {
        Solution testSubject = oralExams
                (list
                                (list(1, 1, 1)
                                        , list(1, 1, 1)
                                        , list(1, 1, 1))
                        , list
                                (list(1, 1, 1)
                                        , list(1, 2, 1)
                                        , list(1, 1, 2))
                ).asSolution();
        testSubject.optimize(linearInitialization());
        {
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .then(has_minimal_distance_of(DATE, 3.0))
                            .rating()
                    ).isEqualTo(cost(9));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .then(has_minimal_distance_of(DATE, 5.0))
                            .rating()
                    ).isEqualTo(cost(15));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .forAllCombinationsOf(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(1));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .rating()
                    ).isEqualTo(cost(25));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(OBSERVER)
                            .forAllCombinationsOf(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(1));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(EXAMINER)
                            .forAllCombinationsOf(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(1));
            assertThat
                    (testSubject.constraint().query()
                            .forAllCombinationsOf(DATE, SHIFT, ROOM_NUMBER)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(noCost());
        }
        assertThat(testSubject.constraint().query().rating()).isEqualTo(cost(27));
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(27));
    }

    /**
     * TODO
     */
    private Constraint studentSpecificConstraints() {
        Constraint rVal = forAll();
        return rVal;
    }

    /**
     * TODO
     */
    private Constraint examinerSpecificConstraints() {
        Constraint rVal = forAll();
        return rVal;
    }

    /**
     * TODO
     */
    private Constraint checkerSpecificConstraints() {
        Constraint rVal = forAll();
        return rVal;
    }
}
