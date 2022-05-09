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
package net.splitcells.sep.test.functionality;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.resource.communication.log.Domsole;
import net.splitcells.dem.resource.communication.log.IsEchoToFile;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.GelDev;
import net.splitcells.gel.GelEnv;
import net.splitcells.gel.data.database.DatabaseSynchronization;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OnlineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import net.splitcells.gel.solution.optimization.StepType;
import net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedOfflineRepair;
import net.splitcells.sep.Network;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.interaction.UiRouter.uiRouter;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.MathUtils.*;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.lambdas.DescriptiveLambda.describedPredicate;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.database.Databases.database2;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.rater.AllSame.allSame;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.HasMinimalSize.hasMinimalSize;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.*;
import static net.splitcells.gel.rating.rater.RegulatedLength.regulatedLength;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;
import static net.splitcells.sep.Network.network;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
    @Disabled
    @Test
    public static void main(String... args) {
        GelDev.process(() -> {
            var network = registerSchoolScheduling(network(), 15, 20, 30);
            network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, linearInitialization());
            rangeClosed(1, 1).forEach(i -> {
                network.withOptimization(RAILS_FOR_SCHOOL_SCHEDULING, railsForSchoolSchedulingOptimization(4)
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());

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
                network.withOptimization(TEACHER_ALLOCATION_FOR_COURSES, linearInitialization());
                network.withOptimization(TEACHER_ALLOCATION_FOR_COURSES, teacherAllocationForCoursesOptimization()
                        , (currentSolution, step) -> step <= 100 && !currentSolution.isOptimal());
            });
            network.process(RAILS_FOR_SCHOOL_SCHEDULING, Solution::createStandardAnalysis);
            network.process(TEACHER_ALLOCATION_FOR_COURSES, Solution::createStandardAnalysis);
            network.withOptimization(STUDENT_ALLOCATION_FOR_COURSES, linearInitialization());
        }, GelEnv.standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(MessageFilter.class, logMessage -> logMessage.path()
                            .equals(list("demands", "Solution", "isComplete", "optimize", "after", "cost")))
                    .withConfigValue(Domsole.class, uiRouter(env.config().configValue(MessageFilter.class)));
        }));
    }

    /**
     * Select grouping according to {@link net.splitcells.gel.constraint.type.ForAll}{@link #COURSE_ID}.
     */
    public static OnlineOptimization teacherAllocationForCoursesOptimization() {
        final var randomness = randomness();
        return simpleConstraintGroupBasedRepair(c -> list(c.get(1))
                , freeCoursesByTopic -> solution -> {
                    for (final var topic : listWithValuesOf(freeCoursesByTopic.keySet()).shuffle(randomness)) {
                        // TODO Use constraint system for complex queries.
                        final var suitableCourseSubjects = freeCoursesByTopic
                                .get(topic)
                                .stream()
                                .map(e -> e.value(SUBJECT))
                                .collect(Lists.toList())
                                .shuffle(randomness);
                        for (var suitableCourseSubject : suitableCourseSubjects) {
                            final var suitableTeachers = solution.suppliesFree()
                                    .lookup(TEACH_SUBJECT_SUITABILITY, suitableCourseSubject)
                                    .getLines();
                            if (suitableTeachers.isEmpty()) {
                                continue;
                            }
                            final var suitableTeacher = randomness.chooseOneOf(suitableTeachers).value(TEACHER);
                            final var suitableCourses = solution.demandsFree()
                                    .lookup(SUBJECT, suitableCourseSubject)
                                    .getLines();
                            if (suitableCourses.isEmpty()) {
                                continue;
                            }
                            final var fittingCourseId = suitableCourses
                                    .get(0)
                                    .value(COURSE_ID);
                            final var freeCourseSlots = solution.demandsFree()
                                    .lookup(COURSE_ID, fittingCourseId)
                                    .getLines()
                                    .shuffle(randomness);
                            for (final var freeCourseSlot : freeCourseSlots) {
                                final var teacherCapacity = solution
                                        .suppliesFree()
                                        .lookup(TEACHER, suitableTeacher)
                                        .getLines();
                                if (!teacherCapacity.isEmpty()) {
                                    solution.allocate(freeCourseSlot, teacherCapacity.shuffle(randomness).get(0));
                                }
                            }
                        }
                    }
                }
        );
    }

    public static OfflineOptimization railsForSchoolSchedulingOptimization(int minimumConstraintGroupPath) {
        final var randomness = randomness();
        // TODO Split up into multiple methods for better overview and documentation.
        return ConstraintGroupBasedOfflineRepair.simpleConstraintGroupBasedOfflineRepair(groupSelector(randomness, minimumConstraintGroupPath
                        , 1)
                , (freeDemandGroups, freeSupplies) -> solution -> {
                    final var allocatedCourses = Maps.<Integer, Set<Line>>map();
                    solution.columnView(COURSE_ID).values().stream().distinct()
                            .forEach(e -> allocatedCourses.put(e, setOfUniques()));
                    allocatedCourses.keySet().forEach(course -> allocatedCourses.get(course)
                            .addAll(solution.columnView(COURSE_ID).lookup(course).getLines()));
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
                            .addAll(solution.demands().columnView(COURSE_ID).lookup(course).getLines()));
                    final var targetedCourseHours = Maps.<Integer, Integer>map();
                    allCourses.keySet().forEach(course
                            -> targetedCourseHours.put(course
                            , allCourses.get(course)
                                    .iterator()
                                    .next()
                                    .value(COURSE_LENGTH)));
                    final List<OptimizationEvent> optimization = list();
                    final var freeNulls = solution.suppliesFree()
                            .columnView(ALLOCATED_HOURS)
                            .lookup(0)
                            .getLines();
                    final Map<Integer, List<Line>> freeSuppliesByAllocatedHours = map();
                    solution.suppliesFree()
                            .getLines()
                            .stream()
                            .filter(l -> l.value(ALLOCATED_HOURS) != 0)
                            .forEach(line ->
                                    freeSuppliesByAllocatedHours.addIfAbsent(line.value(ALLOCATED_HOURS), Lists::list).add(line)
                            );
                    freeDemandGroups.values()
                            .stream()
                            .reduce(Sets::merge)
                            .get()
                            .stream()
                            .map(d -> d.value(COURSE_ID))
                            .distinct()
                            .forEach(course -> {
                                final var allocatedHours = allocatedCourseHours.getOrDefault(course, 0);
                                final var targetedHours = targetedCourseHours.get(course);
                                final var potentialCourseGroup = freeDemandGroups.keySet().stream()
                                        .filter(courseKey -> freeDemandGroups.get(courseKey).iterator().next()
                                                .value(COURSE_ID)
                                                .equals(course))
                                        .findFirst();
                                // TODO This seems to be a hack or does not make sense.
                                if (potentialCourseGroup.isEmpty()) {
                                    return;
                                }
                                final var courseGroup = potentialCourseGroup.get();
                                final var freeSlots = freeDemandGroups.get(courseGroup)
                                        .stream()
                                        .collect(toList());
                                final var possibleNonEmptySlotCount = sumsForTarget(targetedHours - allocatedHours
                                        , solution.suppliesFree()
                                                .columnView(ALLOCATED_HOURS)
                                                .values()
                                                .stream()
                                                .distinct()
                                                .filter(e -> e != 0)
                                                .collect(toList()))
                                        .stream()
                                        .map(e -> e.size())
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
                                final var possibleSplits = sumsForTarget(targetedHours - allocatedHours
                                        , solution.suppliesFree()
                                                .columnView(ALLOCATED_HOURS)
                                                .values()
                                                .stream()
                                                .distinct()
                                                .filter(e -> e != 0)
                                                .collect(toList()))
                                        .stream()
                                        .filter(e -> e.size() == nonEmptySlotCount)
                                        .collect(toList());
                                if (!possibleSplits.isEmpty()) {
                                    final var chosenSplit = randomness.chooseOneOf(possibleSplits);
                                    final var chosenRails = randomness.chooseAtMostMultipleOf(chosenSplit.size()
                                            , solution.suppliesFree()
                                                    .getLines()
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
                                    throw new RuntimeException();
                                }
                            });
                    return optimization;
                });
        /* TODO REMOVE this when the obove works.
        return simpleConstraintGroupBasedRepair(groupSelector(randomness(), minimumConstraintGroupPath
                        , 1)
                , supplySelector());*/
    }

    @Tag(INTEGRATION_TEST)
    @Test
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
        railsForSchoolScheduling.optimize(linearInitialization());
        teacherAllocationForCourses.optimize(linearInitialization());
        studentAllocationsForCourses.optimize(linearInitialization());
        assertThat(railsForSchoolScheduling.isOptimal()).isTrue();
        assertThat(teacherAllocationForCourses.isOptimal()).isTrue();
        assertThat(studentAllocationsForCourses.constraint().rating()).isEqualTo(cost(1));
    }

    @Tag(INTEGRATION_TEST)
    @Test
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
        railsForSchoolScheduling.optimize(linearInitialization());
        teacherAllocationForCourses.optimize(linearInitialization());
        studentAllocationsForCourses.optimize(linearInitialization());
        assertThat(railsForSchoolScheduling.isOptimal()).isTrue();
        assertThat(teacherAllocationForCourses.isOptimal()).isTrue();
        studentAllocationsForCourses.createStandardAnalysis();
        assertThat(studentAllocationsForCourses.isOptimal()).isTrue();
    }

    /**
     * TODO
     */
    @Tag(INTEGRATION_TEST)
    @Disabled
    @Test
    public void testSchoolScheduling() {
        schoolScheduling(15, 20, 30);
        fail("Test not implemented");
    }

    private static Network registerSchoolScheduling(Network network, int minimalNumberOfStudentsPerCourse
            , int optimalNumberOfStudentsPerCourse
            , int maximumNumberOfStudentsPerCourse) {
        final var numberOfSubjects = 27;
        network.withNode(RAILS_FOR_SCHOOL_SCHEDULING
                        , defineRailsForSchoolScheduling(2
                                , numberOfSubjects
                                , 30
                                , 410d / 158d
                                , 4
                                , 17))
                .withNode(TEACHER_ALLOCATION_FOR_COURSES
                        , railsForSchoolScheduling ->
                                defineTeacherAllocationForCourses
                                        (railsForSchoolScheduling
                                                , 56
                                                , 158d / 56d
                                                , 158)
                        , RAILS_FOR_SCHOOL_SCHEDULING)
                .withNode(STUDENT_ALLOCATION_FOR_COURSES
                        , teacherAllocationForCourses -> defineStudentAllocationsForCourses
                                (teacherAllocationForCourses
                                        , 92
                                        , 11
                                        , minimalNumberOfStudentsPerCourse
                                        , optimalNumberOfStudentsPerCourse
                                        , maximumNumberOfStudentsPerCourse
                                        , 115
                                        , 11)
                        , TEACHER_ALLOCATION_FOR_COURSES);
        return network;
    }

    private static List<Solution> schoolScheduling(int minimalNumberOfStudentsPerCourse
            , int optimalNumberOfStudentsPerCourse
            , int maximumNumberOfStudentsPerCourse) {
        final var numberOfSubjects = 27;
        final var railsForSchoolScheduling = defineRailsForSchoolScheduling(2
                , numberOfSubjects
                , 30
                , 410d / 158d
                , 4
                , 17);
        final var teacherAllocationForCourses = defineTeacherAllocationForCourses
                (railsForSchoolScheduling
                        , 56
                        , 158d / 56d
                        , 158);
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
                            .mapToObj(i -> Lists.<Object>list(courseId, subject, length, vintage))
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
        rangeClosed(1, numberOfRails).mapToObj(railId ->
                        rangeClosed(1, numberOfVintages * numberOfSubjects * numberOfCourses)
                                .mapToObj(i -> Lists.<Object>list(0, railId))
                                .collect(toList()))
                .flatMap(e -> e.stream())
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
                    r.forAll(COURSE_ID).forAll(noneVoidRail()).then(regulatedLength(COURSE_LENGTH, ALLOCATED_HOURS));
                    r.forAll(RAIL).forAll(noneVoidRail()).then(allSame(ALLOCATED_HOURS));
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
    private static Solution defineTeacherAllocationForCourses(Solution solution, int numberOfTeachers
            , double averageNumberOfCoursesPerTeacher
            , int numberOfCourses) {
        final var randomness = randomness();
        final var teacherCapacity = rangeClosed(1, numberOfTeachers)
                .mapToObj(teacher ->
                        rangeClosed(1, randomness.integer
                                (1
                                        , averageNumberOfCoursesPerTeacher
                                        , roundToInt(2 * averageNumberOfCoursesPerTeacher)))
                                .mapToObj(iSubject -> Lists.<Object>list(teacher, randomness.integer(1, numberOfCourses)))
                                .collect(toList()))
                .flatMap(e -> e.stream())
                .collect(toList());
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
                    r.forAll(RAIL).forAll(TEACHER).then(hasSize(1));
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
        final var supplies = database2(solution.headerView());
        solution.synchronize(new DatabaseSynchronization() {
            @Override
            public void registerAddition(Line line) {
                rangeClosed(1, maximumNumberOfStudentsPerCourse).forEach(i -> supplies.addTranslated(line.values()));
            }

            @Override
            public void registerBeforeRemoval(Line line) {
                supplies.columnView(COURSE_ID)
                        .lookup(line.value(COURSE_ID))
                        .getLines()
                        .forEach(supplies::remove);
            }
        });
        return defineProblem("Student To Courses Allocation")
                .withDemandAttributes(STUDENT, REQUIRED_SUBJECT, STUDENT_S_VINTAGE)
                .withDemands(studentDemands)
                .withSupplies(supplies)
                .withConstraint(r -> {
                    r.then(lineValueRater(line -> line.value(SUBJECT).equals(line.value(REQUIRED_SUBJECT))
                            , "Student gets course in required subject."));
                    r.then(lineValueRater(line -> line.value(STUDENT_S_VINTAGE).equals(line.value(COURSE_S_VINTAGE))
                            , "Student gets courses of the same vintage."));
                    r.forAll(RAIL).forAll(STUDENT).then(allSame(COURSE_ID));
                    r.forAll(COURSE_ID).then(hasMinimalSize(minimalNumberOfStudentsPerCourse));
                    r.forAll(COURSE_ID).then(hasSize(optimalNumberOfStudentsPerCourse));
                    return r;
                })
                .toProblem()
                .asSolution();
    }

}
