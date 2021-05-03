package net.splitcells.gel.test.functionality;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.AllDifferent.allDifferent;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.*;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static org.assertj.core.api.Assertions.fail;

public class SchoolSchedulingTest {

    public static final Attribute<Integer> TEACHER = attribute(Integer.class, "teacher");
    public static final Attribute<Integer> SCHOOL_SUBJECT = attribute(Integer.class, "school subject");
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
                .withDemandAttributes(TEACHER, SCHOOL_SUBJECT, VINTAGE, REQUIRED_HOURS)
                .withSupplyAttributes(ALLOCATED_HOURS, RAIL)
                .withConstraint(r -> {
                    r.forAll(lineValueSelector(line -> line.value(RAIL) == 0))
                            .then(lineValueRater(line -> line.value(ALLOCATED_HOURS) == 0, line -> cost(1)));
                    r.forAll(SCHOOL_SUBJECT)
                            .forAll(lineValueSelector(line -> line.value(RAIL) != 0))
                            .then(allDifferent(RAIL));
                    r.forAll(COURSE_ID)
                            // TODO Implement this via derived values. This concept is not implemented yet.
                            .then(new Rater() {

                                @Override
                                public RatingEvent ratingAfterAddition(Table lines, Line addition
                                        , List<Constraint> children, Table ratingsBeforeAddition) {
                                    final var additionRating = ratingEvent();
                                    final int requiredHours = addition.value(REQUIRED_HOURS);
                                    final var allocatedHours = lines.getLines()
                                            .stream()
                                            .map(line -> line.value(ALLOCATED_HOURS))
                                            .reduce(Integer::sum)
                                            .orElse(0);
                                    final var totalCost = distance(requiredHours, allocatedHours);
                                    final var lineCost = cost(totalCost / lines.getLines().size());
                                    lines.getLines().stream()
                                            .filter(e -> e.index() != addition.index())
                                            .forEach(e -> additionRating.updateRating_withReplacement(e
                                                    , localRating()
                                                            .withPropagationTo(children)
                                                            .withRating(lineCost)
                                                            .withResultingGroupId
                                                                    (e.value(Constraint.INCOMING_CONSTRAINT_GROUP))));
                                    additionRating.additions().put(addition
                                            , localRating()
                                                    .withPropagationTo(children)
                                                    .withRating(lineCost)
                                                    .withResultingGroupId
                                                            (addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
                                    return additionRating;
                                }

                                @Override
                                public RatingEvent rating_before_removal(Table lines, Line removal
                                        , List<Constraint> children, Table ratingsBeforeRemoval) {
                                    return ratingEvent();
                                }

                                @Override
                                public List<Domable> arguments() {
                                    throw not_implemented_yet();
                                }

                                @Override
                                public void addContext(Discoverable context) {
                                    throw not_implemented_yet();
                                }

                                @Override
                                public Collection<List<String>> paths() {
                                    throw not_implemented_yet();
                                }
                            });
                    return r;
                }).toProblem();
    }

}
