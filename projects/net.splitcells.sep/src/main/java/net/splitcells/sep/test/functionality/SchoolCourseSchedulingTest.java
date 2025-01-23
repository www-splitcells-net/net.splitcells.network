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
package net.splitcells.sep.test.functionality;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.dem.resource.communication.log.IsEchoToFile;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.GelDev;
import net.splitcells.gel.GelEnv;
import net.splitcells.gel.data.table.TableSynchronization;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OnlineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.StepType;
import net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedOfflineRepair;
import net.splitcells.sep.Network;

import java.util.Optional;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.LoggerRouter.uiRouter;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.MathUtils.*;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.lambdas.DescriptiveLambda.describedPredicate;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.table.Tables.table2;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.rater.lib.AllSame.allSame;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.HasMinimalSize.hasMinimalSize;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.*;
import static net.splitcells.gel.rating.rater.lib.RegulatedLength.regulatedLength;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;
import static net.splitcells.gel.solution.optimization.primitive.repair.SupplySelectors.hillClimber;
import static net.splitcells.sep.Network.network;

public class SchoolCourseSchedulingTest {

    private static final String RAILS_FOR_SCHOOL_SCHEDULING = "rails for school scheduling";
    private static final String TEACHER_ALLOCATION_FOR_COURSES = "teacher allocation for courses";
    private static final String STUDENT_ALLOCATION_FOR_COURSES = "student allocations for courses";

    private static final Attribute<Integer> TEACHER = attribute(Integer.class, "teacher");
    private static final Attribute<Integer> SUBJECT = attribute(Integer.class, "subject");
    private static final Attribute<Integer> TEACH_SUBJECT_SUITABILITY = attribute(Integer.class
            , "teach subject suitability");
    private static final Attribute<Integer> COURSE_ID = attribute(Integer.class, "course id");
    private static final Attribute<Integer> COURSE_S_VINTAGE = attribute(Integer.class, "course's vintage");
    private static final Attribute<Integer> COURSE_LENGTH = attribute(Integer.class, "course length");
    private static final Attribute<Integer> ALLOCATED_HOURS = attribute(Integer.class, "allocated hours");
    private static final Attribute<Integer> RAIL = attribute(Integer.class, "rail");
    private static final Attribute<Integer> STUDENT = attribute(Integer.class, "student");
    private static final Attribute<Integer> STUDENT_S_VINTAGE = attribute(Integer.class, "student's vintage");
    private static final Attribute<Integer> REQUIRED_SUBJECT = attribute(Integer.class, "required subject");

    private static List<Object> course(int courseId, int subject, int coursesVintage, int courseLength) {
        return list(courseId, subject, coursesVintage, courseLength);
    }

    private static List<Object> railCapacity(int allocatedHours, int rail) {
        return list(allocatedHours, rail);
    }

    private static List<Object> teacherCapacity(int teacher, int teachSubjectSuitability) {
        return list(teacher, teachSubjectSuitability);
    }

    private static List<Object> studentDemand(int student, int requiredSubject, int studentsVintage) {
        return list(student, requiredSubject, studentsVintage);
    }

