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

import java.util.Collections;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class StreamUtils {

    private StreamUtils() {
        throw new ConstructorIllegal();
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
