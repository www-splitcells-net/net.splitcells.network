/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.optimization;

import lombok.val;
import net.splitcells.gel.editor.Editor;

public class DefaultEditorOptimization implements EditorOptimization {
    public static DefaultEditorOptimization defaultEditorOptimization() {
        return new DefaultEditorOptimization();
    }

    private DefaultEditorOptimization() {

    }

    @Override public void optimize(Editor editor) {
        val solutionPaths = editor.solutionPaths();
        solutionPaths.requireSizeOf(1, getClass().getName() + " only supports a list of solutions and not a full tree or even graph of interdependent solutions."
                + " In other words, every solution is only allowed to have at most 1 solution as its demand or supply."
                + " Furthermore, the solution's interdependencies are not allowed to form a circle.");
    }
}
