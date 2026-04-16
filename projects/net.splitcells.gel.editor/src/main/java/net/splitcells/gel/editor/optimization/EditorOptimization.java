/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.optimization;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.editor.Editor;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Defines an optimizer, that tackles all {@link Editor#getSolutions()}.
 */
public interface EditorOptimization {
    Optional<EditorOptimization> runNextStep();

    Tree status();
}
