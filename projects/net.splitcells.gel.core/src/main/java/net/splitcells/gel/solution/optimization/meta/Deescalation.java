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

    public static Deescalation deescalation(Function<Integer, OnlineOptimization> optimizations, int escalationLevel, int minimumEscalationLevel, int maximumEscalationLevel) {
        return new Deescalation(optimizations, escalationLevel, minimumEscalationLevel, maximumEscalationLevel);
    }

    private final Function<Integer, OnlineOptimization> optimizations;
    private int escalationLevel;
    private final int minimumEscalationLevel;
    private final int maximumEscalationLevel;

    private Deescalation(Function<Integer, OnlineOptimization> optimizations, int escalationLevel, int minimumEscalationLevel, int maximumEscalationLevel) {
        this.optimizations = optimizations;
        this.escalationLevel = escalationLevel;
        this.minimumEscalationLevel = minimumEscalationLevel;
        this.maximumEscalationLevel = maximumEscalationLevel;
    }

    @Override
    public void optimize(Solution solution) {
        final var startRating = solution.constraint().rating();
        if (escalationLevel < minimumEscalationLevel) {
            return;
        }
        this.optimizations.apply(escalationLevel).optimize(solution);
        final var nextRating = solution.constraint().rating();
        if (nextRating.betterThan(startRating)) {
            if (escalationLevel < maximumEscalationLevel) {
                escalationLevel += 1;
            }
        } else {
            if (escalationLevel > minimumEscalationLevel) {
                escalationLevel -= 1;
            }
        }
    }
}
