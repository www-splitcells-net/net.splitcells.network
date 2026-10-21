/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.geal.example;

import net.splitcells.dem.testing.annotations.CapabilityTest;
import net.splitcells.gel.editor.GelEditorFileSystem;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.object.Discoverable.ANONYMOUS_PATH;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.editor.EditorData.editorData;
import static net.splitcells.gel.editor.geal.parser.SourceUnitParser.parseGealSourceUnit;
import static net.splitcells.website.Format.CSV;

public class ColloquiumPlanningTest {
    
    @CapabilityTest public void testCompleteness() {
        final var editor = editor("test-subject", ANONYMOUS_PATH);
        editor.saveData("demands.csv", editorData(CSV, toBytes(ColloquiumExample.demandsCsv())));
        editor.saveData("supplies.csv", editorData(CSV, toBytes(ColloquiumExample.suppliesCsv())));
        editor.interpret(parseGealSourceUnit(configValue(GelEditorFileSystem.class).readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/colloquium-planning.txt")));
        editor.optimize();
        require(editor.getSolutions().values().iterator().next().isComplete());
    }
}
