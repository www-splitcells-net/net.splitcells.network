/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive.repair;

import lombok.val;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.framework.Variable;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.data.assignment.AssignmentEventType;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.rating.framework.Rating;

import java.util.function.Function;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.environment.config.framework.Variable.variable;
import static net.splitcells.dem.environment.config.framework.Variable.variable;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.data.assignment.AssignmentEvent.assignmentEvent;
import static net.splitcells.gel.data.assignment.AssignmentEventType.ADDITION;
import static net.splitcells.gel.data.table.TableEvent.tableEvent;
import static net.splitcells.gel.data.table.history.TableEventType.REMOVAL;

public class SupplySelectors {
    private SupplySelectors() {
        throw constructorIllegal();
    }

    public static SupplySelector indexBasedRepairer(Function<Integer, Integer> indexSelector) {
        return freeDemandGroups -> solution -> {
            final var demandsUsed = Sets.<Line>setOfUniques();
            freeDemandGroups.entrySet().forEach(group -> {
                group.getValue().forEach(demand -> {
                    if (demandsUsed.contains(demand)) {
                        return;
                    }
                    final var freeSupplies = solution.suppliesFree().unorderedLines();
                    if (freeSupplies.hasElements()) {
                        final var supplySelection = freeSupplies.get(indexSelector.apply(freeSupplies.size() - 1));
                        demandsUsed.add(demand);
                        solution.assign(demand, supplySelection);
                    }
                });
            });
        };
    }

    public static SupplySelector hillClimber() {
        return hillClimber(Integer.MAX_VALUE);
    }

    public static SupplySelector hillClimber(int tries) {
        return hillClimber(tries, randomness());
    }

    /**
     * @param tries
     * @return Checks the rating of {@code tries} many random allocations and returns the one with the best {@link Rating}.
     */
    public static SupplySelector hillClimber(int tries, Randomness rnd) {
        return freeDemandGroups -> solution -> {
            /* Using the ordered lines makes the indexes more meaningful,
             * especially when random generators with specific meanings are used.
             */
            val freeSupplies = solution.suppliesFree().orderedLines();
            for (val demandGroup : freeDemandGroups.values()) {
                for (val freeDemand : demandGroup) {
                    if (freeSupplies.isEmpty()) {
                        return;
                    }
                    if (solution.demandsFree().isRawLine(freeDemand.index())) {
                        val bestSupply = Variable.<Line>variable();
                        val bestRating = variable(solution.constraint().rating());
                        solution.history().processWithoutHistory(() -> {
                            range(0, tries).filter(i -> i < freeSupplies.size()).forEach(i -> {
                                if (freeSupplies.isEmpty()) {
                                    return;
                                }
                                // Using a free supply multiple times creates an error and therefore it is avoided.
                                val nextSupply = freeSupplies.switchVal(rnd.integer(0, freeSupplies.size() - 1), null);
                                if (nextSupply == null) {
                                    return;
                                }
                                val assignment = assignmentEvent(freeDemand, nextSupply, ADDITION).setReason(tree("Checking rating for potential assignment."));
                                solution.process(assignment);
                                val nextRating = solution.constraint().rating();
                                solution.process(tableEvent(assignment.getAssignment().orElseThrow(), REMOVAL).setReason(tree("Removing allocation after rating check.")));
                                if (bestSupply.isNull() || nextRating.betterThan(bestRating.val())) {
                                    bestSupply.withValue(nextSupply);
                                    bestRating.withValue(nextRating);
                                }
                            });
                        });
                        if (bestSupply.hasValue()) {
                            solution.assign(freeDemand, bestSupply.val());
                        }
                    }
                }
            }
        };
    }

    public static SupplySelector supplySelector() {
        final var randomness = randomness();
        return indexBasedRepairer(i -> randomness.integer(0, i));
    }
}
