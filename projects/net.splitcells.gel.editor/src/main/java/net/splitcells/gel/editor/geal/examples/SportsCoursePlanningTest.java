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
package net.splitcells.gel.editor.geal.examples;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.editor.GelEditorFileSystem;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.gel.editor.Editor.editor;

public class SportsCoursePlanningTest {
    @UnitTest public void testInterpretation() {
        final var problemDefinition = configValue(GelEditorFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/sports-course-planning.txt");
        final var testSubject = editor("test-subject", EXPLICIT_NO_CONTEXT);
        testSubject.interpret(problemDefinition);
    }
}
