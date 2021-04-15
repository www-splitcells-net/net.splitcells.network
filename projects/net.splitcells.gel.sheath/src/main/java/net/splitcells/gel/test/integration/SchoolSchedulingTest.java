package net.splitcells.gel.test.integration;

import net.splitcells.gel.data.table.attribute.Attribute;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.fail;

public class SchoolSchedulingTest {

    public static final Attribute<String> TEACHER = attribute(String.class, "teacher");
    // RENAME
    public static final Attribute<String> SUBJECT = attribute(String.class, "can teach");
    public static final Attribute<Integer> SUBJECT_LENGTH = attribute(Integer.class, "subject length");
    public static final Attribute<Integer> SESSION_LENGTH = attribute(Integer.class, "session length");

    // RENAME
    public static final Attribute<Integer> PARALLEL_EXECUTION_GROUPS = attribute(Integer.class,
            "parallel execution groups");

    public static final Attribute<String> STUDENT = attribute(String.class, "student");
    public static final Attribute<String> SUBJECT_REQUIRED = attribute(String.class, "subject required");

    public static final Attribute<String> WEEKDAY = attribute(String.class, "week day");
    // RENAME
    public static final Attribute<String> HOUR = attribute(String.class, "hour");
    public static final Attribute<Integer> ROOM_NUMBER = attribute(Integer.class, "room number");

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

}
