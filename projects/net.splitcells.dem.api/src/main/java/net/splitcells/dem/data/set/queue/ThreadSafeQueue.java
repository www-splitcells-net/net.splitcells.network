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
package net.splitcells.dem.data.set.queue;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class ThreadSafeQueue<T> implements Queue<T> {
    public static <T> ThreadSafeQueue<T> threadSafeQueue() {
        return new ThreadSafeQueue<>();
    }

    private final LinkedBlockingQueue<T> content;

    private ThreadSafeQueue() {
        content = new LinkedBlockingQueue<>();
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return content.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    @Override
    public Object[] toArray() {
        return content.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return content.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return content.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return content.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return content.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return content.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return content.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return content.retainAll(c);
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public boolean offer(T t) {
        return content.offer(t);
    }

    @Override
    public T remove() {
        return content.remove();
    }

    @Override
    public T poll() {
        return content.poll();
    }

    @Override
    public T element() {
        return content.element();
    }

    @Override
    public T peek() {
        return content.peek();
    }

    @Override
    public Optional<T> pollNext() {
        try {
            return Optional.ofNullable(content.poll(1L, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw execException(e);
        }
    }
}
