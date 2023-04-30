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
package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.function.BiPredicate;

public class OptimizationConfig {
    public static OptimizationConfig optimizationConfig() {
        return new OptimizationConfig();
    }

    private boolean recordHistory = true;
    private boolean optimizeOnce = false;

    private BiPredicate<Solution, Integer> continuationCondition = (s, i) -> i == 0;

    private OptimizationConfig() {

    }

    public OptimizationConfig withRecordHistory(boolean recordHistory) {
        this.recordHistory = recordHistory;
        return this;
    }

    public boolean recordHistory() {
        return recordHistory;
    }

    public OptimizationConfig withOptimizeOnce(boolean optimizeOnce) {
        this.optimizeOnce = optimizeOnce;
        return this;
    }

    public boolean optimizeOnce() {
        return optimizeOnce;
    }

    public OptimizationConfig withContinuationCondition(BiPredicate<Solution, Integer> continuationCondition) {
        this.continuationCondition = continuationCondition;
        return this;
    }

    public BiPredicate<Solution, Integer> continuationCondition() {
        return continuationCondition;
    }
}
