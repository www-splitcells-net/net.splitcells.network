/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.nocode;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.CapabilityTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.lang.AttributeDescription;
import net.splitcells.gel.editor.lang.PrimitiveType;
import net.splitcells.gel.editor.lang.ReferenceDescription;
import net.splitcells.gel.ui.GelUiFileSystem;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_EACH_NAME;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.lang.AttributeDescription.attributeDescription;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.has_minimal_distance_of;
import static net.splitcells.gel.ui.editor.nocode.NoCodeEditorLangParser.parseNoCodeSolutionDescription;
import static net.splitcells.gel.ui.editor.nocode.NoCodeEditorLangParser.parseNoCodeSolutionEditor;

public class NoCodeEditorLangParserTest {
    @UnitTest
    public void testParsing() {
        final var problem = parseNoCodeSolutionEditor(Dem.configValue(GelUiFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/ui/editor/nocode/examples/school-course-scheduling-problem.xml"))
                .requiredValue()
                .solution()
                .orElseThrow();
        final var observer = problem.allocations().attributeByName("observer");
        final var examiner = problem.allocations().attributeByName("examiner");
        final var student = problem.allocations().attributeByName("student");
        final var date = problem.allocations().attributeByName("date");
        final var shift = problem.allocations().attributeByName("shift");
        query(problem.constraint(), false)
                .forAll(observer)
                .forAllCombinationsOf(list(date, shift))
                .then(hasSize(1));
        query(problem.constraint(), false)
                .forAll(examiner)
                .forAllCombinationsOf(list(date, shift))
                .then(hasSize(1));
        query(problem.constraint(), false)
                .forAll(student)
                .forAllCombinationsOf(list(date, shift))
                .then(hasSize(1));
        query(problem.constraint(), false)
                .forAll(student)
                .then(has_minimal_distance_of((Attribute<Integer>) date, 3));
        query(problem.constraint(), false)
                .forAll(student)
                .then(has_minimal_distance_of((Attribute<Integer>) date, 5));
    }

    @UnitTest
    public void testDatabaseParsing() {
        final var parsedDatabases = parseNoCodeSolutionEditor(Dem.configValue(GelUiFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/ui/editor/nocode/examples/school-course-scheduling-problem.xml"))
                .requiredValue()
                .solution()
                .orElseThrow();
        parsedDatabases.demands().requireEqualFormat(
                table("demands"
                        , stringAttribute("student")
                        , stringAttribute("examiner")
                        , stringAttribute("observer")));
        parsedDatabases.supplies().requireEqualFormat(
                table("supplies"
                        , integerAttribute("date")
                        , integerAttribute("shift")
                        , integerAttribute("roomNumber")));
    }

    @UnitTest
    public void testProblemParsing() {
        final var testResult = parseNoCodeSolutionEditor(Dem.configValue(GelUiFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/ui/editor/nocode/examples/school-course-scheduling-problem.xml"))
                .requiredValue()
                .solution()
                .orElseThrow();
        setOfUniques(testResult.headerView2()).requireContentsOf((a, b) -> a.equalContentTo(b)
                , stringAttribute("student")
                , stringAttribute("examiner")
                , stringAttribute("observer")
                , integerAttribute("date")
                , integerAttribute("shift")
                , integerAttribute("roomNumber"));
    }

    @UnitTest
    public void testProblemParsing2() {
        final var testResult = parseNoCodeSolutionEditor(Dem.configValue(GelUiFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/ui/editor/nocode/examples/school-course-scheduling-problem.xml"))
                .requiredValue();
        final var solution = testResult.solution().orElseThrow();
        testResult.columnAttributesForOutputFormat().requireEqualityTo(list(solution.attributeByName("roomNumber")));
        testResult.rowAttributesForOutputFormat()
                .requireEqualityTo(list(solution.attributeByName("date"), solution.attributeByName("shift")));
    }
}
