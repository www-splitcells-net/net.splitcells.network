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
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.gel.common.Language.OPTIMIZATION;

public class Escalator implements Optimization {

    public static Escalator escalator(Function<Integer, Optimization> optimizations) {
        return new Escalator(optimizations, 0, 0, Integer.MAX_VALUE);
    }

    public static Escalator escalator(Function<Integer, Optimization> optimizations, int escalationLevel, int minimum_escalation_level, int maximum_escalation_level) {
        return new Escalator(optimizations, escalationLevel, minimum_escalation_level, maximum_escalation_level);
    }

    private final Function<Integer, Optimization> optimizations;
    private int escalationLevel;
    private final int minimum_escalation_level;
    private final int maximum_escalation_level;

    private Escalator(Function<Integer, Optimization> optimizations, int escalationLevel, int minimum_escalation_level, int maximum_escalation_level) {
        this.optimizations = optimizations;
        this.escalationLevel = escalationLevel;
        this.minimum_escalation_level = minimum_escalation_level;
        this.maximum_escalation_level = maximum_escalation_level;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var rootRating = solution.constraint().rating();
        if (TRACING) {
            domsole().append(perspective("escalation-step")
                            .withProperty("escalation-level", "" + escalationLevel)
                            .withProperty("root-cost", "" + rootRating.getContentValue(Cost.class).value())
                    , () -> solution.path().withAppended(OPTIMIZATION.value(), getClass().getSimpleName())
                    , LogLevel.TRACE);
        }
        final var rootHistoryIndex = solution.history().currentIndex();
        if (escalationLevel < minimum_escalation_level) {
            return list();
        }
        final var optimizations = this.optimizations.apply(escalationLevel).optimize(solution);
        // TODO PERFORMANCE This line of code can duplicate the runtime of the complete code.
        final var currentRating = solution.rating(optimizations);
        if (currentRating.betterThan(rootRating)) {
            if (escalationLevel < maximum_escalation_level) {
                escalationLevel += 1;
            }
        } else {
            escalationLevel -= 1;
        }
        return optimizations;
    }
}