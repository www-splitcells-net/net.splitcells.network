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
package net.splitcells.gel.editor.optimization;

import lombok.val;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.DefaultOptimizationStaging;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.solution.optimization.meta.Deescalation.deescalation;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.RepairConfig.repairConfig;

public class RepairOptimizationStep implements EditorOptimization {
    public static RepairOptimizationStep repairOptimizationStep(Solution argSolution) {
        return new RepairOptimizationStep(argSolution);
    }

    private final int maxStep = 100;
    private int currentStep = -1;
    private final Solution solution;
    private final OnlineOptimization deescalation;

    private RepairOptimizationStep(Solution argSolution) {
        solution = argSolution;
        val maxDepth = solution.constraint().longestConstraintPathLength();
        deescalation = deescalation(currentDepth -> s -> {
                    final int execCount = currentDepth + 1;
                    for (int j = 0; j < execCount; ++j) {
                        constraintGroupBasedRepair(repairConfig().withMinimumConstraintGroupPath(currentDepth))
                                .optimize(s);
                    }
                }
                , maxDepth, 0, maxDepth);
    }

    @Override public Optional<EditorOptimization> runNextStep() {
        if (++currentStep == 0) {
            // Ensures, that at the end of the optimization all values are assigned.
            onlineLinearInitialization().optimize(solution);
        }
        if (currentStep <= maxStep) {
            deescalation.optimize(solution);
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override public Tree status() {
        return tree("Constraint Group Based Repair")
                .withProperty("current step", "" + currentStep)
                .withProperty("max step", "" + maxStep);
    }
}
