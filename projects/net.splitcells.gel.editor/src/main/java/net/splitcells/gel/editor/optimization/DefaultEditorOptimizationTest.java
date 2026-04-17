/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.optimization;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.editor.GelEditorFileSystem;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.editor.geal.parser.SourceUnitParser.parseGealSourceUnit;
import static net.splitcells.gel.editor.optimization.DefaultEditorOptimization.defaultEditorOptimization;

public class DefaultEditorOptimizationTest {
    @UnitTest public void test() {
        val editor = editor("test-subject", EXPLICIT_NO_CONTEXT);
        editor.interpret(parseGealSourceUnit(new GelEditorFileSystem().defaultValue().readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/sports-course-planning.txt")));
        final List<Solution> processedSolutions = list();
        val testSubject = defaultEditorOptimization(editor, s -> new EditorOptimization() {

            @Override public Optional<EditorOptimization> runNextStep() {
                processedSolutions.add(s);
                if (s.isOptimal()) return Optional.empty();
                return Optional.of(this);
            }

            @Override public Tree status() {
                return tree("no-status");
            }
        });
        var currentStep = testSubject.runNextStep();
        while (currentStep.isPresent()) {
            currentStep = currentStep.get().runNextStep();
        }
        processedSolutions.requireEquals(list(editor.getSolutions().get("courseAssignment")
                , editor.getSolutions().get("courseScheduling")));
    }
}