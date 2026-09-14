/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.optimization;

import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

/**
 * Defines an optimization
 */
public interface OptimizationStep {
    Optional<OptimizationStep> runNextStep();

    Tree status();
}
