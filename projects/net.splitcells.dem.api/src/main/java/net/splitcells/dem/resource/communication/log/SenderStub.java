/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.AppendableList;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ExecutionException.execException;

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
    public <R extends AppendableList<T>> R append(T arg) {
        if (closed) {
            throw ExecutionException.execException("Sender already closed:" + this);
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
