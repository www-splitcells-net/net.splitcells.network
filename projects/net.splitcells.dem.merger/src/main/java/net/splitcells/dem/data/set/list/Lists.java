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
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.utils.ConstructorIllegal;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public final class Lists {

    @JavaLegacyBody
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
    @JavaLegacyBody
    public static <T> List<T> concat(java.util.Collection<T>... collections) {
        final var rVal = Lists.<T>list();
        for (java.util.Collection<T> collection : collections) {
            rVal.addAll(collection);
        }
        return rVal;
    }

    public static <T> List<T> list() {
        return ListI.list();
    }

    @JavaLegacyBody
    public static <T> List<T> listWithValuesOf(java.util.Collection<T> values) {
        final var list = Lists.<T>list();
        list.addAll(values);
        return list;
    }

    @SafeVarargs
    @JavaLegacyBody
    public static <T> List<T> listWithValuesOf(T... values) {
        return listWithValuesOf(java.util.Arrays.asList(values));
    }

    @SafeVarargs
    @JavaLegacyBody
    public static <T> List<T> list(T... args) {
        final var list = ListI.<T>list();
        list.addAll(java.util.Arrays.asList(args));
        return list;
    }

}
