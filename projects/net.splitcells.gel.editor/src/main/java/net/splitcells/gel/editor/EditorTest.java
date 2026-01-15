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
package net.splitcells.gel.editor;

import lombok.val;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.editor.geal.lang.*;
import net.splitcells.gel.editor.lang.*;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_EACH_NAME;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.data.view.attribute.AttributeI.*;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.editor.EditorData.editorData;
import static net.splitcells.gel.editor.EditorParser.*;
import static net.splitcells.gel.editor.geal.lang.FunctionCallDesc.*;
import static net.splitcells.gel.editor.geal.parser.SourceUnitParser.parseGealSourceUnit;
import static net.splitcells.gel.editor.lang.AttributeDescription.attributeDescription;
import static net.splitcells.gel.editor.lang.ConstraintDescription.constraintDescription;
import static net.splitcells.gel.editor.lang.FunctionCallDescription.functionCallDescription;
import static net.splitcells.gel.editor.lang.IntegerDescription.integerDescription;
import static net.splitcells.gel.editor.lang.PrimitiveType.INTEGER;
import static net.splitcells.gel.editor.lang.PrimitiveType.STRING;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.editor.lang.SolutionDescription.solutionDescription;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;
import static net.splitcells.gel.editor.lang.TableDescription.tableDescription;
import static net.splitcells.gel.editor.geal.lang.VariableDefinitionDesc.variableDefinitionDesc;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.website.Format.CSV;

public class EditorTest {

