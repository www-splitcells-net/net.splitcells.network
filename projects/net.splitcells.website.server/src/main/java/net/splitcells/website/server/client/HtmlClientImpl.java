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
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.config.InternalPublicPort;
import net.splitcells.website.server.config.PublicDomain;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * TODO Test Firefox and Chromium automatically in tests using a {@link HtmlClient}.
 */
@JavaLegacyArtifact
public class HtmlClientImpl implements HtmlClient {
    /**
     * @return Provides an HTTP based HTML client for {@link net.splitcells.website.server.ServerService}.
     */
    public static HtmlClient htmlClientImpl() {
        if (configValue(PublicDomain.class).isPresent()) {
            return publicHtmlClient();
        }
        return new HtmlClientImpl("http://localhost:" + configValue(ServerConfig.class).openPort());
    }

    /**
     * TODO Determine protocol via configuration.
     *
     * @return Provides an HTTP based HTMLs client,
     * that connects to the public facing HTTPs interface of {@link net.splitcells.website.server.ServerService}.
     * HTTPS is assumed, because it is expected that public facing servers encrypt their communication.
     */
    public static HtmlClient publicHtmlClient() {
        return new HtmlClientImpl("https://"
                + configValue(PublicDomain.class).orElseThrow()
                + configValue(InternalPublicPort.class).map(port -> ":" + port).orElse(""));
    }

    private final Playwright playwright = Playwright.create();
    private final Browser browser = playwright.firefox().launch();
    /**
     * The URL's schema and authority of the webserver.
     * For example, `http://localhost:8443` would be such a URL.
     */
    private final String address;
    private final List<Page> openTabs = list();

    private HtmlClientImpl(String addressArg) {
        address = addressArg;
    }

    @Override
    public Tab openTab(String path) {
        final var page = browser.newPage();
        openTabs.add(page);
        page.navigate(address + path);
        return new Tab() {

            @Override
            public Element elementByClass(String cssClass) {
                return new Element() {

                    final Locator locator = page.locator("." + cssClass);

                    @Override
                    public void click() {
                        locator.click();
                    }

                    @Override
                    public String textContent() {
                        return locator.textContent();
                    }

                    @Override
                    public String value() {
                        return locator.inputValue();
                    }
                };
            }

            @Override
            public Element elementById(String id) {
                return new Element() {

                    final Locator locator = page.locator("#" + id);

                    @Override
                    public void click() {
                        locator.click();
                    }

                    @Override
                    public String textContent() {
                        return locator.textContent();
                    }

                    @Override
                    public String value() {
                        return locator.inputValue();
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
        openTabs.forEach(Page::close);
        playwright.close();
    }

    @Override
    public void flush() {
        // Nothing needs to be done.
    }
}
