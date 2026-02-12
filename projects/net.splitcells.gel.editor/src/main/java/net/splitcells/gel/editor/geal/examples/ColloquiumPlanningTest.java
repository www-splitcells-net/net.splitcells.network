/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.examples;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.editor.GelEditorFileSystem;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.gel.editor.Editor.editor;

public class ColloquiumPlanningTest {
    @UnitTest public void testInterpretation() {
        final var problemDefinition = configValue(GelEditorFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/colloquium-planning.txt");
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        testSubject.interpret(problemDefinition);
    }
}
