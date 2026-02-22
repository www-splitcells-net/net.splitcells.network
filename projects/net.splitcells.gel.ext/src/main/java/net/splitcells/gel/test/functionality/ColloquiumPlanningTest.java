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
package net.splitcells.gel.test.functionality;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.lang.tree.TreeI;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.dem.testing.annotations.CapabilityTest;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.utils.random.DeterministicRootSourceSeed;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.ext.GelExtCell;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber;
import net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedOfflineRepair;

import java.time.ZonedDateTime;
import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.MathUtils.intervalClosed;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.type.ForAlls.*;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.hasMinimalDistanceOf;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.meta.OfflineEscalator.escalator;
import static net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber.functionalHillClimber;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;

/**
 * <p>TODO IDEA Test object orientation by making all people an instance of a certain class.</p>
 * <p>TODO IDEA The number of days with exams for a teacher should be smaller or equals to a given number.</p>
 * <p>TODO Pupil and teachers are not available on certain days or at certain shifts in certain days.</p>
 * <p>TODO Preferred days and shifts for pupil and teachers.</p>
 */
public class ColloquiumPlanningTest extends TestSuiteI {
    public static final Attribute<Integer> STUDENTS = integerAttribute("students");
    public static final Attribute<Integer> EXAMINER = integerAttribute("examiner");
    public static final Attribute<Integer> OBSERVER = integerAttribute("observer");
    public static final Attribute<Integer> SHIFT = integerAttribute("shift");
    public static final Attribute<Integer> DATE = integerAttribute("date");
    public static final Attribute<Integer> ROOM_NUMBER = integerAttribute("room-number");

