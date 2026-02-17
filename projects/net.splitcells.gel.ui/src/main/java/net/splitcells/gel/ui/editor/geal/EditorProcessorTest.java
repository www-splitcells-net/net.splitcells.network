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
package net.splitcells.gel.ui.editor.geal;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.ui.GelUiCell;
import net.splitcells.website.server.messages.FormUpdate;
import net.splitcells.website.server.processor.Request;

import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.testing.Assertions.waitUntilRequirementIsTrue;
import static net.splitcells.dem.utils.StringUtils.requireNonEmptyString;
import static net.splitcells.dem.utils.StringUtils.requirePrefixAbsence;
import static net.splitcells.gel.ui.editor.geal.EditorProcessor.*;
import static net.splitcells.website.server.client.HtmlClients.htmlClient;

public class EditorProcessorTest {
    /**
     * TODO Look up, if there is an error message.
     */
    public static final Runnable TEST_OPTIMIZATION_GUI = () -> {
        try (final var browser = htmlClient()) {
            try (final var tab = browser.openTab("/net/splitcells/gel/ui/editor/geal/index.html")) {
                tab.elementByClass("net-splitcells-website-pop-up-confirmation-button").click();
                tab.elementById("net-splitcells-gel-ui-editor-geal-form-submit").click();
                waitUntilRequirementIsTrue(1000L * 60, () -> tab
                        .elementById("net-splitcells-gel-ui-editor-geal-form-solution")
                        .evalIfExists(e -> !e.value().trim().isEmpty())
                        .orElse(false));
                requireNonEmptyString(tab
                        .elementById("net-splitcells-gel-ui-editor-geal-form-solution.formatted")
                        .textContent());
            }
        }
    };

    @IntegrationTest
    @DisabledTest
    public void testOptimization() {
        process(EditorProcessorTest.TEST_OPTIMIZATION_GUI, GelUiCell.class).requireErrorFree();
    }

    @UnitTest
    public void testErrorStartWithoutNewLines() {
        final var testSubject = editorProcessor();
        final var requestTree = tree("").withProperty(PROBLEM_DEFINITION, "unknownFunction();");
        final var request = Request.<Tree>request(trail(), requestTree);
        requirePrefixAbsence(testSubject
                        .process(request)
                        .data()
                        .namedChild(FormUpdate.DATA_VALUES)
                        .namedChild(FormUpdate.ERRORS)
                        .valueName()
                , "\n");
    }

    @UnitTest
    public void testMissingProblemDefinition() {
        final var testSubject = editorProcessor();
        final var requestTree = tree("");
        testSubject.process(Request.<Tree>request(trail(), requestTree))
                .data()
                .namedChild(FormUpdate.DATA_VALUES)
                .namedChild(FormUpdate.ERRORS)
                .children()
                .requireSizeOf(1);
    }
}
