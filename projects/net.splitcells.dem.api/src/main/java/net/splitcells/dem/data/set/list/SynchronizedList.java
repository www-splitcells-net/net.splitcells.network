/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.execution.ImplicitEffect;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.concurrent.CopyOnWriteArrayList;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

@JavaLegacy
public class SynchronizedList<T> extends CopyOnWriteArrayList<T> implements List<T>, ImplicitEffect {
    public static <T> List<T> _synchronizedList() {
        return new SynchronizedList<>();
    }

    private SynchronizedList() {
    }

    @Override
    public synchronized Flow<T> stream() {
        return shallowCopy().stream();
    }

    @Override
    public synchronized List<T> shallowCopy() {
        final List<T> shallowCopy = Lists.list();
        shallowCopy.addAll(this);
        return shallowCopy;
    }
}
