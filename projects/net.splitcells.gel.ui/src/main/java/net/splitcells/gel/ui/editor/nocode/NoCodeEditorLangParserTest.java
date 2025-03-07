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
import net.splitcells.dem.testing.annotations.CapabilityTest;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.ui.GelUiFileSystem;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.has_minimal_distance_of;
import static net.splitcells.gel.ui.editor.nocode.NoCodeEditorLangParser.parseNoCodeSolutionDescription;
import static net.splitcells.gel.ui.no.code.editor.NoCodeProblemParser.parseNoCodeProblem;

public class NoCodeEditorLangParserTest {
    @CapabilityTest
    public void testAttributeParsing() {
        final var testResult = parseNoCodeSolutionDescription(Dem.configValue(GelUiFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/ui/no/code/editor/examples/school-course-scheduling-problem.xml"));
        testResult.requireWorking();
    }

}
