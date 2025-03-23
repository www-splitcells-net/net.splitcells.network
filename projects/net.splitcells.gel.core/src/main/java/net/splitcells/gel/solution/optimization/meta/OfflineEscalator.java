/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.OfflineOptimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.gel.common.Language.OPTIMIZATION;

public class OfflineEscalator implements OfflineOptimization {

    public static OfflineEscalator escalator(Function<Integer, OfflineOptimization> optimizations) {
        return new OfflineEscalator(optimizations, 0, 0, Integer.MAX_VALUE);
    }

    public static OfflineEscalator escalator(Function<Integer, OfflineOptimization> optimizations, int escalationLevel, int minimum_escalation_level, int maximum_escalation_level) {
        return new OfflineEscalator(optimizations, escalationLevel, minimum_escalation_level, maximum_escalation_level);
    }

    private final Function<Integer, OfflineOptimization> optimizations;
    private int escalationLevel;
    private final int minimum_escalation_level;
    private final int maximum_escalation_level;

    private OfflineEscalator(Function<Integer, OfflineOptimization> optimizations, int escalationLevel, int minimum_escalation_level, int maximum_escalation_level) {
        this.optimizations = optimizations;
        this.escalationLevel = escalationLevel;
        this.minimum_escalation_level = minimum_escalation_level;
        this.maximum_escalation_level = maximum_escalation_level;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        final var rootRating = solution.constraint().rating();
        if (TRACING) {
            logs().append(tree("escalation-step")
                            .withProperty("escalation-level", "" + escalationLevel)
                            .withProperty("root-cost", "" + rootRating.asMetaRating().getContentValue(Cost.class).value())
                    , () -> solution.path().withAppended(OPTIMIZATION.value(), getClass().getSimpleName())
                    , LogLevel.TRACE);
        }
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