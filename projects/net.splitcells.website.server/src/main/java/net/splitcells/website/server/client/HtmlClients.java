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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.HostHardware;

import java.util.concurrent.Semaphore;

import static net.splitcells.dem.data.set.list.Lists.synchronizedList;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.client.HtmlClientImpl.htmlClientImpl;
import static net.splitcells.website.server.client.HtmlClientSharer.htmlClientSharer;

public class HtmlClients {
    private static final List<HtmlClient> FREE_HTML_CLIENT = synchronizedList();
    private static final List<HtmlClient> USED_HTML_CLIENT = synchronizedList();
    /**
     * TODO Using a Java monitor via an {@link Object} would probably lead to less complex code.
     */
    private static final Semaphore HTML_CLIENT_LOCK = new Semaphore(1);
    private static final int MAX_HTML_CLIENT_COUNT = HostHardware.cpuCoreCount();

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
     * Also, only use one {@link HtmlClient} per thread.
     */
    public static HtmlClient htmlClient() {
        try {
            HtmlClient htmlClient;
            HTML_CLIENT_LOCK.acquireUninterruptibly();
            if (FREE_HTML_CLIENT.hasElements()) {
                htmlClient = FREE_HTML_CLIENT.remove(0);
                USED_HTML_CLIENT.add(htmlClient);
                return htmlClient;
            }
            if (MAX_HTML_CLIENT_COUNT > USED_HTML_CLIENT.size()) {
                htmlClient = htmlClientSharer(htmlClientImpl(), (sharer) -> {
                    FREE_HTML_CLIENT.add(sharer);
                    USED_HTML_CLIENT.delete(sharer);
                    HTML_CLIENT_LOCK.release();
                });
                USED_HTML_CLIENT.add(htmlClient);
                return htmlClient;
            }
            while (true) {
                try {
                    HTML_CLIENT_LOCK.acquireUninterruptibly();
                    if (FREE_HTML_CLIENT.hasElements()) {
                        htmlClient = FREE_HTML_CLIENT.remove(0);
                        USED_HTML_CLIENT.add(htmlClient);
                        return htmlClient;
                    }
                } finally {
                    HTML_CLIENT_LOCK.release();
                }
            }
        } finally {
            HTML_CLIENT_LOCK.release();
        }
    }
}
