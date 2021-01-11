package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.type.ForAlls.forAllWithValue;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.Then.tad;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.define_problem;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.primitive.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintGroupBasedRepairTest {

    @Test
    public void testReallocation() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var invalidValueA = 1;
        final var invalidValueB = 3;
        final var validValue = 5;
        final var defyingGroupA = tad(cost(1));
        final var defyingGroupB = tad(cost(1));
        @SuppressWarnings("unchecked") final var solution
                = define_problem()
                .withDemandAttributes(a, b)
                .withDemands
                        (list(invalidValueA, 1)
                                , list(invalidValueA, 1)
                                , list(invalidValueA, 2)
                                , list(invalidValueA, 2)
                                , list(2, invalidValueB)
                                , list(2, invalidValueB)
                                , list(validValue, validValue))
                .withSupplyAttributes()
                .withSupplies
                        (list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                        )
                .withConstraint
                /**
                 * Needless constraints are added, in order to check, if the correct {@link Constraint} is select.
                 */
                        (forAll().withChildren
                                (forAllWithValue(a, validValue).withChildren(tad(noCost()))
                                        , forAllWithValue(b, validValue).withChildren(tad(noCost()))
                                        , forAllWithValue(a, invalidValueA).withChildren(defyingGroupA)
                                        , forAllWithValue(b, invalidValueB).withChildren(defyingGroupB)
                                        , forAllWithValue(a, validValue).withChildren(tad(noCost()))
                                        , forAllWithValue(b, validValue).withChildren(tad(noCost()))))
                .toProblem()
                .asSolution();
        solution.optimize(linearInitialization());
        final var testSubject = constraintGroupBasedRepair(
                constraintGroup -> Optional.of(constraintGroup.get(6)) // Select the first defying group.
                , freeDemandGroups -> currentSolution -> {
                    final List<OptimizationEvent> repairs = list();
                    final int i[] = {0};
                    freeDemandGroups.entrySet().forEach(freeGroup -> {
                        freeGroup.getValue().forEach(freeDemand -> {
                            repairs.add(
                                    optimizationEvent(
                                            ADDITION
                                            , freeDemand.toLinePointer()
                                            , currentSolution.supplies_free().getLines().get(i[0]++).toLinePointer()
                                    ));
                        });
                    });
                    return repairs;
                }
        );
        final var groupOfConstraintGroup = testSubject.groupOfConstraintGroup(solution);
        final var demandClassification = groupOfConstraintGroup
                .map(e -> e
                        .lastValue()
                        .map(f -> testSubject.demandGrouping(f, solution))
                        .orElseGet(() -> map()))
                .orElseGet(() -> map());
        final var testProduct = testSubject.repair(solution, demandClassification);
        assertThat(testProduct).hasSize(4);
        final var freeSupplyIndexes = testProduct.stream()
                .map(optimizationEvent -> optimizationEvent.supply().index())
                .collect(toList());
        assertThat(freeSupplyIndexes).contains(7, 8, 9, 10);
        final var demandIndexes = testProduct.stream()
                .map(optimizationEvent -> optimizationEvent.demand().index())
                .collect(toList());
        assertThat(demandIndexes).contains(0, 1, 2, 3);
    }

    @Test
    public void testIzbrīvoNeievērotajuGrupuNoIerobežojumuGrupu() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var nēderigaAVertība = 1;
        final var nēderigaBVertība = 3;
        final var derigaVertība = 5;
        final var neievērotajuGrupaA = tad(cost(1));
        final var neievērotajuGrupaB = tad(cost(1));
        @SuppressWarnings("unchecked") final var atrisinājums
                = define_problem()
                .withDemandAttributes(a, b)
                .withDemands
                        (list(nēderigaAVertība, 1)
                                , list(nēderigaAVertība, 1)
                                , list(nēderigaAVertība, 2)
                                , list(nēderigaAVertība, 2)
                                , list(2, nēderigaBVertība)
                                , list(2, nēderigaBVertība)
                                , list(derigaVertība, derigaVertība))
                .withSupplyAttributes()
                .withSupplies
                        (list(), list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list())
                .withConstraint
                        (forAll().withChildren
                                (forAllWithValue(a, Integer.valueOf(derigaVertība)).withChildren(tad(noCost()))
                                        , forAllWithValue(b, Integer.valueOf(derigaVertība)).withChildren(tad(noCost()))
                                        , forAllWithValue(a, Integer.valueOf(nēderigaAVertība)).withChildren(neievērotajuGrupaA)
                                        , forAllWithValue(b, Integer.valueOf(nēderigaBVertība)).withChildren(neievērotajuGrupaB)
                                        , forAllWithValue(a, Integer.valueOf(derigaVertība)).withChildren(tad(noCost()))
                                        , forAllWithValue(b, Integer.valueOf(derigaVertība)).withChildren(tad(noCost()))))
                .toProblem()
                .asSolution();
        atrisinājums.optimize(linearInitialization());
        assertThat(atrisinājums.getLines()).hasSize(7);

        final var pārbaudesPriekšmets = constraintGroupBasedRepair();
        atrisinājums.optimize(pārbaudesPriekšmets.freeDefyingGroupOfConstraintGroup(atrisinājums, neievērotajuGrupaA));
        assertThat(atrisinājums.getLines()).hasSize(3);
        atrisinājums.optimize(pārbaudesPriekšmets.freeDefyingGroupOfConstraintGroup(atrisinājums, neievērotajuGrupaB));
        assertThat(atrisinājums.getLines()).hasSize(1);
        assertThat(atrisinājums.getLines()).hasSize(1);
    }

    @Test
    public void testPrāsībasGrupēšana() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var nēderigaAVertība = 1;
        final var nēderigaBVertība = 3;
        final var derigaVertība = 5;
        final var neievērotajuGrupaA = tad(cost(1));
        final var neievērotajuGrupaB = tad(cost(1));
        @SuppressWarnings("unchecked") final var atrisinājums
                = define_problem()
                .withDemandAttributes(a, b)
                .withDemands
                        (list(nēderigaAVertība, derigaVertība)
                                , list(nēderigaAVertība, derigaVertība)
                                , list(nēderigaAVertība, derigaVertība)
                                , list(nēderigaAVertība, derigaVertība)
                                , list(derigaVertība, nēderigaBVertība)
                                , list(derigaVertība, nēderigaBVertība)
                                , list(derigaVertība, derigaVertība))
                .withSupplyAttributes()
                .withSupplies
                        (list(), list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list())
                .withConstraint
                        (forAll().withChildren
                                (forAllWithValue(a, Integer.valueOf(derigaVertība)).withChildren(tad(noCost()))
                                        , forAllWithValue(b, Integer.valueOf(derigaVertība)).withChildren(tad(noCost()))
                                        , forAllWithValue(a, Integer.valueOf(nēderigaAVertība)).withChildren(neievērotajuGrupaA)
                                        , forAllWithValue(b, Integer.valueOf(nēderigaBVertība)).withChildren(neievērotajuGrupaB)
                                        , forAllWithValue(a, Integer.valueOf(derigaVertība)).withChildren(tad(noCost()))
                                        , forAllWithValue(b, Integer.valueOf(derigaVertība)).withChildren(tad(noCost()))))
                .toProblem()
                .asSolution();
        atrisinājums.optimize(linearInitialization());
        assertThat(atrisinājums.getLines()).hasSize(7);

        final var pārbaudesPriekšmets = constraintGroupBasedRepair();
        final var pārbaudesRažojums = pārbaudesPriekšmets.demandGrouping
                (atrisinājums.constraint().childrenView().get(3).childrenView().get(0)
                        , atrisinājums);
        assertThat(pārbaudesRažojums).hasSize(1);
        assertThat(pārbaudesRažojums.values().iterator().next()).hasSize(2);
        pārbaudesRažojums.values().iterator().next()
                .forEach(rinda -> assertThat(rinda.value(b)).isEqualTo(nēderigaBVertība));
    }
}
