/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.optimization;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.solution.optimization.DefaultOptimization.defaultOptimization;

public class DefaultEditorOptimization implements EditorOptimization {
    public static DefaultEditorOptimization defaultEditorOptimization(Editor argEditor) {
        return new DefaultEditorOptimization(argEditor);
    }

    private final Editor editor;
    private int currentSolutionPath = 0;
    private int currentSolutionIndex;
    private final List<List<Solution>> solutionPaths;

    private DefaultEditorOptimization(Editor argEditor) {
        editor = argEditor;
        solutionPaths = editor.solutionPaths();
        currentSolutionIndex = solutionPaths.get(currentSolutionPath).size();
    }

    @Override public Optional<DefaultEditorOptimization> runNextStep() {
        solutionPaths.requireSizeOf(1, () -> getClass().getName() + " only supports a list of solutions and not a full tree or even graph of interdependent solutions."
                + " In other words, every solution is only allowed to have at most 1 solution as its demand or supply."
                + " Furthermore, the solution's interdependencies are not allowed to form a circle.");
        if (--currentSolutionIndex > -1) {
            val currentSolution = solutionPaths.get(currentSolutionPath).get(currentSolutionIndex);
            currentSolution.history().processWithHistory(() -> currentSolution.optimize(defaultOptimization()));
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override public Tree status() {
        val completed = solutionPaths.size() - 1 <= currentSolutionPath
                && solutionPaths.get(currentSolutionPath).size() - 1 <= currentSolutionIndex;
        if (completed) {
            return tree(editor.getName() + " is optimized by " + getClass().getSimpleName());
        }
        val currentSolutionPathDescription = solutionPaths.get(currentSolutionPath)
                .stream()
                .map(View::name)
                .reduce((a, b) -> a + "/" + b)
                .orElse("");
        return tree(editor.getName() + " is being optimized by " + getClass().getSimpleName())
                .withProperty("Solution paths processed", "" + (currentSolutionPath + 1) + "/" + solutionPaths.size())
                .withProperty("Current Solution path position", currentSolutionIndex + "/" + solutionPaths.get(currentSolutionPath).size())
                .withProperty("Current Solution path", currentSolutionPathDescription)
                ;
    }
}
