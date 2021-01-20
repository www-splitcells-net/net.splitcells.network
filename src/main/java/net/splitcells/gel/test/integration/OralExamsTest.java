package net.splitcells.gel.test.integration;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.solution.Solution;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.floorMod;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.CAPABILITY_TEST;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.ForAlls.forAllCombinations;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.database.Databases.databaseOfFods;
import static net.splitcells.gel.data.database.Databases.objectAttributes;
import static net.splitcells.gel.data.table.attribute.AttributeI.*;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.MinimalDistance.minimalDistance2;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.define_problem;
import static net.splitcells.gel.solution.optimization.meta.Escalator.escalator;
import static net.splitcells.gel.solution.optimization.primitive.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.TemplateInitializer.templateInitializer;
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

    private static class OralExamOptimizationArguments {
        public static OralExamOptimizationArguments create(Solution solution, Rating rating) {
            return new OralExamOptimizationArguments(solution, rating);
        }

        private OralExamOptimizationArguments(Solution solution, Rating rating) {
            this.solution = solution;
            this.rating = rating;
        }

        public final Solution solution;
        public final Rating rating;
    }

    public void testOralExamOptimization(OralExamOptimizationArguments arguments) {
        arguments.solution.optimize(linearInitialization());
        arguments.solution.optimize(escalator(i -> {
                    // TODO
                    //ierobežojumGrupaBalstītsRemonts();
                    //funkcionālsKalnāKāpējs(ierobežojumGrupaBalstītsRemonts(), 2);
                    return constraintGroupBasedRepair(0);
                }
        ));
        arguments.solution.createStandardAnalysis();
        assertThat(arguments.solution.isComplete()).isTrue();
        assertThat(arguments.solution.constraint().rating()).isEqualTo(arguments.rating);
    }

    @Disabled
    @Test
    public void testCurrent() {
        final var testSubject = randomOralExams(88, 177, 40, 41, 2, 4, 5, 6)
                .asSolution();
        final var initialSolutionTemplate = testSubject.dataContainer().resolve("previous").resolve("results.fods");
        if (Files.exists(initialSolutionTemplate)) {
            testSubject.optimize
                    (templateInitializer
                            (databaseOfFods(objectAttributes(testSubject.headerView())
                                    , Xml.parse(initialSolutionTemplate).getDocumentElement())));
        }
        testSubject.optimize(linearInitialization());
        testSubject.optimizeOnce(constraintGroupBasedRepair(4));
        /*IntStream.rangeClosed(1, 10).forEach(j -> {
                    testSubject.optimize(escalator(i -> {
                        return constraintGroupBasedRepair(i);
                    }, 3, 3, 4));
                }

        );*/
        testSubject.createStandardAnalysis();
    }

    @Disabled
    @Tag(INTEGRATION_TEST)
    @TestFactory
    public Stream<DynamicTest> oralExamOptimizationTests() {
        return dynamicTests(this::testOralExamOptimization
                , OralExamOptimizationArguments.create(randomOralExams(1, 1, 1, 1, 1, 1, 1, 1).asSolution()
                        , noCost())
                , OralExamOptimizationArguments.create(two_oral_exams_with_multiple_possible_choices().asSolution()
                        , noCost())
                , OralExamOptimizationArguments.create(two_oral_exams_with_multiple_bad_choices().asSolution()
                        , cost(1))
        );
    }

    private Problem two_oral_exams_with_multiple_possible_choices() {
        return oralExams
                (list
                                (list(1, 1, 1)
                                        , list(1, 1, 1))
                        , list
                                (list(1, 1, 1)
                                        , list(8, 1, 1)));
    }

    private Problem two_oral_exams_with_multiple_bad_choices() {
        return oralExams
                (list
                                (list(1, 1, 1)
                                        , list(1, 1, 1))
                        , list
                                (list(1, 1, 1)
                                        , list(4, 1, 1)));
    }

    public Problem randomOralExams(int studentCount, int examCount, int examinerCount, int checkerCount, int weekCount
            , int examDayCountPerWeek, int shiftsPerDayCount, int roomCount) {
        final var randomness = randomness();
        final List<List<Object>> supplies = list();
        for (int room = 1; room <= roomCount; ++room) {
            for (int examDay = 1; examDay <= examDayCountPerWeek; ++examDay) {
                for (int shift = 1; shift <= shiftsPerDayCount; ++shift) {
                    supplies.add(list(shift, floorMod(examDay, examDayCountPerWeek) + ((examDay / 5) * 7) + 1, room));
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
        return define_problem()
                .withDemandAttributes(STUDENTS, EXAMINER, OBSERVER)
                .withDemands(demands)
                .withSupplyAttributes(DATE, SHIFT, ROOM_NUMBER)
                .withSupplies(supplies)
                .withConstraint
                        (forAll()
                                .withChildren(forAll(OBSERVER)
                                                .withChildren(forAllCombinations(DATE, SHIFT)
                                                        .withChildren(then(hasSize(1))))
                                        , forAll(EXAMINER)
                                                .withChildren(forAllCombinations(DATE, SHIFT)
                                                        .withChildren(then(hasSize(1))))
                                        , forAll(STUDENTS)
                                                .withChildren(forAllCombinations(DATE, SHIFT)
                                                                .withChildren(then(hasSize(1)))
                                                        , then(minimalDistance2(DATE, 3.0))
                                                        , then(minimalDistance2(DATE, 5.0))
                                                )
                                        /** TODO Every examiner and observer wants to minimize the number of days with exams.
                                         * <p/>
                                         * TODO Every examiner and observer wants to minimize the pause between 2 exams of one day.
                                         * <p/>
                                         * TODO Every examiner and observer wants to minimize the number of room switches per day.
                                         */
                                        , forAllCombinations(DATE, SHIFT, ROOM_NUMBER)
                                                .withChildren(then(hasSize(1)))
                                        , studentSpecificConstraints()
                                        , checkerSpecificConstraints()
                                        , examinerSpecificConstraints()
                                )
                        ).toProblem();
    }

    @Tag(CAPABILITY_TEST)
    @Test
    public void testRatingsOfSingleOralExam() {
        Solution testSubject = oralExams(list(list(1, 1, 1)), list(list(1, 1, 1))).asSolution();
        testSubject.optimize(linearInitialization());
        assertThat(testSubject.constraint().rating()).isEqualTo(noCost());
    }

    @Tag(CAPABILITY_TEST)
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
                    .forAllCombinations(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
            ).isEqualTo(cost(1));
            assertThat(testSubject.constraint().query()
                    .forAll(EXAMINER)
                    .forAllCombinations(DATE, SHIFT)
                    .then(hasSize(1))
                    .rating()
            ).isEqualTo(cost(1));
            {
                assertThat
                        (testSubject.constraint().query()
                                .forAll(STUDENTS)
                                .forAllCombinations(DATE, SHIFT)
                                .then(hasSize(1))
                                .rating()
                        ).isEqualTo(cost(1));
                assertThat
                        (testSubject.constraint().query()
                                .forAll(STUDENTS)
                                .then(minimalDistance2(DATE, 3.0))
                                .rating()
                        ).isEqualTo(cost(1));
                assertThat
                        (testSubject.constraint().query()
                                .forAll(STUDENTS)
                                .then(minimalDistance2(DATE, 5.0))
                                .rating()
                        ).isEqualTo(cost(1));
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
                    ).isEqualTo(cost(3));
            assertThat(
                    testSubject.constraint().query()
                            .forAllCombinations(DATE, SHIFT, ROOM_NUMBER)
                            .then(hasSize(1))
                            .rating()
            ).isEqualTo(cost(1));
        }
        assertThat(testSubject.constraint().query().rating()).isEqualTo(cost(6));
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(6));
    }

    @Tag(CAPABILITY_TEST)
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
                            .forAllCombinations(DATE, SHIFT)
                            .then(hasSize(1)).rating()
                    ).isEqualTo(cost(2));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .then(minimalDistance2(DATE, 3.0))
                            .rating()
                    ).isEqualTo(cost(10));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .then(minimalDistance2(DATE, 5.0))
                            .rating()
                    ).isEqualTo(cost(10));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(EXAMINER)
                            .forAllCombinations(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(2));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(OBSERVER)
                            .forAllCombinations(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(2));
            assertThat
                    (testSubject.constraint().query()
                            .forAllCombinations(DATE, SHIFT, ROOM_NUMBER)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(1));
        }
        assertThat(testSubject.constraint().query().rating()).isEqualTo(cost(27));
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(27));
    }

    @Tag(CAPABILITY_TEST)
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
        testSubject.createStandardAnalysis();
        {
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .then(minimalDistance2(DATE, 3.0))
                            .rating()
                    ).isEqualTo(cost(3));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .then(minimalDistance2(DATE, 5.0))
                            .rating()
                    ).isEqualTo(cost(3));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .forAllCombinations(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(1));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(STUDENTS)
                            .rating()
                    ).isEqualTo(cost(7));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(OBSERVER)
                            .forAllCombinations(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(1));
            assertThat
                    (testSubject.constraint().query()
                            .forAll(EXAMINER)
                            .forAllCombinations(DATE, SHIFT)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(cost(1));
            assertThat
                    (testSubject.constraint().query()
                            .forAllCombinations(DATE, SHIFT, ROOM_NUMBER)
                            .then(hasSize(1))
                            .rating()
                    ).isEqualTo(noCost());
        }
        assertThat(testSubject.constraint().query().rating()).isEqualTo(cost(9));
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(9));
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
