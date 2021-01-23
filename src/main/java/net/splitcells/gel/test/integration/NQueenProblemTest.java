package net.splitcells.gel.test.integration;

import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.problem.derived.SimplifiedAnnealingProblem;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RaterBasedOnLineValue;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.SolutionBuilder;
import net.splitcells.gel.solution.optimization.primitive.UsedSupplySwitcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.lang.Math.*;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.resource.host.Files.createDirectory;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.testing.TestTypes.CAPABILITY_TEST;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.HasSize.has_size;
import static net.splitcells.gel.rating.rater.classification.GroupMultiplier.groupMultiplier;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.optimization.meta.LinearIterator.linearIterator;
import static net.splitcells.gel.solution.optimization.meta.hill.climber.FunctionalHillClimber.functionalHillClimber;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

public class NQueenProblemTest extends TestSuiteI {
    public static final Attribute<Integer> COLUMN = attribute(Integer.class, "column");
    public static final Attribute<Integer> ROW = attribute(Integer.class, "row");

    @Disabled
    @Tag(CAPABILITY_TEST)
    @Test
    public void test_8_queen_problem_with_rolling_the_dice() {
        // TODO Setting the randomness seed.
        final Solution testSubject = nQueenProblem(8, 8).asSolution();
        testSubject.optimize(linearInitialization());
        testSubject.optimize(functionalHillClimber(UsedSupplySwitcher.usedSupplySwitcher(6), 50));
        testSubject.optimize(functionalHillClimber(UsedSupplySwitcher.usedSupplySwitcher(8), 100));
        createDirectory(environment().config().configValue(ProcessPath.class));
        writeToFile(environment().config().configValue(ProcessPath.class).resolve("history.fods"), testSubject.history().toFods());
        writeToFile(environment().config().configValue(ProcessPath.class).resolve("analysis.fods"), testSubject.toFodsTableAnalysis());
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(0));
    }

    /**
     * TODO
     */
    @Disabled
    @Tag(CAPABILITY_TEST)
    @Test
    public void test_8_queen_problem_with_annealing_hill_climber() {
        final var testSubject = nQueenProblem(8, 8).asSolution();
        // The temperature functions was determined by trial and error with universal allocation program's temperature functions.
        testSubject.optimize(linearInitialization());
        SimplifiedAnnealingProblem.simplifiedAnnealingProblem(testSubject,
                i -> max((float) (log(4.0) / pow(log(i + 3), 15))
                        , 0))
                .optimizeOnce(functionalHillClimber(
                        linearIterator(
                                list(
                                        UsedSupplySwitcher.usedSupplySwitcher(2))),
                        100));
        // NOTE usedSupplySwitcher(2) finds many non improving steps.
        createDirectory(environment().config().configValue(ProcessPath.class));
        writeToFile(environment().config().configValue(ProcessPath.class).resolve("history.fods"), testSubject.history().toFods());
        writeToFile(environment().config().configValue(ProcessPath.class).resolve("analysis.fods"), testSubject.toFodsTableAnalysis());
        domsole().append(testSubject.constraint().rating(), empty(), LogLevel.UNKNOWN_ERROR);
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(0));
    }

    private Problem nQueenProblem(int rows, int columns) {
        final var demands = listWithValuesOf(
                rangeClosed(1, columns).
                        mapToObj(i -> list((Object) i)).
                        collect(toList()));
        final var supplies = listWithValuesOf(
                rangeClosed(1, rows).
                        mapToObj(i -> list((Object) i)).
                        collect(toList()));
        return SolutionBuilder.define_problem()
                .withDemandAttributes(COLUMN)
                .withDemands(demands)
                .withSupplyAttributes(ROW)
                .withSupplies(supplies)
                .withConstraint(ForAlls.forAll()
                        .withChildren(r -> r.for_each(ROW).for_each(COLUMN).then(has_size(1)))
                        .withChildren(r -> r.for_each(ROW).then(has_size(1)))
                        .withChildren(r -> r.for_each(COLUMN).then(has_size(1)))
                        .withChildren(r -> r.for_all(ascDiagonals(rows, columns)).then(has_size(1)))
                        .withChildren(r -> r.for_all(descDiagonals(rows, columns)).then(has_size(1))))
                .toProblem();
    }

    /**
     * TODO Use this.
     */
    private static Rater diagonals(int rows, int columns) {
        return groupMultiplier(
                ascDiagonals(rows, columns),
                descDiagonals(rows, columns)
        );
    }

    /**
     * The ascending diagonal with the number 0 represents the diagonal in the middle.
     */
    private static Rater ascDiagonals(int rows, int columns) {
        return RaterBasedOnLineValue.raterBasedOnLineValue("ascDiagonals", line -> line.value(ROW) - line.value(COLUMN));
    }

    /**
     * The descending diagonal with the number 0 represents the diagonal in the middle.
     */
    private static Rater descDiagonals(int rows, int columns) {
        return RaterBasedOnLineValue.raterBasedOnLineValue("descDiagonals", line -> line.value(ROW) - Math.abs(line.value(COLUMN) - columns - 1));
    }
}
