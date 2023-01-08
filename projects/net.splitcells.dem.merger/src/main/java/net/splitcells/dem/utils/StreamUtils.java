/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Collections;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

@JavaLegacyArtifact
public final class StreamUtils {

    private StreamUtils() {
        throw constructorIllegal();
    }

    public static <T> Stream<T> concat(Stream<T> a, Stream<T> b) {
        return java.util.stream.Stream.concat(a, b);
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