    @UnitTest
    public void testGealInterpretation() {
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        final var testData = SourceUnit.sourceUnitForTest(list(
                variableDefinitionDesc(NameDesc.nameDescForTest("student")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest("attribute")
                                , list(functionCallDescForTest(STRING_TYPE), StringDesc.stringDescForTest("student"))))
                , variableDefinitionDesc(NameDesc.nameDescForTest("examiner")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest("attribute")
                                , list(functionCallDescForTest(STRING_TYPE), StringDesc.stringDescForTest("examiner"))))
                , variableDefinitionDesc(NameDesc.nameDescForTest("observer")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest("attribute")
                                , list(functionCallDescForTest(STRING_TYPE), StringDesc.stringDescForTest("observer"))))
                , variableDefinitionDesc(NameDesc.nameDescForTest("date")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest("attribute")
                                , list(functionCallDescForTest(INTEGER_TYPE), StringDesc.stringDescForTest("date"))))
                , variableDefinitionDesc(NameDesc.nameDescForTest("shift")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest("attribute")
                                , list(functionCallDescForTest(INTEGER_TYPE), StringDesc.stringDescForTest("shift"))))
                , variableDefinitionDesc(NameDesc.nameDescForTest("roomNumber")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest("attribute")
                                , list(functionCallDescForTest(INTEGER_TYPE), StringDesc.stringDescForTest(
                                        "roomNumber"))))
                , variableDefinitionDesc(NameDesc.nameDescForTest("demands")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(TABLE_FUNCTION)
                                , list(StringDesc.stringDescForTest("exams")
                                        , functionCallDescForTest("student")
                                        , functionCallDescForTest("examiner")
                                        , functionCallDescForTest("observer"))))
                , variableDefinitionDesc(NameDesc.nameDescForTest("supplies")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(TABLE_FUNCTION)
                                , list(StringDesc.stringDescForTest("time slots")
                                        , functionCallDescForTest("date")
                                        , functionCallDescForTest("shift")
                                        , functionCallDescForTest("roomNumber"))))
                , variableDefinitionDesc(NameDesc.nameDescForTest("solution")
                        , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(SOLUTION_FUNCTION)
                                , list(StringDesc.stringDescForTest("Colloquium Plan")
                                        , functionCallDescForTest("demands")
                                        , functionCallDescForTest("supplies"))))
                , FunctionCallChainDesc.functionCallChainDescForTest(NameDesc.nameDescForTest("solution")
                        , list(FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(FOR_EACH_NAME),
                                        list(functionCallDescForTest("examiner")))
                                ,
                                FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(FOR_EACH_NAME)
                                        , list(functionCallDescForTest("date"), functionCallDescForTest("shift")))
                                , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(THEN_NAME)
                                        ,
                                        list(FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(HAS_SIZE_NAME)
                                                , list(IntegerDesc.integerDescForTest(1)))))))
                , FunctionCallChainDesc.functionCallChainDescForTest(NameDesc.nameDescForTest("solution")
                        , list(FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(FOR_EACH_NAME),
                                        list(functionCallDescForTest("student")))
                                ,
                                FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(FOR_EACH_NAME)
                                        , list(functionCallDescForTest("date"), functionCallDescForTest("shift")))
                                , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(THEN_NAME)
                                        ,
                                        list(FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(HAS_SIZE_NAME)
                                                , list(IntegerDesc.integerDescForTest(1)))))))
                , FunctionCallChainDesc.functionCallChainDescForTest(NameDesc.nameDescForTest("solution")
                        , list(FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(FOR_EACH_NAME),
                                        list(functionCallDescForTest("student")))
                                , FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(THEN_NAME)
                                        ,
                                        list(FunctionCallDesc.functionCallDescForTest(NameDesc.nameDescForTest(HAS_SIZE_NAME)
                                                , list(IntegerDesc.integerDescForTest(2)))))))
        ));
        testSubject.interpret(testData);
        testSubject.getAttributes().requirePresence("student", stringAttribute("student"), CONTENT_COMPARISON)
                .requirePresence("examiner", stringAttribute("examiner"), CONTENT_COMPARISON)
                .requirePresence("observer", stringAttribute("observer"), CONTENT_COMPARISON)
                .requirePresence("date", integerAttribute("date"), CONTENT_COMPARISON)
                .requirePresence("shift", integerAttribute("shift"), CONTENT_COMPARISON)
                .requirePresence("roomNumber", integerAttribute("roomNumber"), CONTENT_COMPARISON);
        testSubject.getTables().get("demands").headerView2().requireEquality(list(
                        stringAttribute("student")
                        , stringAttribute("examiner")
                        , stringAttribute("observer"))
                , CONTENT_COMPARISON);
        testSubject.getTables().get("supplies").headerView2().requireEquality(list(
                        integerAttribute("date")
                        , integerAttribute("shift")
                        , integerAttribute("roomNumber"))
                , CONTENT_COMPARISON);
        testSubject.getSolutions().get("solution").headerView2().requireEquality(list(
                        stringAttribute("student")
                        , stringAttribute("examiner")
                        , stringAttribute("observer")
                        , integerAttribute("date")
                        , integerAttribute("shift")
                        , integerAttribute("roomNumber"))
                , CONTENT_COMPARISON);
        testSubject.getSolutions().get("solution")
                .constraint()
                .readQuery()
                .forAll(testSubject.getAttributes().get("examiner"))
                .forAllCombinationsOf(testSubject.getAttributes().get("date"), testSubject.getAttributes().get("shift"))
                .then(hasSize(1));
        // The following tests, whether proper constraint trees are supported.
        testSubject.getSolutions().get("solution")
                .constraint()
                .readQuery()
                .forAll(testSubject.getAttributes().get("student"))
                .forAllCombinationsOf(testSubject.getAttributes().get("date"), testSubject.getAttributes().get("shift"))
                .then(hasSize(1));
        // The following tests, whether constraint sharing is working.
        testSubject.getSolutions().get("solution")
                .constraint()
                .readQuery()
                .forAll(testSubject.getAttributes().get("student"))
                .then(hasSize(2));
    }

    @UnitTest
    public void testGealParsing() {
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        final var testData = """
                student    = attribute(String,  'student');
                examiner   = attribute(String,  'examiner');
                observer   = attribute(String,  'observer');
                date       = attribute(Integer, 'date');
                shift      = attribute(Integer, 'shift');
                roomNumber = attribute(Integer, 'room number');
                
                demands    = table('exams', student, examiner, observer);
                supplies   = table('time slots', date, shift, roomNumber);
                
                solution   = solution('Colloquium Plan', demands, supplies);
                solution   .forEach(examiner)
                           .forEach(date, shift)
                           .then(hasSize(1));
                solution   .forEach(student)
                           .forEach(date, shift)
                           .then(hasSize(1));
                solution   .forEach(student)
                           .then hasSize(2);
                """;
        testSubject.interpret(parseGealSourceUnit(testData));
        testSubject.getAttributes().requirePresence("student", stringAttribute("student"), CONTENT_COMPARISON)
                .requirePresence("examiner", stringAttribute("examiner"), CONTENT_COMPARISON)
                .requirePresence("observer", stringAttribute("observer"), CONTENT_COMPARISON)
                .requirePresence("date", integerAttribute("date"), CONTENT_COMPARISON)
                .requirePresence("shift", integerAttribute("shift"), CONTENT_COMPARISON)
                .requirePresence("roomNumber", integerAttribute("room number"), CONTENT_COMPARISON);
        testSubject.getTables().get("demands").headerView2().requireEquality(list(
                        stringAttribute("student")
                        , stringAttribute("examiner")
                        , stringAttribute("observer"))
                , CONTENT_COMPARISON);
        testSubject.getTables().get("supplies").headerView2().requireEquality(list(
                        integerAttribute("date")
                        , integerAttribute("shift")
                        , integerAttribute("room number"))
                , CONTENT_COMPARISON);
        testSubject.getSolutions().get("solution").headerView2().requireEquality(list(
                        stringAttribute("student")
                        , stringAttribute("examiner")
                        , stringAttribute("observer")
                        , integerAttribute("date")
                        , integerAttribute("shift")
                        , integerAttribute("room number"))
                , CONTENT_COMPARISON);
        testSubject.getSolutions().get("solution")
                .constraint()
                .readQuery()
                .forAll(testSubject.getAttributes().get("examiner"))
                .forAllCombinationsOf(testSubject.getAttributes().get("date"), testSubject.getAttributes().get("shift"))
                .then(hasSize(1));
        // The following tests, whether proper constraint trees are supported.
        testSubject.getSolutions().get("solution")
                .constraint()
                .readQuery()
                .forAll(testSubject.getAttributes().get("student"))
                .forAllCombinationsOf(testSubject.getAttributes().get("date"), testSubject.getAttributes().get("shift"))
                .then(hasSize(1));
        // The following tests, whether constraint sharing is working.
        testSubject.getSolutions().get("solution")
                .constraint()
                .readQuery()
                .forAll(testSubject.getAttributes().get("student"))
                .then(hasSize(2));
    }

    @UnitTest
    public void testInput() {
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        final var testData = """
                student    = attribute(String,  'student');
                examiner   = attribute(String,  'examiner');
                observer   = attribute(String,  'observer');
                date       = attribute(Integer, 'date');
                shift      = attribute(Integer, 'shift');
                roomNumber = attribute(Integer, 'room number');
                
                demands    = table('exams', student, examiner, observer);
                demands    . importCsvData('demands.csv');
                
                supplies   = table('time slots', date, shift, roomNumber);
                supplies   . importCsvData('supplies.csv');
                
                solution   = solution('Colloquium Plan', demands, supplies);
                solution   . forEach(examiner)
                           . forEach(date, shift)
                           . then(hasSize(1));
                solution   . forEach(student)
                           . forEach(date, shift)
                           . then(hasSize(1));
                solution   . forEach(student)
                           . then(hasSize(2));
                """;
        final var demandsCsv = """
                student,examiner,observer
                1,2,3
                """;
        final var suppliesCsv = """
                date,shift,room number
                4,5,6
                """;
        testSubject.saveData("demands.csv", editorData(CSV, toBytes(demandsCsv)));
        testSubject.saveData("supplies.csv", editorData(CSV, toBytes(suppliesCsv)));
        testSubject.interpret(parseGealSourceUnit(testData));
        final var demands = testSubject.getTables().get("demands").orderedLines();
        demands.requireSizeOf(1);
        demands.get(0).values().requireEquals(list("1", "2", "3"));
        final var supplies = testSubject.getTables().get("supplies").orderedLines();
        supplies.requireSizeOf(1);
        supplies.get(0).values().requireEquals(list(4, 5, 6));
    }

    @UnitTest
    public void testRatingReport() {
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        final var testData = """
                student    = attribute(String,  'student');
                date       = attribute(Integer, 'date');
                shift      = attribute(Integer, 'shift');
                
                demands    = table('exams', student);
                demands    . importCsvData('demands.csv');
                
                supplies   = table('time slots', date, shift);
                supplies   . importCsvData('supplies.csv');
                
                solution   = solution('Colloquium Plan', demands, supplies);
                solution   . forEach(student)
                           . forEach(date)
                           . forEach(shift)
                           . then(hasSize(1));
                """;
        final var demandsCsv = """
                student
                1
                1
                1
                1
                """;
        final var suppliesCsv = """
                date,shift
                1,1
                1,1
                1,2
                1,2
                """;
        testSubject.saveData("demands.csv", editorData(CSV, toBytes(demandsCsv)));
        testSubject.saveData("supplies.csv", editorData(CSV, toBytes(suppliesCsv)));
        testSubject.interpret(parseGealSourceUnit(testData));
        final var demands = testSubject.getTables().get("demands");
        final var supplies = testSubject.getTables().get("supplies");
        final var solution = testSubject.getSolutions().get("solution");
        demands.orderedLines().requireSizeOf(4);
        supplies.orderedLines().requireSizeOf(4);
        solution.assign(demands.rawLine(0), supplies.rawLine(0));
        // TODO A dedicated variable is used, as the Java11Parser has a bug regarding multi line strings as arguments.
        final var firstReport = """
                # Constraint Rating Report
                
                ## Description
                
                Cost of 0.0
                
                ## Argumentation
                
                There is no argumentation, as there are no errors in the solution, that need reasoning.""";
        requireEquals(solution.constraint().commonMarkRatingReport(), firstReport);
        solution.assign(demands.rawLine(1), supplies.rawLine(1));
        // TODO A dedicated variable is used, as the Java11Parser has a bug regarding multi line strings as arguments.
        final var secondReport = """
                # Constraint Rating Report
                
                ## Description
                
                Cost of 1.0
                
                ## Argumentation
                
                * For all allocations:
                    * For all groups of student attribute values: where value is `1`:
                        * For all groups of date attribute values: where value is `1`:
                            * For all groups of shift attribute values: where value is `1`: Then size should be 1, but is 2
                """;
        requireEquals(solution.constraint().commonMarkRatingReport(), secondReport);
        solution.assign(demands.rawLine(2), supplies.rawLine(2));
        requireEquals(solution.constraint().commonMarkRatingReport(), secondReport);
        solution.assign(demands.rawLine(3), supplies.rawLine(3));
        final var thirdReport = """
                # Constraint Rating Report
                
                ## Description
                
                Cost of 2.0
                
                ## Argumentation
                
                * For all allocations:
                    * For all groups of student attribute values: where value is `1`:
                        * For all groups of date attribute values: where value is `1`:
                            * For all groups of shift attribute values: where value is `1`: Then size should be 1, but is 2
                            * For all groups of shift attribute values: where value is `2`: Then size should be 1, but is 2
                """;
        requireEquals(solution.constraint().commonMarkRatingReport(), thirdReport);
    }

    @UnitTest public void testMinimalParsing() {
        val testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        val testData = new GelEditorFileSystem().defaultValue().readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/geal-minimal-example.txt");
        testSubject.interpret(parseGealSourceUnit(testData));
    }

    @UnitTest public void testGealGrammarByExamplePrasing() {
        val testData = new GelEditorFileSystem().defaultValue().readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/geal-grammar-by-example.txt");
        parseGealSourceUnit(testData);
    }
}
