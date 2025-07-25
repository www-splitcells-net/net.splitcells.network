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

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.editor.lang.*;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_EACH_NAME;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.data.view.attribute.AttributeI.*;
import static net.splitcells.gel.editor.Editor.editor;
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
import static net.splitcells.gel.editor.SolutionEditor.solutionEditor;
import static net.splitcells.gel.editor.geal.lang.FunctionCallChainDesc.functionCallChainDesc;
import static net.splitcells.gel.editor.geal.lang.IntegerDesc.integerDesc;
import static net.splitcells.gel.editor.geal.lang.NameDesc.nameDesc;
import static net.splitcells.gel.editor.geal.lang.SourceUnit.sourceUnit;
import static net.splitcells.gel.editor.geal.lang.StringDesc.stringDesc;
import static net.splitcells.gel.editor.geal.lang.VariableDefinitionDesc.variableDefinitionDesc;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;

public class EditorTest {
    @UnitTest
    public void testAllParsingBranches() {
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        final var colloquiumDescription = solutionDescription("colloquium-planning"
                , list(attributeDescription("student", STRING, emptySourceCodeQuote())
                        , attributeDescription("examiner", STRING, emptySourceCodeQuote())
                        , attributeDescription("observer", STRING, emptySourceCodeQuote())
                        , attributeDescription("date", INTEGER, emptySourceCodeQuote())
                        , attributeDescription("shift", INTEGER, emptySourceCodeQuote())
                        , attributeDescription("roomNumber", INTEGER, emptySourceCodeQuote()))
                , tableDescription("exams"
                        , list(referenceDescription("student", AttributeDescription.class, emptySourceCodeQuote())
                                , referenceDescription("examiner", AttributeDescription.class, emptySourceCodeQuote())
                                , referenceDescription("observer", AttributeDescription.class, emptySourceCodeQuote()))
                        , emptySourceCodeQuote())
                , tableDescription("exam slot"
                        , list(referenceDescription("date", AttributeDescription.class, emptySourceCodeQuote())
                                , referenceDescription("shift", AttributeDescription.class, emptySourceCodeQuote())
                                , referenceDescription("roomNumber", AttributeDescription.class, emptySourceCodeQuote()))
                        , emptySourceCodeQuote())
                , list(constraintDescription(functionCallDescription(FOR_EACH_NAME
                                , list(referenceDescription("observer", AttributeDescription.class, emptySourceCodeQuote()))
                                , emptySourceCodeQuote())
                        , list(constraintDescription(functionCallDescription(FOR_ALL_COMBINATIONS_OF
                                        , list(referenceDescription("date", AttributeDescription.class, emptySourceCodeQuote())
                                                , referenceDescription("shift", AttributeDescription.class, emptySourceCodeQuote())),
                                        emptySourceCodeQuote())
                                , list(constraintDescription(functionCallDescription(THEN_NAME
                                                , list(functionCallDescription(HAS_SIZE_NAME, emptySourceCodeQuote(), integerDescription(1, emptySourceCodeQuote())))
                                                , emptySourceCodeQuote())
                                        , list()
                                        , emptySourceCodeQuote())), emptySourceCodeQuote())), emptySourceCodeQuote()))
                , emptySourceCodeQuote()
        );
        final var colloquium = solutionEditor(testSubject, colloquiumDescription);
        colloquium.parse(colloquiumDescription).requireWorking();
        colloquium.attributes().requirePresence("student", stringAttribute("student"), CONTENT_COMPARISON)
                .requirePresence("examiner", stringAttribute("examiner"), CONTENT_COMPARISON)
                .requirePresence("observer", stringAttribute("observer"), CONTENT_COMPARISON)
                .requirePresence("date", integerAttribute("date"), CONTENT_COMPARISON)
                .requirePresence("shift", integerAttribute("shift"), CONTENT_COMPARISON)
                .requirePresence("roomNumber", integerAttribute("roomNumber"), CONTENT_COMPARISON);
        colloquium.demands().orElseThrow().headerView2().requireEquality(list(
                        stringAttribute("student")
                        , stringAttribute("examiner")
                        , stringAttribute("observer"))
                , CONTENT_COMPARISON);
        colloquium.supplies().orElseThrow().headerView2().requireEquality(list(
                        integerAttribute("date")
                        , integerAttribute("shift")
                        , integerAttribute("roomNumber"))
                , CONTENT_COMPARISON);
        final var solution = colloquium.solution().orElseThrow();
        solution.constraint().readQuery()
                .forAll(solution.attributeByName("observer"))
                .forAllCombinationsOf(solution.attributeByName("date"), solution.attributeByName("shift"))
                .then(hasSize(1));
    }

