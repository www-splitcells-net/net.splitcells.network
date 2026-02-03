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

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.lang.reflect.Array;
import java.util.Arrays;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public final class Lists {

    public static <T> T lastValueOf(T... arg) {
        if (arg.length == 0) {
            throw execException("Cannot get last element of array, because it is empty.");
        }
        return arg[arg.length - 1];
    }

    @JavaLegacy
    public static <T> java.util.stream.Collector<T, ?, List<T>> toList() {
        return java.util.stream.Collector.of(
                () -> list(),
                (a, b) -> a.addAll(b),
                (a, b) -> {
                    a.addAll(b);
                    return a;
                }
        );
    }

    private Lists() {
        throw constructorIllegal();
    }

    @SafeVarargs
    @JavaLegacy
    public static <T> List<T> concat(java.util.Collection<T>... collections) {
        final var rVal = Lists.<T>list();
        for (java.util.Collection<T> collection : collections) {
            rVal.addAll(collection);
        }
        return rVal;
    }

    public static <T> List<T> list() {
        return ListI._list();
    }

    @JavaLegacy
    public static <T> List<T> listWithValuesOf(java.util.Collection<T> values) {
        final var list = Lists.<T>list();
        list.addAll(values);
        return list;
    }

    @SafeVarargs
    @JavaLegacy
    public static <T> List<T> listWithValuesOf(T... values) {
        return listWithValuesOf(java.util.Arrays.asList(values));
    }

    @SafeVarargs
    @JavaLegacy
    public static <T> List<T> list(T... args) {
        final var list = ListI.<T>_list();
        list.addAll(java.util.Arrays.asList(args));
        return list;
    }

    public static <T> List<List<T>> listOfShallowCopies(List<T> element, int numberOfCopies) {
        final List<List<T>> listOfShallowCopies = list();
        rangeClosed(1, numberOfCopies).forEach(i -> listOfShallowCopies.add(listWithValuesOf(element)));
        return listOfShallowCopies;
    }

    /**
     * This is an attempt to create large empty lists quickly.
     *
     * @param content
     * @param multiple
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> listWithMultiple(T content, int multiple, Class<T> type) {
        T[] resultContent = (T[]) Array.newInstance(type, multiple);
        Arrays.fill(resultContent, content);
        final var list = ListI.<T>_list();
        list.prepareForSizeOf(multiple);
        list.addAll(Arrays.asList(resultContent));
        return list;
    }

    public static <T> List<T> synchronizedList() {
        return SynchronizedList._synchronizedList();
    }

}
