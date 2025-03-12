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

import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.gel.ui.GelUiCell;

import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.waitUntilRequirementIsTrue;
import static net.splitcells.dem.utils.StringUtils.requireNonEmptyString;
import static net.splitcells.website.server.client.HtmlClients.htmlClient;

public class NoCodeSolutionCalculatorTest {

    public static Runnable TEST_OPTIMIZATION_GUI = () -> {
        try (final var browser = htmlClient()) {
            try (final var tab = browser.openTab("/net/splitcells/gel/ui/no/code/editor/index.html")) {
                requireEquals("", tab.elementById("net-splitcells-gel-editor-form-errors").textContent());
                requireEquals("", tab.elementById("net-splitcells-gel-editor-form-solution").textContent());
                requireEquals("", tab.elementById("net-splitcells-gel-editor-form-solution-rating").textContent());
                tab.elementByClass("net-splitcells-website-pop-up-confirmation-button").click();
                tab.elementById("net-splitcells-gel-ui-editor-nocode-calculate-solution-form-submit-1").click();
                waitUntilRequirementIsTrue(1000L * 60, () -> !tab.elementById("net-splitcells-gel-editor-form-solution").value().isEmpty());
                requireEquals("", tab.elementById("net-splitcells-gel-ui-editor-nocode-form-errors").textContent());
                requireNonEmptyString(tab.elementById("net-splitcells-gel-editor-form-solution-rating").textContent());
            }
        }
    };

    /**
     * <p>TODO This test does not work on Codeberg.</p>
     * <p>TODO This test should be made deterministic.</p>
     * <p>TODO Additionally a random tests with probabilistic successes could be supported as well.
     * It should be stored in the network log, how often the test failed or succeeded yet.
     * Another job should check the ratio between failed tests and succeeded ones.</p>
     */
    @IntegrationTest
    @DisabledTest
    public void testOptimization() {
        process(TEST_OPTIMIZATION_GUI, GelUiCell.class).requireErrorFree();
    }
}
