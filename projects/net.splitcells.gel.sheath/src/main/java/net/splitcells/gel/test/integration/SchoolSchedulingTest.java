package net.splitcells.gel.test.integration;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.HasSize.has_size;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static org.assertj.core.api.Assertions.fail;

public class SchoolSchedulingTest {

    public static final Attribute<Integer> TEACHER = attribute(Integer.class, "teacher");
    public static final Attribute<Integer> SCHOOL_SUBJECT = attribute(Integer.class, "school subject");
    public static final Attribute<Integer> VINTAGE = attribute(Integer.class, "vintage");
    public static final Attribute<Integer> HOURS = attribute(Integer.class, "hours");
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
                .withDemandAttributes(TEACHER, SCHOOL_SUBJECT, VINTAGE)
                .withSupplyAttributes(HOURS, RAIL)
                .withConstraint(r -> {
                    r.then(has_size(0));
                    return r;
                }).toProblem();
    }

}
