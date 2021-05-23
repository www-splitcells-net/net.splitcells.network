package net.splitcells.gel.test.functionality;

import net.splitcells.gel.data.database.DatabaseSynchronization;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.rater.RegulatedLength;
import net.splitcells.gel.solution.Solution;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.database.Databases.database2;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.rater.AllSame.allSame;
import static net.splitcells.gel.rating.rater.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.*;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static org.assertj.core.api.Assertions.fail;

public class SchoolSchedulingTest {

    private static final Attribute<Integer> TEACHER = attribute(Integer.class, "teacher");
    private static final Attribute<Integer> SUBJECT = attribute(Integer.class, "subject");
    private static final Attribute<Integer> TEACH_SUBJECT_SUITABILITY = attribute(Integer.class
            , "teach subject suitability");
    private static final Attribute<Integer> COURSE_ID = attribute(Integer.class, "course id");
    private static final Attribute<Integer> COURSE_S_VINTAGE = attribute(Integer.class, "course's vintage");
    private static final Attribute<Integer> REQUIRED_HOURS = attribute(Integer.class, "required hours");
    private static final Attribute<Integer> ALLOCATED_HOURS = attribute(Integer.class, "allocated hours");
    private static final Attribute<Integer> RAIL = attribute(Integer.class, "rail");
    private static final Attribute<Integer> STUDENT = attribute(Integer.class, "student");
    private static final Attribute<Integer> STUDENT_S_VINTAGE = attribute(Integer.class, "student's vintage");
    private static final Attribute<Integer> PREFERRED_SUBJECT = attribute(Integer.class, "preferred subject");
    private static final Attribute<Integer> COURSE_POSITION = attribute(Integer.class, "course position");

    /**
     * TODO
     */
    @Tag(INTEGRATION_TEST)
    @Disabled
    @Test
    public void testSchoolScheduling() {
        schoolScheduling(30);
        fail("Test not implemented");
    }

    private Solution schoolScheduling(int maximumStudentsPerCourse) {
        return definePupilAllocationsForCourses
                (defineTeacherAllocationForCourses
                                (defineRailsForSchoolScheduling())
                        , maximumStudentsPerCourse);
    }

    private Solution defineRailsForSchoolScheduling() {
        return defineProblem()
                .withDemandAttributes(COURSE_ID, SUBJECT, COURSE_S_VINTAGE, REQUIRED_HOURS)
                .withSupplyAttributes(ALLOCATED_HOURS, RAIL)
                .withConstraint(r -> {
                    r.forAll(lineValueSelector(line -> line.value(RAIL) == 0))
                            .then(lineValueRater(line -> line.value(ALLOCATED_HOURS) == 0));
                    r.forAll(SUBJECT)
                            .forAll(lineValueSelector(line -> line.value(RAIL) != 0))
                            .then(allDifferent(RAIL));
                    r.forAll(COURSE_ID).then(RegulatedLength.regulatedLength(REQUIRED_HOURS, ALLOCATED_HOURS));
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
    private Solution defineTeacherAllocationForCourses(Solution solution) {
        return defineProblem()
                .withDemands(solution)
                .withSupplyAttributes(TEACHER, TEACH_SUBJECT_SUITABILITY)
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

    private Solution definePupilAllocationsForCourses(Solution solution, int maximumStudentsPerCourse) {
        final var demands = database2(solution.headerView());
        solution.synchronize(new DatabaseSynchronization() {
            @Override
            public void register_addition(Line line) {
                rangeClosed(1, maximumStudentsPerCourse).forEach(i -> demands.addTranslated(line.values()));
            }

            @Override
            public void register_before_removal(Line line) {
                demands.columnView(COURSE_ID)
                        .lookup(line.value(COURSE_ID))
                        .getLines()
                        .forEach(demands::remove);
            }
        });
        return defineProblem()
                .withDemands(demands)
                .withSupplyAttributes2(solution.headerView())
                .withConstraint(r -> {
                    r.then(lineValueRater(line -> line.value(SUBJECT).equals(line.value(PREFERRED_SUBJECT))));
                    r.then(lineValueRater(line -> line.value(STUDENT_S_VINTAGE).equals(line.value(COURSE_S_VINTAGE))));
                    r.forAll(COURSE_POSITION).then(allSame(STUDENT));
                    r.forAll(RAIL).forAll(STUDENT).then(allSame(COURSE_ID));
                    return r;
                })
                .toProblem()
                .toSolution();
    }

}
