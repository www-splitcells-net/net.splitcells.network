/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

public class RandomInitialization implements OfflineOptimization {
    public static RandomInitialization randomInitialization() {
        return new RandomInitialization(randomness());
    }

    private final Randomness randomness;

    private RandomInitialization(Randomness randomness) {
        this.randomness = randomness;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (solution.demandsFree().hasContent() && solution.suppliesFree().hasContent()) {
            return list(optimizationEvent(ADDITION
                                    , randomness.chooseOneOf(solution.demandsFree().unorderedLines()).toLinePointer()
                                    , randomness.chooseOneOf(solution.suppliesFree().unorderedLines()).toLinePointer()));

        }
        return list();
    }
}
