/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.optimization;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import java.util.Optional;

/**
 * <p>This API defines a step in an optimization chain and points to the next step after this one is done.
 * This is primarily used, in order to pause and generate status info about the progress.</p>
 * <p>Do not see this as a pure optimization API, but more as an execution control and report.
 * It is preferred to implement this as a wrapper of {@link OnlineOptimization},
 * in order to avoid duplicate optimization implementations.</p>
 * <p>TODO Extend this interface to persist paused optimization,
 * This also can be used to distribute optimization tasks to nodes via a command and state repo.</p>
 */
public interface OptimizationStep {
    Optional<OptimizationStep> runAndProvideNextStep();

    Tree status();
}
