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
package net.splitcells.gel.editor.solution;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.editor.lang.PrimitiveType;
import net.splitcells.gel.editor.lang.SolutionDescription;

import static net.splitcells.gel.data.view.attribute.AttributeI.*;
import static net.splitcells.gel.editor.lang.AttributeDescription.attributeDescription;
import static net.splitcells.gel.editor.lang.PrimitiveType.INTEGER;
import static net.splitcells.gel.editor.lang.PrimitiveType.STRING;
import static net.splitcells.gel.editor.solution.SolutionEditor.solutionEditor;

public class SolutionEditorTest {
    @UnitTest
    public void testParsingColloquium() {
        final var testData = SolutionDescription.solutionDescription("test-subject");
        testData.attributeDescriptions().put("student", attributeDescription("student", STRING));
        testData.attributeDescriptions().put("examiner", attributeDescription("examiner", STRING));
        testData.attributeDescriptions().put("observer", attributeDescription("observer", STRING));
        testData.attributeDescriptions().put("date", attributeDescription("date", INTEGER));
        testData.attributeDescriptions().put("shift", attributeDescription("shift", INTEGER));
        testData.attributeDescriptions().put("roomNumber", attributeDescription("roomNumber", INTEGER));
        final var testSubject = solutionEditor(testData);
        testSubject.attributes().requirePresence("student", stringAttribute("student"), CONTENT_COMPARISON);
        testSubject.attributes().requirePresence("examiner", stringAttribute("examiner"), CONTENT_COMPARISON);
        testSubject.attributes().requirePresence("observer", stringAttribute("observer"), CONTENT_COMPARISON);
        testSubject.attributes().requirePresence("date", integerAttribute("date"), CONTENT_COMPARISON);
        testSubject.attributes().requirePresence("shift", integerAttribute("shift"), CONTENT_COMPARISON);
        testSubject.attributes().requirePresence("roomNumber", integerAttribute("roomNumber"), CONTENT_COMPARISON);
    }
}
