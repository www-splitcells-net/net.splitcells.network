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
package net.splitcells.website.server.test;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.website.server.client.HtmlClientImpl;

import static net.splitcells.website.server.client.HtmlClients.htmlClient;

/**
 * <p>This {@link Option} provides a {@link Runnable},
 * that tests the HTML GUI of the {@link net.splitcells.website.server.ServerService},
 * that is currently running.
 * The tests are intended to be run during tests as well as during production.</p>
 * <p>For any tester it is recommend, to use the {@link HtmlClientImpl#htmlClientImpl()}
 * for each test, in order to access the HTML based UI.</p>
 */
public class HtmlLiveTest implements Option<Runnable> {
    /**
     * @return By default it is tested, whether the root of the HTML website, provided by the server,
     * can be opened in the browser.
     */
    @Override
    public Runnable defaultValue() {
        return () -> {
            try (final var browser = htmlClient()) {
                browser.openTab("/");
            }
        };
    }
}
