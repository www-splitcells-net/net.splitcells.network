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
package net.splitcells.website.server.client;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class HtmlClientImpl implements HtmlClientApi {
    /**
     * @param address The URL's schema and authority of the webserver.
     *                For example, `http://localhost:8443` would be such a URL.
     * @return
     */
    public static HtmlClientApi htmlClientImpl(String address) {
        return new HtmlClientImpl(address);
    }

    private final Playwright playwright = Playwright.create();
    private final Browser browser = playwright.firefox().launch();
    private final String address;

    private HtmlClientImpl(String addressArg) {
        address = addressArg;
    }

    @Override
    public TabApi openTab(String path) {
        final var page = browser.newPage();
        page.navigate(address + path);
        return new TabApi() {

            @Override
            public ElementApi elementByClass(String cssClass) {
                return new ElementApi() {

                    @Override
                    public void click() {
                        page.click("." + cssClass);
                    }
                };
            }

            @Override
            public ElementApi elementById(String id) {
                return new ElementApi() {

                    @Override
                    public void click() {
                        page.click("#" + id);
                    }
                };
            }

            @Override
            public void close() {
                page.close();
            }
        };
    }

    @Override
    public void close() {
        playwright.close();
    }

    @Override
    public void flush() {
    }
}
