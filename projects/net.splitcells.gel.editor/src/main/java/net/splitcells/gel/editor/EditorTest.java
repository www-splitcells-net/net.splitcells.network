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
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.editor.lang.AttributeDescription;
import net.splitcells.gel.editor.lang.ReferenceDescription;
import net.splitcells.gel.editor.lang.SolutionDescription;
import net.splitcells.gel.editor.lang.TableDescription;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.gel.data.view.attribute.AttributeI.*;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.editor.lang.AttributeDescription.attributeDescription;
import static net.splitcells.gel.editor.lang.PrimitiveType.INTEGER;
import static net.splitcells.gel.editor.lang.PrimitiveType.STRING;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.editor.lang.SolutionDescription.solutionDescription;
import static net.splitcells.gel.editor.lang.TableDescription.tableDescription;
import static net.splitcells.gel.editor.solution.SolutionEditor.solutionEditor;

public class EditorTest {
    @UnitTest
    public void testParsingColloquium() {
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        final var colloquiumDescription = solutionDescription("colloquium-planning");
        colloquiumDescription.attributes().with("student", attributeDescription("student", STRING))
                .with("examiner", attributeDescription("examiner", STRING))
                .with("observer", attributeDescription("observer", STRING))
                .with("date", attributeDescription("date", INTEGER))
                .with("shift", attributeDescription("shift", INTEGER))
                .with("roomNumber", attributeDescription("roomNumber", INTEGER));
        colloquiumDescription.withDemands(tableDescription("exams")
                .withAttribute(referenceDescription("student", AttributeDescription.class))
                .withAttribute(referenceDescription("examiner", AttributeDescription.class))
                .withAttribute(referenceDescription("observer", AttributeDescription.class)));
        colloquiumDescription.withSupplies(tableDescription("exam slot")
                .withAttribute(referenceDescription("date", AttributeDescription.class))
                .withAttribute(referenceDescription("shift", AttributeDescription.class))
                .withAttribute(referenceDescription("roomNumber", AttributeDescription.class)));
        final var colloquium = solutionEditor(testSubject, colloquiumDescription);
        colloquium.attributes().requirePresence("student", stringAttribute("student"), CONTENT_COMPARISON)
                .requirePresence("examiner", stringAttribute("examiner"), CONTENT_COMPARISON)
                .requirePresence("observer", stringAttribute("observer"), CONTENT_COMPARISON)
                .requirePresence("date", integerAttribute("date"), CONTENT_COMPARISON)
                .requirePresence("shift", integerAttribute("shift"), CONTENT_COMPARISON)
                .requirePresence("roomNumber", integerAttribute("roomNumber"), CONTENT_COMPARISON);
        colloquium.demands().headerView2().requireEquality(list(
                        stringAttribute("student")
                        , stringAttribute("examiner")
                        , stringAttribute("observer"))
                , CONTENT_COMPARISON);
        colloquium.supplies().headerView2().requireEquality(list(
                        integerAttribute("date")
                        , integerAttribute("shift")
                        , integerAttribute("roomNumber"))
                , CONTENT_COMPARISON);
    }
}
