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
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.editor.lang.*;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_EACH_NAME;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.data.view.attribute.AttributeI.*;
import static net.splitcells.gel.editor.Editor.editor;
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
import static net.splitcells.gel.editor.solution.SolutionEditor.solutionEditor;
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
}