    /**
     * maven.execute net.splitcells.sep.test.functionality.SchoolCourseSchedulingTest -Dnet.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY=false -Dnet.splitcells.dem.environment.config.StaticFlags.TELLING_STORY=true
     *
     * @param args
     */
    public static void main(String... args) {
        GelDev.process(() -> {
            var network = registerSchoolScheduling(network()
                    , 15
                    , 20
                    , 30
                    , 2
                    , 2
                    , 8
                    , 1
                    , 1
                    , 4
                    , 4
                    , 2
                    , 20
                    , 2
                    , 0
                    , 1
            );
            rangeClosed(1, 1).forEach(i -> {
                /* TODO This can be used to demonstrate the problems of implementing own custom supply selector.
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(3, false)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
                network.process(RAILS_FOR_SCHOOL_SCHEDULING, s -> s.createStandardAnalysis(1));*/
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, onlineLinearInitialization());
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, simpleConstraintGroupBasedRepair(groupSelector(randomness(), 3
                                , 1), hillClimber(), false)
                        , (currentSolution, step) -> step <= 10 && !currentSolution.isOptimal());

                /*network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(3)
                        , (currentSolution, step) -> step <= 1 && !currentSolution.isOptimal());
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(4)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());

                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(2)
                        , (currentSolution, step) -> step <= 1 && !currentSolution.isOptimal());
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(4)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());

                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(1)
                        , (currentSolution, step) -> step <= 1 && !currentSolution.isOptimal());
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(4)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());*/
                // TODO REMOVE network.withOptimization(TEACHER_ALLOCATION_FOR_COURSES, linearInitialization());
                network.withOptimization(TEACHER_ALLOCATION_FOR_COURSES, teacherAllocationForCoursesOptimization()
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
                network.process(TEACHER_ALLOCATION_FOR_COURSES, s -> s.createStandardAnalysis(1));
                /*network.withOptimization(TEACHER_ALLOCATION_FOR_COURSES, simpleConstraintGroupBasedRepair(groupSelector(randomness(), 3
                                , 1), hillClimber(), false)
                        , (currentSolution, step) -> step <= 1 && !currentSolution.isOptimal());*/
            });
            network.withOptimization(STUDENT_ALLOCATION_FOR_COURSES, onlineLinearInitialization());
            network.withOptimization(STUDENT_ALLOCATION_FOR_COURSES, studentAllocationOptimization()
                    , (currentSolution, step) -> step <= 3 && !currentSolution.isOptimal());
            network.processAll(Solution::createStandardAnalysis);
        }, GelEnv.standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(MessageFilter.class, logMessage -> logMessage.path().equals(list("demands", "Solution", "isComplete", "optimize", "after", "cost")))
                    .withConfigValue(Logs.class, uiRouter(env.config().configValue(MessageFilter.class)));
        }));
        if (false) {
            // TODO Will be done later. Simpler instance will be solved first.
            GelDev.process(() -> {
                var network = registerSchoolScheduling(network()
                        , 15
                        , 20
                        , 30
                        , 27
                        , 17
                        , 30
                        , 410d / 158d
                        , 4
                        , 56
                        , 3
                        , 11
                        , 115
                        , 11
                        , 92
                        , 2
                );
                rangeClosed(1, 1).forEach(i -> {
                /* TODO This can be used to demonstrate the problems of implementing own custom supply selector.
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(3, false)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
                network.process(RAILS_FOR_SCHOOL_SCHEDULING, s -> s.createStandardAnalysis(1));*/
                    network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, onlineLinearInitialization());
                    network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, simpleConstraintGroupBasedRepair(groupSelector(randomness(), 3
                                    , 1), hillClimber(), false)
                            , (currentSolution, step) -> step <= 10 && !currentSolution.isOptimal());

                /*network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(3)
                        , (currentSolution, step) -> step <= 1 && !currentSolution.isOptimal());
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(4)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());

                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(2)
                        , (currentSolution, step) -> step <= 1 && !currentSolution.isOptimal());
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(4)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());

                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(1)
                        , (currentSolution, step) -> step <= 1 && !currentSolution.isOptimal());
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(4)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());*/
                    // TODO REMOVE network.withOptimization(TEACHER_ALLOCATION_FOR_COURSES, linearInitialization());
                    network.withOptimization(TEACHER_ALLOCATION_FOR_COURSES, teacherAllocationForCoursesOptimization()
                            , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
                    network.process(TEACHER_ALLOCATION_FOR_COURSES, s -> s.createStandardAnalysis(1));
                /*network.withOptimization(TEACHER_ALLOCATION_FOR_COURSES, simpleConstraintGroupBasedRepair(groupSelector(randomness(), 3
                                , 1), hillClimber(), false)
                        , (currentSolution, step) -> step <= 1 && !currentSolution.isOptimal());*/
                });
                network.process(RAILS_FOR_SCHOOL_SCHEDULING, Solution::createStandardAnalysis);
                network.process(TEACHER_ALLOCATION_FOR_COURSES, Solution::createStandardAnalysis);
                network.process(STUDENT_ALLOCATION_FOR_COURSES, Solution::createStandardAnalysis);
                network.withOptimization(STUDENT_ALLOCATION_FOR_COURSES, onlineLinearInitialization());
                network.withOptimization(STUDENT_ALLOCATION_FOR_COURSES, studentAllocationOptimization()
                        , (currentSolution, step) -> step <= 3 && !currentSolution.isOptimal());
            }, GelEnv.standardDeveloperConfigurator().andThen(env -> {
                env.config()
                        .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                        .withConfigValue(IsEchoToFile.class, true)
                        .withConfigValue(MessageFilter.class, logMessage -> logMessage.path().equals(list("demands", "Solution", "isComplete", "optimize", "after", "cost")))
                        .withConfigValue(Logs.class, uiRouter(env.config().configValue(MessageFilter.class)));
            }));
        }
    }

    private static OnlineOptimization studentAllocationOptimization() {
        final var randomness = randomness();
        return simpleConstraintGroupBasedRepair(rootConstraint -> list(rootConstraint.query().constraintPath())
                , freeDemandsByStudentSubjects -> solution -> {
                    for (final var demandByStudentSubject : freeDemandsByStudentSubjects.values()) {
                        for (final var demandOfStudentSubject : demandByStudentSubject) {
                            final var student = demandOfStudentSubject.value(STUDENT);
                            final var requiredSubject = demandOfStudentSubject.value(REQUIRED_SUBJECT);
                            final var studentsVintage = demandOfStudentSubject.value(STUDENT_S_VINTAGE);
                            final var usedRails = solution.lookup(STUDENT, student)
                                    .columnView(RAIL)
                                    .values()
                                    .stream()
                                    .distinct()
                                    .collect(toList());
                            final var fittingRails = solution.suppliesFree()
                                    .columnView(RAIL)
                                    .values()
                                    .stream()
                                    .distinct()
                                    .filter(rail -> !usedRails.contains(rail))
                                    .collect(toList());
                            if (fittingRails.isEmpty()) {
                                return;
                            }
                            randomness.chooseOneOf(fittingRails);
                            fittingRails.shuffle(randomness)
                                    .stream()
                                    .map(rail -> solution.suppliesFree()
                                            .lookup(SUBJECT, requiredSubject)
                                            .lookup(COURSE_S_VINTAGE, studentsVintage)
                                            .lookup(RAIL, rail)
                                            .unorderedLinesStream()
                                            .findFirst())
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .findFirst()
                                    .ifPresent(fittingSupply -> solution.assign(demandOfStudentSubject, fittingSupply));
                        }
                    }
                }
        );
    }

    /**
     * Select grouping according to {@link net.splitcells.gel.constraint.type.ForAll}{@link #COURSE_ID}.
     */
    private static OnlineOptimization teacherAllocationForCoursesOptimization() {
        final var randomness = randomness();
        return simpleConstraintGroupBasedRepair(groupSelector(randomness, 2, 1)
                , freeCoursesByGroupId -> solution -> {
                    final Map<Integer, Set<Line>> allFreeCoursesById = Maps.map();
                    {
                        final var freeCourseIds = solution.demandsFree().unorderedLines().stream()
                                .map(demand -> demand.value(COURSE_ID))
                                .distinct()
                                .collect(toList());
                        for (final var freeCourseId : freeCourseIds) {
                            final var freeCourseInstances = solution.demandsFree().lookup(COURSE_ID, freeCourseId).unorderedLines();
                            allFreeCoursesById.put(freeCourseId, setOfUniques(freeCourseInstances));
                        }
                    }
                    for (final var freeCourseGroupId : freeCoursesByGroupId.keySet()) {
                        final var freeCourseRepresentant = freeCoursesByGroupId.get(freeCourseGroupId).iterator().next();
                        final var freeCourseId = freeCourseRepresentant.value(COURSE_ID);
                        final var sameCourseInstances = solution.allocations()
                                .lookup(COURSE_ID, freeCourseId)
                                .unorderedLines();
                        final Set<Line> newFreeCoursesOfId;
                        if (allFreeCoursesById.containsKey(freeCourseId)) {
                            newFreeCoursesOfId = allFreeCoursesById.get(freeCourseId);
                        } else {
                            newFreeCoursesOfId = setOfUniques();
                            allFreeCoursesById.put(freeCourseId, newFreeCoursesOfId);
                        }
                        newFreeCoursesOfId.addAll(freeCoursesByGroupId.get(freeCourseGroupId));
                        for (final var sameCourseInstance : sameCourseInstances) {
                            final var sameSubjectCourseDemand = solution.demandOfAssignment(sameCourseInstance);
                            newFreeCoursesOfId.add(sameSubjectCourseDemand);
                            solution.remove(sameCourseInstance);
                        }

                    }
                    for (final var freeCourseId : listWithValuesOf(allFreeCoursesById.keySet()).shuffle(randomness)) {
                        final var freeCourseRails = solution.demandsFree()
                                .lookup(COURSE_ID, freeCourseId)
                                .unorderedLines().stream()
                                .map(l -> l.value(RAIL))
                                .collect(toSetOfUniques());
                        final var freeCourseRepresentant = allFreeCoursesById.get(freeCourseId).iterator().next();
                        final var freeCourseSubject = freeCourseRepresentant.value(SUBJECT);
                        final var suitableTeachers = solution.suppliesFree()
                                .lookup(TEACH_SUBJECT_SUITABILITY, freeCourseSubject)
                                .unorderedLines()
                                .stream()
                                .filter(teacher -> {
                                    final var teachersRails = solution.lookup(TEACHER, teacher.value(TEACHER)).unorderedLines().stream()
                                            .map(teacherAllocation -> teacherAllocation.value(RAIL))
                                            .collect(toSetOfUniques());
                                    return !freeCourseRails.containsAny(teachersRails);
                                })
                                .collect(toList());
                        if (suitableTeachers.isEmpty()) {
                            continue;
                        }
                        final var suitableTeacher = randomness.chooseOneOf(suitableTeachers).value(TEACHER);
                        final var freeCourseSlots = solution.demandsFree()
                                .lookup(COURSE_ID, freeCourseId)
                                .unorderedLines()
                                .shuffle(randomness);
                        for (final var freeCourseSlot : freeCourseSlots) {
                            final var teacherCapacity = solution
                                    .suppliesFree()
                                    .lookup(TEACHER, suitableTeacher)
                                    .lookup(TEACH_SUBJECT_SUITABILITY, freeCourseSubject)
                                    .unorderedLines();
                            if (!teacherCapacity.isEmpty()) {
                                solution.assign(freeCourseSlot, teacherCapacity.shuffle(randomness).get(0));
                            }
                        }
                    }
                }
        );
    }

    private static OfflineOptimization railsForSchoolSchedulingOptimization(int minimumConstraintGroupPath, boolean repairCompliants) {
        final var randomness = randomness();
        // TODO Split up into multiple methods for better overview and documentation.
        return ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair(groupSelector(randomness, minimumConstraintGroupPath
                        , 1)
                , (freeDemandGroups, freeSupplies) -> solution -> {
                    final List<OptimizationEvent> optimization = list();
                    final var allocatedCourses = Maps.<Integer, Set<Line>>map();
                    solution.columnView(COURSE_ID).values().stream().distinct()
                            .forEach(e -> allocatedCourses.put(e, setOfUniques()));
                    allocatedCourses.keySet().forEach(course -> allocatedCourses.get(course)
                            .addAll(solution.columnView(COURSE_ID).lookup(course).unorderedLines()));
                    final var allocatedCourseHours = Maps.<Integer, Integer>map();
                    allocatedCourses.keySet().forEach(course -> {
                        allocatedCourseHours.put(course
                                , allocatedCourses.get(course).stream()
                                        .map(c -> c.value(ALLOCATED_HOURS))
                                        .reduce((a, b) -> a + b)
                                        .get());
                    });
                    final var allCourses = Maps.<Integer, Set<Line>>map();
                    solution.demands().columnView(COURSE_ID).values().stream().distinct()
                            .forEach(e -> allCourses.put(e, setOfUniques()));
                    allCourses.keySet().forEach(course -> allCourses.get(course)
                            .addAll(solution.demands().columnView(COURSE_ID).lookup(course).unorderedLines()));
                    final var targetedCourseHours = Maps.<Integer, Integer>map();
                    allCourses.keySet().forEach(course
                            -> targetedCourseHours.put(course
                            , allCourses.get(course)
                                    .iterator()
                                    .next()
                                    .value(COURSE_LENGTH)));
                    final var freeNulls = solution.suppliesFree()
                            .columnView(ALLOCATED_HOURS)
                            .lookup(0)
                            .unorderedLines();
                    final Map<Integer, List<Line>> freeSuppliesByAllocatedHours = map();
                    solution.suppliesFree()
                            .unorderedLines()
                            .stream()
                            .filter(l -> l.value(ALLOCATED_HOURS) != 0)
                            .forEach(line ->
                                    freeSuppliesByAllocatedHours.addIfAbsent(line.value(ALLOCATED_HOURS), Lists::list).add(line)
                            );
                    final var mergedFreeDemandGroups = freeDemandGroups.values()
                            .stream()
                            .reduce(Sets::merge) // ?
                            .orElseGet(() -> setOfUniques());
                    mergedFreeDemandGroups.addAll(solution.demandsFree().unorderedLines());
                    mergedFreeDemandGroups
                            .stream()
                            .map(d -> d.value(COURSE_ID))
                            .distinct()
                            .forEach(course -> {
                                final var allocatedHours = allocatedCourseHours.getOrDefault(course, 0);
                                final var targetedHours = targetedCourseHours.get(course);
                                final List<Line> freeSlots = mergedFreeDemandGroups.stream()
                                        .filter(demand -> demand.value(COURSE_ID).equals(course))
                                        .collect(toList());
                                solution.demandsFree().lookup(COURSE_ID, course).unorderedLines().forEach(l -> {
                                    if (!freeSlots.contains(l)) {
                                        freeSlots.add(l);
                                    }
                                });
                                final var retainedAllocatedHours = solution.lookup(COURSE_ID, course).unorderedLines().stream()
                                        .filter(l -> !freeSlots.contains(solution.demandOfAssignment(l)))
                                        .map(l -> l.value(ALLOCATED_HOURS))
                                        .reduce((a, b) -> a + b)
                                        .orElse(0);
                                final int hoursLeft;
                                if (targetedHours < retainedAllocatedHours) {
                                    // More hours are allocated and not repaired to a course, than needed.
                                    hoursLeft = 0;
                                } else {
                                    hoursLeft = targetedHours - retainedAllocatedHours;
                                }
                                final var possibleNonEmptySlotCount = sumsForTarget(hoursLeft
                                        , solution.suppliesFree()
                                                .columnView(ALLOCATED_HOURS)
                                                .values()
                                                .stream()
                                                .distinct()
                                                .filter(e -> e != 0)
                                                .collect(toList()))
                                        .stream()
                                        .map(e -> e.size())
                                        .filter(e -> e <= freeSlots.size())
                                        .distinct()
                                        .collect(toList());
                                final var nonEmptySlotCount = randomness.chooseOneOf(possibleNonEmptySlotCount);
                                final var emptySlotCount = freeSlots.size() - nonEmptySlotCount;
                                rangeClosed(1, emptySlotCount).forEach(i -> {
                                    final var freeSlot = freeSlots.remove(0);
                                    final var nullHour = freeNulls.remove(0);
                                    optimization.add(optimizationEvent(StepType.ADDITION
                                            , freeSlot.toLinePointer()
                                            , nullHour.toLinePointer()));
                                });
                                final List<List<Integer>> possibleSplits;
                                if (hoursLeft == 0) {
                                    possibleSplits = list();
                                    final List<Integer> emptySplit = list();
                                    rangeClosed(1, freeSlots.size()).forEach(i -> emptySplit.add(0));
                                    possibleSplits.add(emptySplit);
                                } else {
                                    final var splitComponents = solution.suppliesFree()
                                            .columnView(ALLOCATED_HOURS)
                                            .values()
                                            .stream()
                                            .filter(e -> e != 0)
                                            .distinct()
                                            .collect(toList());
                                    possibleSplits = sumsForTarget(hoursLeft, splitComponents, freeSlots.size())
                                            .stream()
                                            .filter(e -> e.size() == nonEmptySlotCount)
                                            .collect(toList());
                                }
                                if (!possibleSplits.isEmpty()) {
                                    final var chosenSplit = randomness.chooseOneOf(possibleSplits);
                                    final var chosenRails = randomness.chooseAtMostMultipleOf(chosenSplit.size()
                                            , solution.suppliesFree()
                                                    .unorderedLines()
                                                    .stream()
                                                    .filter(l -> l.value(ALLOCATED_HOURS) != 0)
                                                    .map(l -> l.value(RAIL))
                                                    .distinct()
                                                    .collect(toList()));
                                    chosenSplit.forEach(e
                                            -> {
                                        final var chosenRail = chosenRails.remove(0);
                                        final var chosenSupply = freeSuppliesByAllocatedHours.get(e).stream()
                                                .filter(s -> s.value(RAIL).equals(chosenRail))
                                                .findFirst()
                                                .get();
                                        freeSuppliesByAllocatedHours.get(e).remove(chosenSupply);
                                        optimization.add(optimizationEvent(StepType.ADDITION
                                                , freeSlots.remove(0).toLinePointer()
                                                , chosenSupply.toLinePointer()));
                                    });
                                } else {
                                    throw ExecutionException.execException("No course split found: targetedHours=" + targetedHours + ", retainedAllocatedHours=" + retainedAllocatedHours + ", allocatedHours=" + allocatedHours);
                                }
                            });
                    return optimization;
                }, repairCompliants);
        /* TODO REMOVE this when the obove works.
        return simpleConstraintGroupBasedRepair(groupSelector(randomness(), minimumConstraintGroupPath
                        , 1)
                , supplySelector());*/
    }

    @IntegrationTest
    public void testCourseWithTooManyStudents() {
        final List<List<Object>> courses = list(
                course(1, 1, 1, 2)
                , course(1, 1, 1, 2));
        final List<List<Object>> railCapacity = list(
                railCapacity(1, 1)
                , railCapacity(1, 2));
        final var railsForSchoolScheduling = defineRailsForSchoolScheduling(courses, railCapacity);
        // TODO This should not be multiplied.
        final List<List<Object>> teacherCapacity = list(
                teacherCapacity(1, 1)
                , teacherCapacity(1, 1));
        final var teacherAllocationForCourses
                = defineTeacherAllocationForCourses(railsForSchoolScheduling, teacherCapacity);
        // TODO This should not be multiplied.
        final List<List<Object>> studentDemands = list(
                studentDemand(1, 1, 1)
                , studentDemand(1, 1, 1));
        final int minimalNumberOfStudentsPerCourse = 1;
        final int optimalNumberOfStudentsPerCourse = 1;
        final int maximumNumberOfStudentsPerCourse = 1;
        final var studentAllocationsForCourses = defineStudentAllocationsForCourses(teacherAllocationForCourses
                , studentDemands
                , minimalNumberOfStudentsPerCourse
                , optimalNumberOfStudentsPerCourse
                , maximumNumberOfStudentsPerCourse);
        railsForSchoolScheduling.optimize(offlineLinearInitialization());
        teacherAllocationForCourses.optimize(offlineLinearInitialization());
        studentAllocationsForCourses.optimize(offlineLinearInitialization());
        require(railsForSchoolScheduling.isOptimal());
        require(teacherAllocationForCourses.isOptimal());
        studentAllocationsForCourses.constraint().rating().requireEqualsTo(cost(1));
    }

    @IntegrationTest
    public void testSingleCourseAssignment() {
        final List<List<Object>> courses = list(
                course(1, 1, 1, 1));
        final List<List<Object>> railCapacity = list(
                railCapacity(1, 1));
        final var railsForSchoolScheduling = defineRailsForSchoolScheduling(courses, railCapacity);
        final List<List<Object>> teacherCapacity = list(
                teacherCapacity(1, 1));
        final var teacherAllocationForCourses
                = defineTeacherAllocationForCourses(railsForSchoolScheduling, teacherCapacity);
        final List<List<Object>> studentDemands = list(
                studentDemand(1, 1, 1));
        final int minimalNumberOfStudentsPerCourse = 1;
        final int optimalNumberOfStudentsPerCourse = 1;
        final int maximumNumberOfStudentsPerCourse = 1;
        final var studentAllocationsForCourses = defineStudentAllocationsForCourses(teacherAllocationForCourses
                , studentDemands
                , minimalNumberOfStudentsPerCourse
                , optimalNumberOfStudentsPerCourse
                , maximumNumberOfStudentsPerCourse);
        railsForSchoolScheduling.optimize(offlineLinearInitialization());
        teacherAllocationForCourses.optimize(offlineLinearInitialization());
        studentAllocationsForCourses.optimize(offlineLinearInitialization());
        require(railsForSchoolScheduling.isOptimal());
        require(teacherAllocationForCourses.isOptimal());
    }

    /**
     * TODO
     */
    @DisabledTest
    public void testSchoolScheduling() {
        schoolScheduling(15, 20, 30);
        throw ExecutionException.execException("Test not implemented");
    }

    private static Network registerSchoolScheduling(Network network, int minimalNumberOfStudentsPerCourse
            , int optimalNumberOfStudentsPerCourse
            , int maximumNumberOfStudentsPerCourse
            , int numberOfSubjects
            , int numberOfRails
            , int numberOfCourses
            , double averageCourseLength
            , int maximumCourseLength
            , int numberOfTeachers
            , int teacherChoiceFactor
            , int numberOfSubjectsPerStudentsInSecondVintage
            , int numberOfStudentsInFirstVintage
            , int numberOfSubjectsPerStudentsInVintage
            , int numberOfStudentsInSecondVintage
            , int numberOfVintages
    ) {
        final var averageNumberOfCoursesPerTeacher = numberOfCourses / numberOfTeachers;
        network.withNode(RAILS_FOR_SCHOOL_SCHEDULING
                        , defineRailsForSchoolScheduling(numberOfVintages
                                , numberOfSubjects
                                , numberOfCourses
                                , averageCourseLength
                                , maximumCourseLength
                                , numberOfRails))
                .withNode(TEACHER_ALLOCATION_FOR_COURSES
                        , railsForSchoolScheduling ->
                                defineTeacherAllocationForCourses
                                        (railsForSchoolScheduling
                                                , numberOfTeachers
                                                , averageNumberOfCoursesPerTeacher
                                                , numberOfSubjects
                                                , numberOfRails
                                                , teacherChoiceFactor)
                        , RAILS_FOR_SCHOOL_SCHEDULING)
                .withNode(STUDENT_ALLOCATION_FOR_COURSES
                        , teacherAllocationForCourses -> defineStudentAllocationsForCourses
                                (teacherAllocationForCourses
                                        , numberOfStudentsInSecondVintage
                                        , numberOfSubjectsPerStudentsInSecondVintage
                                        , minimalNumberOfStudentsPerCourse
                                        , optimalNumberOfStudentsPerCourse
                                        , maximumNumberOfStudentsPerCourse
                                        , numberOfStudentsInFirstVintage
                                        , numberOfSubjectsPerStudentsInVintage)
                        , TEACHER_ALLOCATION_FOR_COURSES);
        return network;
    }

    private static List<Solution> schoolScheduling(int minimalNumberOfStudentsPerCourse
            , int optimalNumberOfStudentsPerCourse
            , int maximumNumberOfStudentsPerCourse) {
        final var numberOfSubjects = 27;
        final var numberOfRails = 17;
        final var railsForSchoolScheduling = defineRailsForSchoolScheduling(2
                , numberOfSubjects
                , 30
                , 410d / 158d
                , 4
                , numberOfRails);
        final var teacherAllocationForCourses = defineTeacherAllocationForCourses
                (railsForSchoolScheduling
                        , 56
                        , 158d / 56d
                        , numberOfSubjects
                        , numberOfRails
                        , 3);
        final var studentAllocationsForCourses = defineStudentAllocationsForCourses
                (teacherAllocationForCourses
                        , 92
                        , 11
                        , minimalNumberOfStudentsPerCourse
                        , optimalNumberOfStudentsPerCourse
                        , maximumNumberOfStudentsPerCourse
                        , 115
                        , 11);
        return list(railsForSchoolScheduling, teacherAllocationForCourses, studentAllocationsForCourses);
    }

    private static Solution defineRailsForSchoolScheduling(int numberOfVintages, int numberOfSubjects, int numberOfCourses
            , double averageCourseLength
            , int maximumCourseLength
            , int numberOfRails) {
        final var randomness = randomness();
        final var courses = rangeClosed(1, numberOfCourses)
                .mapToObj(courseId -> {
                    final var subject = randomness.integer(1, numberOfSubjects);
                    final var length = randomness.integer(1, averageCourseLength, maximumCourseLength);
                    final var vintage = randomness.integer(1, numberOfVintages);
                    return rangeClosed(1, length)
                            .mapToObj(i -> Lists.<Object>list(courseId, subject, vintage, length))
                            .collect(toList());
                })
                .flatMap(e -> e.stream())
                .collect(toList());
        final var railCapacity = rangeClosed(1, numberOfRails)
                .mapToObj(railId ->
                        rangeClosed(1, courses.size())
                                .mapToObj(i -> rangeClosed(1, maximumCourseLength)
                                        .mapToObj(railLength -> Lists.<Object>list(railLength, railId))
                                        .collect(toList())
                                )
                                .flatMap(e -> e.stream())
                                .collect(toList())
                )
                .flatMap(e -> e.stream())
                .collect(toList());
        rangeClosed(1, numberOfVintages * numberOfSubjects * numberOfCourses)
                .mapToObj(i -> Lists.<Object>list(0, 1))
                .forEach(railCapacity::add);
        return defineRailsForSchoolScheduling(courses, railCapacity);
    }

    private static Rater voidRail() {
        return lineValueSelector(describedPredicate(line -> line.value(ALLOCATED_HOURS) == 0, "void rail"));
    }

    private static Rater noneVoidRail() {
        return lineValueSelector(describedPredicate(line -> line.value(ALLOCATED_HOURS) != 0, "none void rail"));
    }

    private static Solution defineRailsForSchoolScheduling(List<List<Object>> courses, List<List<Object>> railCapacity) {
        return defineProblem("Rails For School Scheduling")
                .withDemandAttributes(COURSE_ID, SUBJECT, COURSE_S_VINTAGE, COURSE_LENGTH)
                .withDemands(courses)
                .withSupplyAttributes(ALLOCATED_HOURS, RAIL)
                .withSupplies(railCapacity)
                .withConstraint(r -> {
                    // TODO Does this make sense?
                    r.forAll(voidRail()).then(lineValueRater(line -> line.value(ALLOCATED_HOURS) == 0));
                    r.forAll(COURSE_ID).forAll(noneVoidRail()).then(allDifferent(RAIL));
                    r.forAll(COURSE_ID).then(regulatedLength(COURSE_LENGTH, ALLOCATED_HOURS));
                    r.forAll(RAIL).then(allSame(ALLOCATED_HOURS));
                    return r;
                }).toProblem()
                .asSolution();
    }

    /**
     * All {@link #COURSE_ID} in a {@link #RAIL} need to have different teachers,
     * so that all of them can be hold in parallel.
     *
     * @param solution The demands of this problem.
     * @return A problem modelling allocations of teachers to courses.
     */
    private static Solution defineTeacherAllocationForCourses(Solution solution
            , int numberOfTeachers
            , double averageNumberOfCoursesPerTeacher
            , int numberOfSubjects
            , int numberOfRails
            , int teacherChoiceFactor) {
        final var randomness = randomness();
        final List<List<Object>> teacherCapacity = list();
        for (int teacher = 1; teacher <= numberOfTeachers; ++teacher) {
            final var teachersCourseCount = randomness.integer(1
                    , averageNumberOfCoursesPerTeacher
                    , roundToInt(2 * averageNumberOfCoursesPerTeacher));
            for (int courseIndex = 1; courseIndex <= teachersCourseCount; ++courseIndex) {
                for (int choiceCount = 1; choiceCount <= teacherChoiceFactor; ++choiceCount) {
                    final var teachersSubject = randomness().integer(1, numberOfSubjects);
                    for (int railsCount = 1; railsCount <= numberOfRails; ++railsCount) {
                        teacherCapacity.add(list(teacher, teachersSubject));
                    }
                }
            }
        }
        return defineTeacherAllocationForCourses(solution, teacherCapacity);
    }

    private static Solution defineTeacherAllocationForCourses(Solution solution, List<List<Object>> teacherCapacity) {
        return defineProblem("Teacher Allocation For Courses")
                .withDemands(solution)
                .withSupplyAttributes(TEACHER, TEACH_SUBJECT_SUITABILITY)
                .withSupplies(teacherCapacity)
                .withConstraint(r -> {
                    r.forAll(COURSE_ID).then(allSame(TEACHER));
                    r.forAll(TEACHER)
                            .then(lineValueRater(line -> line.value(SUBJECT).equals
                                            (line.value(TEACH_SUBJECT_SUITABILITY))
                                    , "Teacher holds only suitable subjects."));
                    r.forAll(RAIL).forAll(noneVoidRail()).forAll(TEACHER).then(hasSize(1));
                    return r;
                })
                .toProblem()
                .asSolution();
    }

    private static Solution defineStudentAllocationsForCourses(Solution solution
            , int numberOfStudentsInSecondVintage
            , int numberOfSubjectsPerStudentsInSecondVintage
            , int minimalNumberOfStudentsPerCourse
            , int optimalNumberOfStudentsPerCourse
            , int maximumNumberOfStudentsPerCourse
            , int numberOfStudentsInFirstVintage
            , int numberOfSubjectsPerStudentsInFirstVintage) {
        final var firstVintageStudentDemands = rangeClosed(1, numberOfStudentsInFirstVintage)
                .mapToObj(student -> rangeClosed(1, numberOfSubjectsPerStudentsInFirstVintage)
                        .mapToObj(studentSubject -> Lists.<Object>list(student, studentSubject, 1))
                        .collect(toList()))
                .flatMap(e -> e.stream())
                .collect(toList());
        final var secondVintageStudentDemands = rangeClosed(1, numberOfStudentsInSecondVintage)
                .mapToObj(student -> rangeClosed(1, numberOfSubjectsPerStudentsInSecondVintage)
                        .mapToObj(studentSubject -> Lists.<Object>list(student, studentSubject, 2))
                        .collect(toList()))
                .flatMap(e -> e.stream())
                .collect(toList());
        return defineStudentAllocationsForCourses(solution
                , concat(firstVintageStudentDemands, secondVintageStudentDemands)
                , minimalNumberOfStudentsPerCourse
                , optimalNumberOfStudentsPerCourse
                , maximumNumberOfStudentsPerCourse);
    }

    private static Solution defineStudentAllocationsForCourses(Solution solution
            , List<List<Object>> studentDemands
            , int minimalNumberOfStudentsPerCourse
            , int optimalNumberOfStudentsPerCourse
            , int maximumNumberOfStudentsPerCourse) {
        final var supplies = table2(solution.headerView());
        solution.synchronize(new TableSynchronization() {
            @Override
            public void registerAddition(Line line) {
                rangeClosed(1, maximumNumberOfStudentsPerCourse).forEach(i -> supplies.addTranslated(line.values()));
            }

            @Override
            public void registerBeforeRemoval(Line line) {
                supplies.columnView(COURSE_ID)
                        .lookup(line.value(COURSE_ID))
                        .unorderedLines()
                        .forEach(supplies::remove);
            }
        });
        return defineProblem("Student To Courses Allocation")
                .withDemandAttributes(STUDENT, REQUIRED_SUBJECT, STUDENT_S_VINTAGE)
                .withDemands(studentDemands)
                .withSupplies(supplies)
                .withConstraint(r -> {
                    r.forAll(STUDENT).forAll(REQUIRED_SUBJECT).then(lineValueRater(line -> line.value(SUBJECT).equals(line.value(REQUIRED_SUBJECT))
                            , "Student gets course in required subject."));
                    r.forAll(STUDENT).forAll(REQUIRED_SUBJECT).then(lineValueRater(line -> line.value(STUDENT_S_VINTAGE).equals(line.value(COURSE_S_VINTAGE))
                            , "Student gets courses of the same vintage."));
                    r.forAll(STUDENT).forAll(REQUIRED_SUBJECT).then(allSame(COURSE_ID));
                    r.forAll(STUDENT).forAll(RAIL).then(hasSize(1));
                    r.forAll(COURSE_ID).then(hasMinimalSize(minimalNumberOfStudentsPerCourse));
                    r.forAll(COURSE_ID).then(hasSize(optimalNumberOfStudentsPerCourse));
                    return r;
                })
                .toProblem()
                .asSolution();
    }

}
