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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.editor.optimization.RepairOptimizationStep.repairOptimizationStep;
import static net.splitcells.gel.solution.optimization.DefaultOptimization.defaultOptimization;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;

public class DefaultEditorOptimization implements EditorOptimization {
    public static EditorOptimization defaultEditorOptimization(Editor argEditor) {
        return new DefaultEditorOptimization(argEditor, cs -> repairOptimizationStep(cs));
    }

    public static EditorOptimization defaultEditorOptimization(Editor argEditor, Function<Solution, EditorOptimization> argSubOptimizerFactory) {
        return new DefaultEditorOptimization(argEditor, argSubOptimizerFactory);
    }

    private final Editor editor;
    private int currentSolutionPath = 0;
    private int currentSolutionIndex;
    private Optional<EditorOptimization> currentOptimizer = Optional.empty();
    private final List<List<Solution>> solutionPaths;
    private final Function<Solution, EditorOptimization> subOptimizerFactory;

    private DefaultEditorOptimization(Editor argEditor, Function<Solution, EditorOptimization> argSubOptimizerFactory) {
        subOptimizerFactory = argSubOptimizerFactory;
        editor = argEditor;
        solutionPaths = editor.solutionPaths();
        currentSolutionIndex = solutionPaths.get(currentSolutionPath).size();
    }

    @Override public Optional<EditorOptimization> runNextStep() {
        solutionPaths.requireSizeOf(1, () -> getClass().getName() + " only supports a list of solutions and not a full tree or even graph of interdependent solutions."
                + " In other words, every solution is only allowed to have at most 1 solution as its demand or supply."
                + " Furthermore, the solution's interdependencies are not allowed to form a circle.");
        if (currentOptimizer.isPresent()) {
            currentOptimizer = currentOptimizer.get().runNextStep();
            return Optional.of(this);
        }
        if (--currentSolutionIndex > -1) {
            val currentSolution = solutionPaths.get(currentSolutionPath).get(currentSolutionIndex);
            if (currentSolutionIndex == solutionPaths.get(currentSolutionPath).size() - 1) {
                currentSolution.history().processWithHistory(() -> onlineLinearInitialization().optimize(currentSolution));
            }
            currentOptimizer = Optional.of(subOptimizerFactory.apply(currentSolution));
            currentOptimizer = currentSolution.history().processWithHistory(cs -> cs.orElseThrow().runNextStep(), currentOptimizer);
            return Optional.of(this);
        } else {
            if (currentSolutionPath > solutionPaths.size() - 2) {
                return Optional.empty();
            }
            currentSolutionIndex = solutionPaths.get(++currentSolutionPath).size();
            return runNextStep();
        }
    }

    @Override public Tree status() {
        final Optional<Solution> currentSolution;
        if (currentSolutionIndex > -1) {
            currentSolution = Optional.of(solutionPaths.get(currentSolutionPath).get(currentSolutionIndex));
        } else {
            currentSolution = Optional.empty();
        }
        val currentSolutionPathDescription = solutionPaths.get(currentSolutionPath)
                .stream()
                .map(View::name)
                .reduce((a, b) -> a + "/" + b)
                .orElse("");
        return tree(editor.getName() + " is being optimized by " + getClass().getSimpleName())
                .withProperty("Solution paths processed", (currentSolutionPath + 1) + "/" + solutionPaths.size())
                .withProperty("Current solution path position", currentSolutionIndex + "/" + solutionPaths.get(currentSolutionPath).size())
                .withProperty("Current solution path", currentSolutionPathDescription)
                .withProperty("Current sub optimizer", currentOptimizer
                        .map(EditorOptimization::status)
                        .orElseGet(() -> tree("No sub optimizer present")))
                .withProperty("Current solution rating", currentSolution.map(cs -> cs.name()
                        + ": "
                        + cs.constraint().rating().descriptionForUser()
                ).orElse("No rating is present."))
                .withChild(tree("Overhaul solutions rating")
                        .withChildren(editor.getSolutions().values().stream()
                                .map(s -> tree(s.name() + ": " + s.constraint().rating().descriptionForUser()))
                                .collect(toList())))
                ;
    }
}
