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
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.set.AppendableSet;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.annotations.ReturnsThis;

/**
 * <p>This is a list interface that contains only methods to add values.</p>
 * <p>TODOC What is the difference between append and add? -> add set specific and
 * the method does not guarrenties that is added to the of the list. Append addsto end.</p>
 *
 * @param <T>
 */
public interface AppendableList<T> extends AppendableSet<T> {
    @ReturnsThis
    <R extends AppendableList<T>> R append(T arg);

    @Override
    default <R extends AppendableSet<T>> R add(T arg) {
        return append(arg);
    }

    @SuppressWarnings("unchecked")
    @ReturnsThis
    default <R extends AppendableList<T>> R appendAll(T... arg) {
        for (int i = 0; i < arg.length; ++i) {
            append(arg[i]);
        }
        return (R) this;
    }

    @JavaLegacy
    @SuppressWarnings("unchecked")
    @ReturnsThis
    default <R extends AppendableList<T>> R appendAll(java.util.Collection<T> arg) {
        arg.forEach(e -> this.append(e));
        return (R) this;
    }

    @JavaLegacy
    @Override
    default <R extends AppendableSet<T>> R addAll(java.util.Collection<T> value) {
        return appendAll(value);
    }
}
