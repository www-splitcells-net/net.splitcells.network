package net.splitcells.gel.test.functionality;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.database.DatabaseSynchronization;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.RegulatedLength;
import net.splitcells.gel.solution.Solution;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.dem.utils.MathUtils.roundToInt;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.database.Databases.database2;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.rater.AllSame.allSame;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.HasMinimalSize.hasMinimalSize;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.*;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static org.assertj.core.api.Assertions.fail;

public class SchoolSchedulingTest {

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
    private static final Attribute<Integer> COURSE_POSITION = attribute(Integer.class, "course position");

    private List<Object> course(int courseId, int subject, int coursesVintage, int courseLength) {
        return list(courseId, subject, coursesVintage, courseLength);
    }

    private List<Object> railCapacity(int allocatedHours, int rail) {
        return list(allocatedHours, rail);
    }

    private List<Object> teacherCapacity(int teacher, int teachSubjectSuitability) {
        return list(teacher, teachSubjectSuitability);
    }

    private List<Object> studentDemand(int student, int requiredSubject, int studentsVintage) {
        return list(student, requiredSubject, studentsVintage);
    }

    @Disabled
    @Test
    public void currentDevelopment() {
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
        defineStudentAllocationsForCourses(teacherAllocationForCourses
                , studentDemands
                , minimalNumberOfStudentsPerCourse
                , optimalNumberOfStudentsPerCourse
                , maximumNumberOfStudentsPerCourse);
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

    private Solution schoolScheduling(int minimalNumberOfStudentsPerCourse
            , int optimalNumberOfStudentsPerCourse
            , int maximumNumberOfStudentsPerCourse) {
        final var numberOfSubjects = 30;
        return defineStudentAllocationsForCourses
                (defineTeacherAllocationForCourses
                                (defineRailsForSchoolScheduling
                                                (2
                                                        , numberOfSubjects
                                                        , 30
                                                        , 410d / 158d
                                                        , 4
                                                        , 17)
                                        , 56
                                        , 56d / 158d
                                        , numberOfSubjects)
                        , 92
                        , 11
                        , minimalNumberOfStudentsPerCourse
                        , optimalNumberOfStudentsPerCourse
                        , maximumNumberOfStudentsPerCourse
                        , 115
                        , 11);
    }

    private Solution defineRailsForSchoolScheduling(int numberOfVintages, int numberOfSubjects, int numberOfCourses
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
        return defineRailsForSchoolScheduling(courses, railCapacity);
    }

    private Solution defineRailsForSchoolScheduling(List<List<Object>> courses, List<List<Object>> railCapacity) {
        return defineProblem()
                .withDemandAttributes(COURSE_ID, SUBJECT, COURSE_S_VINTAGE, COURSE_LENGTH)
                .withDemands(courses)
                .withSupplyAttributes(ALLOCATED_HOURS, RAIL)
                .withSupplies(railCapacity)
                .withConstraint(r -> {
                    r.forAll(lineValueSelector(line -> line.value(RAIL) == 0))
                            .then(lineValueRater(line -> line.value(ALLOCATED_HOURS) == 0));
                    r.forAll(SUBJECT)
                            .forAll(lineValueSelector(line -> line.value(RAIL) != 0))
                            .then(allDifferent(RAIL));
                    r.forAll(COURSE_ID).then(RegulatedLength.regulatedLength(COURSE_LENGTH, ALLOCATED_HOURS));
                    r.forAll(RAIL).then(allSame(ALLOCATED_HOURS));
                    return r;
                }).toProblem()
                .toSolution();
    }

    /**
     * All {@link #COURSE_ID} in a {@link #RAIL} need to have different teachers,
     * so that all of them can be hold in parallel.
     *
     * @param solution The demands of this problem.
     * @return A problem modelling allocations of teachers to courses.
     */
    private Solution defineTeacherAllocationForCourses(Solution solution, int numberOfTeachers
            , double averageNumberOfSubjectsPerTeacher
            , int numberOfSubjects) {
        final var randomness = randomness();
        final var teacherCapacity = rangeClosed(1, numberOfTeachers)
                .mapToObj(teacher ->
                        rangeClosed(1, randomness.integer
                                (1
                                        , averageNumberOfSubjectsPerTeacher
                                        , roundToInt(2 * averageNumberOfSubjectsPerTeacher)))
                                .mapToObj(iSubject -> Lists.<Object>list(teacher, randomness.integer(1, numberOfSubjects)))
                                .collect(toList()))
                .flatMap(e -> e.stream())
                .collect(toList());
        return defineTeacherAllocationForCourses(solution, teacherCapacity);
    }

    private Solution defineTeacherAllocationForCourses(Solution solution, List<List<Object>> teacherCapacity) {
        return defineProblem()
                .withDemands(solution)
                .withSupplyAttributes(TEACHER, TEACH_SUBJECT_SUITABILITY)
                .withSupplies(teacherCapacity)
                .withConstraint(r -> {
                    r.forAll(COURSE_ID).then(allSame(TEACHER));
                    r.forAll(TEACHER)
                            .then(lineValueRater(line -> line.value(SUBJECT).equals
                                    (line.value(TEACH_SUBJECT_SUITABILITY))));
                    r.forAll(RAIL).forAll(TEACHER).then(hasSize(1));
                    return r;
                })
                .toProblem()
                .toSolution();
    }

    private Solution defineStudentAllocationsForCourses(Solution solution
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

    private Solution defineStudentAllocationsForCourses(Solution solution
            , List<List<Object>> studentDemands
            , int minimalNumberOfStudentsPerCourse
            , int optimalNumberOfStudentsPerCourse
            , int maximumNumberOfStudentsPerCourse) {
        final var supplies = database2(solution.headerView());
        solution.synchronize(new DatabaseSynchronization() {
            @Override
            public void register_addition(Line line) {
                rangeClosed(1, maximumNumberOfStudentsPerCourse).forEach(i -> supplies.addTranslated(line.values()));
            }

            @Override
            public void register_before_removal(Line line) {
                supplies.columnView(COURSE_ID)
                        .lookup(line.value(COURSE_ID))
                        .getLines()
                        .forEach(supplies::remove);
            }
        });
        return defineProblem()
                .withDemandAttributes(STUDENT, REQUIRED_SUBJECT, STUDENT_S_VINTAGE)
                .withDemands(studentDemands)
                .withSupplies(supplies)
                .withConstraint(r -> {
                    r.then(lineValueRater(line -> line.value(SUBJECT).equals(line.value(REQUIRED_SUBJECT))));
                    r.then(lineValueRater(line -> line.value(STUDENT_S_VINTAGE).equals(line.value(COURSE_S_VINTAGE))));
                    r.forAll(COURSE_POSITION).then(allSame(STUDENT));
                    r.forAll(RAIL).forAll(STUDENT).then(allSame(COURSE_ID));
                    r.forAll(COURSE_ID).then(hasMinimalSize(minimalNumberOfStudentsPerCourse));
                    r.forAll(COURSE_ID).then(hasSize(optimalNumberOfStudentsPerCourse));
                    return r;
                })
                .toProblem()
                .toSolution();
    }

}
