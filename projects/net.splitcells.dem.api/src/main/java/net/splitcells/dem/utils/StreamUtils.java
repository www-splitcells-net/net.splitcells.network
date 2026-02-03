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
package net.splitcells.dem.utils;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Collections;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 *
 * @deprecated {@link Flow} is the replacement.
 */
@JavaLegacy
@Deprecated
public final class StreamUtils {

    private StreamUtils() {
        throw constructorIllegal();
    }

    public static <T> Stream<T> streamOf(T... args) {
        return Stream.of(args);
    }

    @Deprecated
    public static <T> Stream<T> concat(Stream<T> a, Stream<T> b) {
        return java.util.stream.Stream.concat(a, b);
    }

    public static <T> Stream<T> concat(List<Stream<T>> streams) {
        var concatination = Stream.<T>empty();
        for (final var stream : streams) {
            concatination = Stream.concat(concatination, stream);
        }
        return concatination;
    }

    public static <T> Stream<T> concat(Stream<T> a, Stream<T>... b) {
        var concatination = a;
        for (int i = 0; i < b.length; ++i) {
            concatination = java.util.stream.Stream.concat(concatination, b[i]);
        }
        return concatination;
    }

    public static <T> Stream<T> stream(T[] content) {
        return Stream.of(content);
    }

    public static <T> Stream<T> emptyStream() {
        return Stream.empty();
    }

    public static <T> Stream<T> reverse(Stream<T> stream) {
        return stream.collect(
                        collectingAndThen(
                                toList(), l -> {
                                    Collections.reverse(l);
                                    return l;
                                }))
                .stream();
    }

    public static <T, E extends RuntimeException> BinaryOperator<T> ensureSingle() {
        return (element, otherElement) -> {
            throw new IllegalArgumentException();
        };
    }
}
