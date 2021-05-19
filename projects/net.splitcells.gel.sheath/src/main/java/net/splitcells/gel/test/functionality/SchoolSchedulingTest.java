package net.splitcells.gel.test.functionality;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.rater.RegulatedLength;
import net.splitcells.gel.solution.Solution;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.rater.AllSame.allSame;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.*;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static org.assertj.core.api.Assertions.fail;

public class SchoolSchedulingTest {

    public static final Attribute<Integer> TEACHER = attribute(Integer.class, "teacher");
    public static final Attribute<Integer> SCHOOL_SUBJECT = attribute(Integer.class, "school subject");
    public static final Attribute<Integer> TEACH_SUBJECT_SUITABILITY = attribute(Integer.class, "teach subject suitability");
    public static final Attribute<Integer> COURSE_ID = attribute(Integer.class, "course id");
    public static final Attribute<Integer> VINTAGE = attribute(Integer.class, "vintage");
    public static final Attribute<Integer> REQUIRED_HOURS = attribute(Integer.class, "required hours");
    public static final Attribute<Integer> ALLOCATED_HOURS = attribute(Integer.class, "allocated hours");
    public static final Attribute<Integer> RAIL = attribute(Integer.class, "rail");

    /**
     * TODO
     */
    @Tag(INTEGRATION_TEST)
    @Disabled
    @Test
    public void testSchoolScheduling() {
        /*final var sessionsByTeachers = define_problem()
                .withDemandAttributes(TEACHER, SUBJECT, SUBJECT_LENGTH)
                .withSupplyAttributes(SESSION_LENGTH)
                .withConstraint(null)
                .toProblem()
                .toSolution();
        final var teachersActivityParallelism = define_problem()
                .withDemandAttributes()
                .withSupplyAttributes(PARALLEL_EXECUTION_GROUPS)
                .withConstraint(null)
                .toProblem()
                .toSolution();
        final var studentAssignment = define_problem()
                .withDemandAttributes()
                .withSupplyAttributes(STUDENT, SUBJECT_REQUIRED)
                .withConstraint(null)
                .toProblem()
                .toSolution();
        final var schoolScheduling = define_problem()
                .withDemandAttributes()
                .withSupplyAttributes(WEEKDAY, HOUR, ROOM_NUMBER)
                .withConstraint(null)
                .toProblem()
                .toSolution();*/
        fail("Test not implemented");
    }

    private Problem defineRailsForSchoolScheduling() {
        return defineProblem()
                .withDemandAttributes(COURSE_ID, SCHOOL_SUBJECT, VINTAGE, REQUIRED_HOURS)
                .withSupplyAttributes(ALLOCATED_HOURS, RAIL)
                .withConstraint(r -> {
                    r.forAll(lineValueSelector(line -> line.value(RAIL) == 0))
                            .then(lineValueRater(line -> line.value(ALLOCATED_HOURS) == 0, line -> cost(1)));
                    r.forAll(SCHOOL_SUBJECT)
                            .forAll(lineValueSelector(line -> line.value(RAIL) != 0))
                            .then(allDifferent(RAIL));
                    r.forAll(COURSE_ID).then(RegulatedLength.regulatedLength(REQUIRED_HOURS, ALLOCATED_HOURS));
                    r.forAll(RAIL).then(allSame(ALLOCATED_HOURS));
                    return r;
                }).toProblem();
    }

    private Problem defineTeacherAllocationForCourses(Solution solution) {
        return defineProblem()
                .withDemands(solution)
                .withSupplyAttributes(TEACHER, TEACH_SUBJECT_SUITABILITY)
                .withConstraint(r -> {
                    r.forAll(COURSE_ID).then(allSame(TEACHER));
                    return r;
                })
                .toProblem();
    }

}
