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

import com.microsoft.playwright.*;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.config.InternalPublicPort;
import net.splitcells.website.server.config.PublicDomain;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>The<a href="https://playwright.dev/python/docs/library#threading">Playwright library</a> <a href="https://github.com/microsoft/playwright-java/issues/1184">is not thread safe</a>.
 * Therefore, use 1 instance per thread.
 * Only creating 1 instance per program is even better,
 * as launching multiple ones often results in some broken ones,
 * that litter the process tree.
 * For this to be reactive, it is probably important to never use <a href="https://playwright.dev/java/docs/handles">handlers</a>,
 * as these are probably not thread safe and would therefore require additional synchronization.
 * This synchronization during the execution of one handler would block all other {@link HtmlClientImpl} actions.</p>
 * <p>TODO Test Firefox and Chromium automatically in tests using a {@link HtmlClient}.</p>
 * <p>Starting multiple Playwright instances in parallel sometimes does seem to cause the following error.
 * Therefore, it is prevented via monitors.
 * Maybe this was caused by a low thread limit inside a Podman container instead.
 * If this is the case, the synchronization of Playwright instance creation could be removed:
 * `[62.986s][warning][os,thread] Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN) for attributes: stacksize: 1024k, guardsize: 4k, detached.`</p>
 */
@JavaLegacy
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

    private static final Object PLAYWRIGHT_INIT_SYNCHRONIZER = new Object();

    private final Playwright playwright;
    private final Browser browser;
    private final Object playwrightSynchronizer = new Object();
    /**
     * The URL's schema and authority of the webserver.
     * For example, `http://localhost:8443` would be such a URL.
     */
    private final String address;
    private final List<Page> openTabs = list();
    /**
     * The life cycle management of {@link #openTabs} can only be done with their respective {@link BrowserContext}
     * according to the Playwright doc.
     */
    private final List<BrowserContext> tabContexts = list();

    private HtmlClientImpl(String addressArg) {
        address = addressArg;
        synchronized (PLAYWRIGHT_INIT_SYNCHRONIZER) {
            /* Starting multiple Playwright instances at once, sometimes creates a threading issue,
             * where some C code used by Playwright gets problems.
             */
            playwright = Playwright.create();
            browser = playwright.firefox().launch();
        }
    }

    @Override
    public Tab openTab(String path) {
        synchronized (playwrightSynchronizer) {
            final var context = browser.newContext();
            tabContexts.add(context);
            final var page = context.newPage();
            openTabs.add(page);
            page.navigate(address + path);
            return new Tab() {

                @Override
                public Element elementByClass(String cssClass) {
                    synchronized (playwrightSynchronizer) {
                        return new Element() {

                            final Locator locator = page.locator("." + cssClass);

                            @Override
                            public <T> Optional<T> evalIfExists(Function<Element, T> evaluation) {
                                if (!locator.all().isEmpty()) {
                                    try {
                                        return Optional.of(evaluation.apply(this));
                                    } catch (TimeoutError e) {
                                        throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
                                    }
                                }
                                return Optional.empty();
                            }

                            @Override
                            public void click() {
                                synchronized (playwrightSynchronizer) {
                                    try {
                                        locator.click();
                                    } catch (TimeoutError e) {
                                        throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
                                    }
                                }
                            }

                            @Override
                            public String textContent() {
                                synchronized (playwrightSynchronizer) {
                                    try {
                                        return locator.textContent();
                                    } catch (TimeoutError e) {
                                        throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
                                    }
                                }
                            }

                            @Override
                            public String value() {
                                synchronized (playwrightSynchronizer) {
                                    try {
                                        return locator.inputValue();
                                    } catch (TimeoutError e) {
                                        throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
                                    }
                                }
                            }
                        };
                    }
                }

                @Override
                public Element elementById(String id) {
                    synchronized (playwrightSynchronizer) {
                        return new Element() {

                            /**
                             * Dots have to be escaped, as otherwise this would be a search by an id,
                             * which is before the dot,
                             * and a CSS class, which is after the dot.
                             */
                            final Locator locator = page.locator("#" + id.replace(".", "\\."));

                            @Override
                            public <T> Optional<T> evalIfExists(Function<Element, T> evaluation) {
                                if (!locator.all().isEmpty()) {
                                    try {
                                        return Optional.of(evaluation.apply(this));
                                    } catch (TimeoutError e) {
                                        throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
                                    }
                                }
                                return Optional.empty();
                            }

                            @Override
                            public void click() {
                                synchronized (playwrightSynchronizer) {
                                    try {
                                        locator.click();
                                    } catch (TimeoutError e) {
                                        throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
                                    }
                                }
                            }

                            @Override
                            public String textContent() {
                                synchronized (playwrightSynchronizer) {
                                    try {
                                        return locator.textContent();
                                    } catch (TimeoutError e) {
                                        throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
                                    }

                                }
                            }

                            @Override
                            public String value() {
                                synchronized (playwrightSynchronizer) {
                                    try {
                                        return locator.inputValue();
                                    } catch (TimeoutError e) {
                                        throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
                                    }
                                }
                            }
                        };
                    }
                }

                /**
                 * The context and page might not be present, because they were already removed by the {@link HtmlClientImpl#close()} method.
                 */
                @Override
                public void close() {
                    synchronized (playwrightSynchronizer) {
                        context.close();
                        page.close();
                        tabContexts.deleteIfPresent(context);
                        openTabs.deleteIfPresent(page);
                    }
                }
            };
        }
    }

    @Override
    public void close() {
        synchronized (playwrightSynchronizer) {
            logs().warn(execException("Closing HTML clients is implemented, but is not actually expected to be used in production."));
            // TODO openTabs.forEach(Page::close);
            openTabs.removeAll();
            tabContexts.forEach(BrowserContext::close);
            tabContexts.removeAll();
            // TODO browser.close();
            // TODO playwright.close();
        }
    }

    @Override
    public void flush() {
        // Nothing needs to be done.
    }
}
