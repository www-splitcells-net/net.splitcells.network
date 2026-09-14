/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.optimization;

import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

/**
 * This API defines a step in an optimization chain and points to the next step after this one is done.
 * This is primarily used, in order to pause and generate status info about the progress. 
 */
public interface OptimizationStep {
    Optional<OptimizationStep> runAndProvideNextStep();

    Tree status();
}
