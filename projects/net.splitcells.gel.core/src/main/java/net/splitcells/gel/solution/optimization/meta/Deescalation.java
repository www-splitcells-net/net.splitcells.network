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

import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import java.util.function.Function;

public class Deescalation implements OnlineOptimization {

    public static OnlineOptimization deescalation(Function<Integer, OnlineOptimization> optimizations, int escalationLevel, int minimum_escalation_level, int maximum_escalation_level) {
        return new Deescalation(optimizations, escalationLevel, minimum_escalation_level, maximum_escalation_level);
    }

    private final Function<Integer, OnlineOptimization> optimizations;
    private int escalationLevel;
    private final int minimum_escalation_level;
    private final int maximum_escalation_level;

    private Deescalation(Function<Integer, OnlineOptimization> optimizations, int escalationLevel, int minimum_escalation_level, int maximum_escalation_level) {
        this.optimizations = optimizations;
        this.escalationLevel = escalationLevel;
        this.minimum_escalation_level = minimum_escalation_level;
        this.maximum_escalation_level = maximum_escalation_level;
    }

    @Override
    public void optimize(Solution solution) {
        final var startRating = solution.constraint().rating();
        if (escalationLevel < minimum_escalation_level) {
            return;
        }
        this.optimizations.apply(escalationLevel).optimize(solution);
        final var nextRating = solution.constraint().rating();
        if (nextRating.betterThan(startRating)) {
            if (escalationLevel < maximum_escalation_level) {
                escalationLevel += 1;
            }
        } else {
            escalationLevel -= 1;
        }
    }
}
