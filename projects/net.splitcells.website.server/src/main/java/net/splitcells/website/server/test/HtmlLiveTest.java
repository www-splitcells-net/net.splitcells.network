/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.test;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.website.server.client.HtmlClientImpl;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
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
                browser.openTab("/").close();
            }
        };
    }

    @Override public Optional<Tree> serialize(Runnable currentValue) {
        return Optional.of(tree(getClass() + " is enabled."));
    }
}
