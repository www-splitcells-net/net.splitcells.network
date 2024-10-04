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
package net.splitcells.system;

import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.WebsiteServerCell;
import net.splitcells.website.server.test.HtmlLiveTest;

import static net.splitcells.dem.Dem.serve;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.waitUntilRequirementIsTrue;
import static net.splitcells.dem.utils.StringUtils.requireNonEmptyString;
import static net.splitcells.website.server.client.HtmlClientImpl.publicHtmlClient;

public class SystemCell implements Cell {

    public static void main(String... args) {
        serve(SystemCell.class);
    }

    @Override
    public String groupId() {
        return "net.splitcells";
    }

    @Override
    public String artifactId() {
        return "system";
    }

    @Override
    public void accept(Environment env) {
        env.config()
                .withConfigValue(ServerConfig.class, WebsiteViaJar.config(env.config().configValue(ServerConfig.class)))
                .withConfigValue(HtmlLiveTest.class, () -> {
                    try (final var browser = publicHtmlClient()) {
                        final var tab = browser.openTab("/net/splitcells/gel/ui/no/code/editor/index.html");
                        requireEquals("", tab.elementById("net-splitcells-gel-ui-no-code-editor-form-errors").textContent());
                        requireEquals("", tab.elementById("net-splitcells-gel-ui-no-code-editor-form-solution").textContent());
                        requireEquals("", tab.elementById("net-splitcells-gel-ui-no-code-editor-form-solution-rating").textContent());
                        tab.elementByClass("net-splitcells-website-pop-up-confirmation-button").click();
                        tab.elementById("net-splitcells-gel-ui-no-code-editor-calculate-solution-form-submit-1").click();
                        waitUntilRequirementIsTrue(1000L * 60, () -> !tab.elementById("net-splitcells-gel-ui-no-code-editor-form-solution").value().isEmpty());
                        requireEquals("", tab.elementById("net-splitcells-gel-ui-no-code-editor-form-errors").textContent());
                        requireNonEmptyString(tab.elementById("net-splitcells-gel-ui-no-code-editor-form-solution-rating").textContent());
                    }
                })
        ;
        env.withCell(WebsiteServerCell.class);
    }
}