    /**
     * <p>TODO Too many teachers are present. Therefore, the problem is probably too easy.
     * Creating a new harder instance would be best, because these values are of historical value.</p>
     * <p>The number students, examiners etc., where created from a real instance at a school.</p>
     */
    @CapabilityTest
    @DisabledTest
    public void testRandomInstanceSolving() {
        Dem.process(() -> {
            final var testSubject = randomColloquiumPlanning
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
            testSubject.optimize(offlineLinearInitialization());
            testSubject.optimizeWithFunction(ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair(3)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedOfflineRepair(4, 2)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedOfflineRepair(4, 3)
                    , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            testSubject.optimizeWithFunction(ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair(1), (currentSolution, step) ->
                    step <= 100 && !currentSolution.isOptimal());
            require(testSubject.isOptimal());
        }, GelExtCell.standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class, a -> false)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L);
        })).requireErrorFree();
    }

    /**
     * This test shows, that the {@link FunctionalHillClimber} is not able to solve this problem
     * as efficiently as the {@link ConstraintGroupBasedOfflineRepair}.
     * This is done by trying as many allocations via the {@link FunctionalHillClimber} as is done
     * in {@link #testRandomInstanceSolving} via the {@link ConstraintGroupBasedOfflineRepair}.
     */
    @CapabilityTest
    @DisabledTest
    public void testComplexity() {
        Dem.process(() -> {
            final var testSubject = randomColloquiumPlanning
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
            bool(testSubject.isOptimal()).requireFalse();
        }, GelExtCell.standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class, a -> false)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L);
        })).requireErrorFree();
    }

    /**
     * if (Files.exists(initialSolutionTemplate)) {
     * testSubject.optimize
     * (templateInitializer
     * (databaseOfFods(objectAttributes(testSubject.headerView())
     * , Xml.parse(initialSolutionTemplate).getDocumentElement())));
     * }
     */
    @DisabledTest
    public void testCurrentDevelopment() {
        final var testSubject = randomColloquiumPlanning
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
        testSubject.optimize(offlineLinearInitialization());
        intervalClosed(1, 100).forEach(i -> {
            if (testSubject.isOptimal()) {
                return;
            }
            logs().append(
                    TreeI.tree(i + ""
                            , STRING)
                    , () -> list("debugging")
                    , LogLevel.DEBUG);
            testSubject.optimizeWithFunction(ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair(3), (currentSolution, step) -> {
                logs().append(
                        TreeI.tree(ZonedDateTime.now().toString()
                                        + testSubject.constraint().rating().asMetaRating().getContentValue(Cost.class).value()
                                        + currentSolution.isComplete()
                                        + currentSolution.demandsFree().size()
                                , STRING)
                        , () -> list("debugging")
                        , LogLevel.DEBUG);
                return step <= 100 && !currentSolution.isOptimal();
            });
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedOfflineRepair(4, 2), (currentSolution, step) -> {
                logs().append(
                        TreeI.tree(ZonedDateTime.now().toString()
                                        + testSubject.constraint().rating().asMetaRating().getContentValue(Cost.class).value()
                                        + currentSolution.isComplete()
                                        + currentSolution.demandsFree().size()
                                , STRING)
                        , () -> list("debugging")
                        , LogLevel.DEBUG);
                return step <= 100 && !currentSolution.isOptimal();
            });
            testSubject.optimizeWithFunction(simpleConstraintGroupBasedOfflineRepair(4, 3), (currentSolution, step) -> {
                logs().append(
                        TreeI.tree(ZonedDateTime.now().toString()
                                        + testSubject.constraint().rating().asMetaRating().getContentValue(Cost.class).value()
                                        + currentSolution.isComplete()
                                        + currentSolution.demandsFree().size()
                                , STRING)
                        , () -> list("debugging")
                        , LogLevel.DEBUG);
                return step <= 100 && !currentSolution.isOptimal();
            });
            testSubject.optimizeWithFunction(ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair(1), (currentSolution, step) -> {
                logs().append(
                        TreeI.tree(ZonedDateTime.now().toString()
                                        + testSubject.constraint().rating().asMetaRating().getContentValue(Cost.class).value()
                                        + currentSolution.isComplete()
                                        + currentSolution.demandsFree().size()
                                , STRING)
                        , () -> list("debugging")
                        , LogLevel.DEBUG);
                return step <= 100 && !currentSolution.isOptimal();
            });
        });
    }

    public Problem randomColloquiumPlanning(int studentCount, int examCount, int examinerCount, int checkerCount,
                                            int weekCount
            , int examDayCountPerWeek, int shiftsPerDayCount, int roomCount, Randomness randomness) {
        final List<List<Object>> supplies = list();
        for (int room = 1; room <= roomCount; ++room) {
            for (int week = 1; week <= weekCount; ++week) {
                for (int examDay = 1; examDay <= examDayCountPerWeek; ++examDay) {
                    for (int shift = 1; shift <= shiftsPerDayCount; ++shift) {
                        supplies.add
                                (list
                                        (modulus(examDay, examDayCountPerWeek) + 1
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
        return colloquiumPlanning(demands, supplies);
    }

    /**
     * <p>TODO Every examiner and observer wants to minimize the number of days with exams.</p>
     * <p>TODO Every examiner and observer wants to minimize the pause between 2 exams of one day.</p>
     * <p>TODO Every examiner and observer wants to minimize the number of room switches per day.</p>
     */
    public Problem colloquiumPlanning(List<List<Object>> demands, List<List<Object>> supplies) {
        return defineProblem("colloquium-planning")
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
                                                        , then(hasMinimalDistanceOf(DATE, 3.0))
                                                        , then(hasMinimalDistanceOf(DATE, 5.0))
                                                )
                                        , forAllCombinationsOf(DATE, SHIFT, ROOM_NUMBER)
                                                .withChildren(then(hasSize(1)))
                                        , studentSpecificConstraints()
                                        , checkerSpecificConstraints()
                                        , examinerSpecificConstraints()
                                )
                        ).toProblem();
    }

    @IntegrationTest
    public void testRatingsOfSingleExam() {
        Solution testSubject = colloquiumPlanning(list(list(1, 1, 1)), list(list(1, 1, 1))).asSolution();
        testSubject.optimize(offlineLinearInitialization());
        testSubject.constraint().rating().requireEqualsTo(noCost());
    }

    @IntegrationTest
    public void testRatingsOfPeopleWithMultipleExamClones() {
        Solution testSubject = colloquiumPlanning
                (list
                                (list(1, 1, 1)
                                        , list(1, 1, 1))
                        , list(list(1, 1, 1), list(1, 1, 1)))
                .asSolution();
        testSubject.optimize(offlineLinearInitialization());
        {
            testSubject.constraint().query()
                    .forAll(OBSERVER)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(1));
            testSubject.constraint().query()
                    .forAll(EXAMINER)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(1));
            {
                testSubject.constraint().query()
                        .forAll(STUDENTS)
                        .forAllCombinationsOf(DATE, SHIFT)
                        .then(hasSize(1))
                        .rating()
                        .requireEqualsTo(cost(1));
                testSubject.constraint().query()
                        .forAll(STUDENTS)
                        .then(hasMinimalDistanceOf(DATE, 3.0))
                        .rating()
                        .requireEqualsTo(cost(3));
                testSubject.constraint().query()
                        .forAll(STUDENTS)
                        .then(hasMinimalDistanceOf(DATE, 5.0))
                        .rating()
                        .requireEqualsTo(cost(5));
            }
        }
        {
            testSubject.constraint().query()
                    .forAll(OBSERVER)
                    .rating()
                    .requireEqualsTo(cost(1));
            testSubject.constraint().query()
                    .forAll(EXAMINER)
                    .rating()
                    .requireEqualsTo(cost(1));
            testSubject.constraint().query()
                    .forAll(STUDENTS)
                    .rating()
                    .requireEqualsTo(cost(9));
            testSubject.constraint().query()
                    .forAllCombinationsOf(DATE, SHIFT, ROOM_NUMBER)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(1));
        }
        testSubject.constraint().query().rating().requireEqualsTo(cost(12));
        testSubject.constraint().rating().requireEqualsTo(cost(12));
    }

    @IntegrationTest
    public void testRatingsOfExamsInSameTimeslot() {
        Solution testSubject = colloquiumPlanning
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
        testSubject.optimize(offlineLinearInitialization());
        {
            testSubject.constraint().query()
                    .forAll(STUDENTS)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1)).rating()
                    .requireEqualsTo(cost(2));
            testSubject.constraint().query()
                    .forAll(STUDENTS)
                    .then(hasMinimalDistanceOf(DATE, 3.0))
                    .rating()
                    .requireEqualsTo(cost(26));
            testSubject.constraint().query()
                    .forAll(STUDENTS)
                    .then(hasMinimalDistanceOf(DATE, 5.0))
                    .rating()
                    .requireEqualsTo(cost(46));
            testSubject.constraint().query()
                    .forAll(EXAMINER)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(2));
            testSubject.constraint().query()
                    .forAll(OBSERVER)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(2));
            testSubject.constraint().query()
                    .forAllCombinationsOf(DATE, SHIFT, ROOM_NUMBER)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(1));
        }
        testSubject.constraint().query().rating().requireEqualsTo(cost(79));
        testSubject.constraint().rating().requireEqualsTo(cost(79));
    }

    @IntegrationTest
    public void testRatingsOfStudentWithMultipleExamsInSameDay() {
        Solution testSubject = colloquiumPlanning
                (list
                                (list(1, 1, 1)
                                        , list(1, 1, 1)
                                        , list(1, 1, 1))
                        , list
                                (list(1, 1, 1)
                                        , list(1, 2, 1)
                                        , list(1, 1, 2))
                ).asSolution();
        testSubject.optimize(offlineLinearInitialization());
        {
            testSubject.constraint().query()
                    .forAll(STUDENTS)
                    .then(hasMinimalDistanceOf(DATE, 3.0))
                    .rating()
                    .requireEqualsTo(cost(9));
            testSubject.constraint().query()
                    .forAll(STUDENTS)
                    .then(hasMinimalDistanceOf(DATE, 5.0))
                    .rating()
                    .requireEqualsTo(cost(15));
            testSubject.constraint().query()
                    .forAll(STUDENTS)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(1));
            testSubject.constraint().query()
                    .forAll(STUDENTS)
                    .rating()
                    .requireEqualsTo(cost(25));
            testSubject.constraint().query()
                    .forAll(OBSERVER)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(1));
            testSubject.constraint().query()
                    .forAll(EXAMINER)
                    .forAllCombinationsOf(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(cost(1));
            testSubject.constraint().query()
                    .forAllCombinationsOf(DATE, SHIFT, ROOM_NUMBER)
                    .then(hasSize(1))
                    .rating()
                    .requireEqualsTo(noCost());
        }
        testSubject.constraint().query().rating().requireEqualsTo(cost(27));
        testSubject.constraint().rating().requireEqualsTo(cost(27));
    }

    /**
     * TODO
     */
    private Constraint studentSpecificConstraints() {
        return forAll();
    }

    /**
     * TODO
     */
    private Constraint examinerSpecificConstraints() {
        return forAll();
    }

    /**
     * TODO
     */
    private Constraint checkerSpecificConstraints() {
        return forAll();
    }
}
