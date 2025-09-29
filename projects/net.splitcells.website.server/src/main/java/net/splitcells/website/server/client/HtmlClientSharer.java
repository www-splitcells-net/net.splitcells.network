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

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.HostHardware;

import java.util.concurrent.Semaphore;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.client.HtmlClientImpl.htmlClientImpl;
import static net.splitcells.website.server.client.HtmlClientShare.htmlClientSharer;

public class HtmlClientSharer {
    private static final HtmlClientSharer SHARER = new HtmlClientSharer();

    private final List<HtmlClient> freeClients = list();
    private final List<HtmlClient> usedClients = list();
    private static final Semaphore clientWait = new Semaphore(0);
    private final int maxClientCount = HostHardware.cpuCoreCount();

    private HtmlClientSharer() {
    }

    public HtmlClient _htmlCLient() {
        while (true) {
            synchronized (this) {
                if (freeClients.hasElements()) {
                    val htmlClient = freeClients.remove(0);
                    usedClients.add(htmlClient);
                    return htmlClient;
                } else if (maxClientCount > freeClients.size() + usedClients.size()) {
                    val htmlClient = htmlClientSharer(htmlClientImpl(), this::giveBack);
                    usedClients.add(htmlClient);
                    return htmlClient;
                }
            }
            clientWait.acquireUninterruptibly();
        }
    }

    private synchronized HtmlClient giveBack(HtmlClient client) {
        usedClients.delete(client);
        freeClients.add(client);
        clientWait.release();
        return null;
    }

    public static HtmlClient htmlClient() {
        return SHARER._htmlCLient();
    }
}
