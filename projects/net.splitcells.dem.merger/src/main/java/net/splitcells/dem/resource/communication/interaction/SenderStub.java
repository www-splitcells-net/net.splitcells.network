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
package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.resource.communication.Sender;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class SenderStub<T> implements Sender<T> {
    public static <T> SenderStub<T> create() {
        return new SenderStub<>();
    }

    private final List<T> storage = list();
    private boolean closed = false;
    private int flushCount = 0;

    private SenderStub() {

    }

    @Override
    public <R extends ListWA<T>> R append(T arg) {
        if (closed) {
            throw executionException("Sender already closed:" + this);
        }
        storage.add(arg);
        return (R) this;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public void flush() {
        ++flushCount;
    }

    public int flushCount() {
        return flushCount;
    }

    public boolean closed() {
        return closed;
    }

    public List<T> storage() {
        return storage;
    }
}
