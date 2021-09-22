/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

public class RandomInitialization implements Optimization {
    public static RandomInitialization randomInitialization() {
        return new RandomInitialization(randomness());
    }

    private final Randomness randomness;

    private RandomInitialization(Randomness nejaušiba) {
        this.randomness = nejaušiba;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (solution.demandsFree().hasContent() && solution.suppliesFree().hasContent()) {
            return list(
                    optimizationEvent
                            (ADDITION
                                    , randomness.chooseOneOf(solution.demandsFree().getLines()).toLinePointer()
                                    , randomness.chooseOneOf(solution.suppliesFree().getLines()).toLinePointer()));

        }
        return list();
    }
}
