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

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.client.HtmlClientImpl.htmlClientImpl;
import static net.splitcells.website.server.client.HtmlClientShare.htmlClientShare;

public class HtmlClients {
    private static final List<HtmlClient> FREE_HTML_CLIENT = list();
    private static final List<HtmlClient> USED_HTML_CLIENT = list();
    private static final Semaphore HTML_CLIENT_LOCK = new Semaphore(1);
    private static final Semaphore HTML_CLIENT_WAIT_LOCK = new Semaphore(0);
    private static final int MAX_HTML_CLIENT_COUNT = HostHardware.cpuCoreCount();

    private HtmlClients() {

    }

    /**
     * This method is thread safe.
     *
     * TODO This method should probably be moved to {@link HtmlClientImpl},
     * as it is Playwright specific.
     * In this class the method htmlClient should link to the moved `HtmlClientImpl#htmlClient`.
     *
     * @return Provides an instance of {@link HtmlClient} from a limited pool.
     * When the returned {@link HtmlClient#close()} is called,
     * the {@link HtmlClient} is given back to the pool automatically.
     * Use the returned {@link HtmlClient} only for a limited time,
     * as this may otherwise block other callers indefinitely.
     * Also, only use one {@link HtmlClient} per thread.
     * All threads, share a single {@link HtmlClientImpl} instance,
     * in order to minimize the probability of Playwright specific errors by minimizing the number of Playwright NodeJS based workers.
     * Apart from the first one, every additional NodeJS based workers seems to create an unreasonable high probability of error.
     */
    public static HtmlClient htmlClient() {
        if (true) {
            return HtmlClientSharer.htmlClient();
        }
        try {
            final HtmlClient htmlClient;
            HTML_CLIENT_LOCK.acquireUninterruptibly();
            if (FREE_HTML_CLIENT.hasElements()) {
                htmlClient = FREE_HTML_CLIENT.remove(0);
                USED_HTML_CLIENT.add(htmlClient);
                return htmlClient;
            }
            if (MAX_HTML_CLIENT_COUNT > USED_HTML_CLIENT.size()) {
                htmlClient = htmlClientShare(htmlClientImpl(), sharer -> {
                    try {
                        HTML_CLIENT_LOCK.acquireUninterruptibly();
                        FREE_HTML_CLIENT.add(sharer);
                        USED_HTML_CLIENT.delete(sharer);
                    } finally {
                        HTML_CLIENT_LOCK.release();
                    }
                });
                USED_HTML_CLIENT.add(htmlClient);
                return htmlClient;
            }
        } finally {
            HTML_CLIENT_LOCK.release();
        }
        while (true) {
            try {
                HTML_CLIENT_WAIT_LOCK.acquireUninterruptibly();
                try {
                    HTML_CLIENT_LOCK.acquireUninterruptibly();
                    if (FREE_HTML_CLIENT.hasElements()) {
                        return FREE_HTML_CLIENT.remove(0);
                    }
                } finally {
                    HTML_CLIENT_LOCK.release();
                }
            } finally {
                HTML_CLIENT_WAIT_LOCK.release();
            }
        }
    }
}
