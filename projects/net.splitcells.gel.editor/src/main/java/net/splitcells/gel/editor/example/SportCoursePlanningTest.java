/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.example;

import lombok.val;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.editor.GelEditorFileSystem;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.gel.editor.Editor.editor;

public class SportCoursePlanningTest {
    @UnitTest
    public void test() {
        val editor = editor(getClass().getName(), EXPLICIT_NO_CONTEXT);
        editor.interpret(configValue(GelEditorFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/sports-course-planning.txt"));
        editor.importSolutionCsvData("courseAssignment", """
                Student,Chosen Sport,Chosen Sport Type,Is Secondary Choice,Assigned Course Number,Assigned Sport,Assigned Sport Type
                Ottfried,Badminton,Individual sport,0,1,Team sport,basketball
                """);
        editor.getSolutions().get("courseAssignment").constraint().rating();
    }
}
