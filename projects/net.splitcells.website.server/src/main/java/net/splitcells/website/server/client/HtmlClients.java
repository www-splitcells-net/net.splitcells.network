/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.client;

public class HtmlClients {

    private HtmlClients() {

    }

    /**
     * This method is thread safe.
     *
     * @return Provides an instance of {@link HtmlClient} from a limited pool.
     * When the returned {@link HtmlClient#close()} is called,
     * the {@link HtmlClient} is given back to the pool automatically.
     * Use the returned {@link HtmlClient} only for a limited time,
     * as this may otherwise block other callers indefinitely.
     */
    public static HtmlClient htmlClient() {
        return HtmlClientSharer.htmlClient();
    }
}
