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
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.website.server.test.HtmlLiveTester;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * <p>Provides access to a {@link HtmlClient} to one consumer.
 * {@link HtmlClientSharer} closes only the resources created by the consumer.
 * Create one dedicated {@link HtmlClientSharer} for each consumer,
 * in order to avoid concurrency errors.
 * It does not close the resources of the underlying {@link HtmlClient}
 * created by other consumers via other {@link HtmlClientSharer}.</p>
 * <p>This allows to share one {@link HtmlClient} to all consumers,
 * which is important,
 * when many {@link HtmlClient} are needed (i.e. {@link HtmlLiveTester}).
 * The Problem with creating many real {@link HtmlClient},
 * is the fact, the underlying implementations can have a resource leak problem regarding their {@link HtmlClient#close()}.
 * Playwright, for instance, does not seem to kill all processes created by a Firefox instance on close.
 * Using one browser also speeds up the program,
 * as the browser instance does not have to be created and killed all the time.</p>
 * <p>TODO There should be a way, to close the {@link #subject} of the sharer,
 * so that a complete orderly shutdown is possible.</p>
 */
public class HtmlClientSharer implements HtmlClient {

    public static HtmlClient htmlClientSharer(HtmlClient client, Consumer<HtmlClient> onClosing) {
        return new HtmlClientSharer(client, onClosing);
    }

    private final HtmlClient subject;
    private final List<Closeable> tabs = list();
    private final Consumer<HtmlClient> onClosing;

    private HtmlClientSharer(HtmlClient client, Consumer<HtmlClient> argOnClosing) {
        subject = client;
        onClosing = argOnClosing;
    }

    @Override
    public Tab openTab(String path) {
        final var tab = subject.openTab(path);
        tabs.add(tab);
        return tab;
    }

    @Override
    public void close() {
        tabs.forEach(Closeable::close);
        tabs.removeAll();
        onClosing.accept(this);
    }

    @Override
    public void flush() {
        subject.flush();
    }
}