    @UnitTest
    public void testGealInterpretation() {
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        final var testData = sourceUnit(list(
                variableDefinitionDesc(nameDesc("student")
                        , functionCallDesc2(nameDesc("attribute")
                                , list(functionCallDesc3(STRING_TYPE), stringDesc("student"))))
                , variableDefinitionDesc(nameDesc("examiner")
                        , functionCallDesc2(nameDesc("attribute")
                                , list(functionCallDesc3(STRING_TYPE), stringDesc("examiner"))))
                , variableDefinitionDesc(nameDesc("observer")
                        , functionCallDesc2(nameDesc("attribute")
                                , list(functionCallDesc3(STRING_TYPE), stringDesc("observer"))))
                , variableDefinitionDesc(nameDesc("date")
                        , functionCallDesc2(nameDesc("attribute")
                                , list(functionCallDesc3(INTEGER_TYPE), stringDesc("date"))))
                , variableDefinitionDesc(nameDesc("shift")
                        , functionCallDesc2(nameDesc("attribute")
                                , list(functionCallDesc3(INTEGER_TYPE), stringDesc("shift"))))
                , variableDefinitionDesc(nameDesc("roomNumber")
                        , functionCallDesc2(nameDesc("attribute")
                                , list(functionCallDesc3(INTEGER_TYPE), stringDesc("roomNumber"))))
                , variableDefinitionDesc(nameDesc("demands")
                        , functionCallDesc2(nameDesc(TABLE_FUNCTION)
                                , list(stringDesc("exams")
                                        , functionCallDesc3("student")
                                        , functionCallDesc3("examiner")
                                        , functionCallDesc3("observer"))))
                , variableDefinitionDesc(nameDesc("supplies")
                        , functionCallDesc2(nameDesc(TABLE_FUNCTION)
                                , list(stringDesc("time slots")
                                        , functionCallDesc3("date")
                                        , functionCallDesc3("shift")
                                        , functionCallDesc3("roomNumber"))))
                , variableDefinitionDesc(nameDesc("solution")
                        , functionCallDesc2(nameDesc(SOLUTION_FUNCTION)
                                , list(stringDesc("Colloquium Plan")
                                        , functionCallDesc3("demands")
                                        , functionCallDesc3("supplies")
                                        , functionCallDesc3("rules"))))
                , functionCallChainDesc(nameDesc("solution")
                        , list(functionCallDesc2(nameDesc(FOR_EACH_NAME), list(functionCallDesc3("examiner")))
                                , functionCallDesc2(nameDesc(FOR_ALL_COMBINATIONS_OF)
                                        , list(functionCallDesc3("date"), functionCallDesc3("shift")))
                                , functionCallDesc2(nameDesc(THEN_NAME)
                                        , list(functionCallDesc2(nameDesc(HAS_SIZE_NAME)
                                                , list(integerDesc(1)))))))
                , functionCallChainDesc(nameDesc("solution")
                        , list(functionCallDesc2(nameDesc(FOR_EACH_NAME), list(functionCallDesc3("student")))
                                , functionCallDesc2(nameDesc(FOR_ALL_COMBINATIONS_OF)
                                        , list(functionCallDesc3("date"), functionCallDesc3("shift")))
                                , functionCallDesc2(nameDesc(THEN_NAME)
                                        , list(functionCallDesc2(nameDesc(HAS_SIZE_NAME)
                                                , list(integerDesc(1)))))))
                , functionCallChainDesc(nameDesc("solution")
                        , list(functionCallDesc2(nameDesc(FOR_EACH_NAME), list(functionCallDesc3("student")))
                                , functionCallDesc2(nameDesc(THEN_NAME)
                                        , list(functionCallDesc2(nameDesc(HAS_SIZE_NAME)
                                                , list(integerDesc(2)))))))
        ));
        testSubject.parse(testData);
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
                
                solution   = solution('Colloquium Plan', demands, supplies, rules);
                solution   .forEach(examiner)
                           .forAllCombinationsOf(date, shift)
                           .then(hasSize(1))
                solution   .forEach(student)
                           .forAllCombinationsOf(date, shift)
                           .then(hasSize(1))
                solution   .forEach(student)
                           .then(hasSize(2))
                """;
        testSubject.parse(parseGealSourceUnit(testData));
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
                demands    . importCsv(data('demands.csv'))
                
                supplies   = table('time slots', date, shift, roomNumber);
                supplies   . importCsv(data('supplies.csv'))
                
                solution   = solution('Colloquium Plan', demands, supplies, rules);
                solution   . forEach(examiner)
                           . forAllCombinationsOf(date, shift)
                           . then(hasSize(1))
                solution   . forEach(student)
                           . forAllCombinationsOf(date, shift)
                           . then(hasSize(1))
                solution   . forEach(student)
                           . then(hasSize(2))
                """;
        final var demandsCsv = """
                student,examiner,observer
                1,2,3
                """;
        final var suppliesCsv = """
                date,shift,room number
                4,5,6
                """;
        testSubject.saveData("demands.csv", toBytes(demandsCsv));
        testSubject.saveData("supplies.csv", toBytes(suppliesCsv));
        testSubject.parse(parseGealSourceUnit(testData));
        final var demands = testSubject.getTables().get("demands").orderedLines();
        demands.requireSizeOf(1);
        demands.get(0).values().requireEquals(list("1", "2", "3"));
        final var supplies = testSubject.getTables().get("supplies").orderedLines();
        supplies.requireSizeOf(1);
        supplies.get(0).values().requireEquals(list(4, 5, 6));
    }
}
