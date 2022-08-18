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
package net.splitcells.dem.data.set;

import net.splitcells.dem.environment.resource.ResourceOptionI;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.SetFI_configured.setFiConfigured;
import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;
import static net.splitcells.dem.environment.config.StaticFlags.INLINE_STANDARD_FACTORIES;

public class Sets extends ResourceOptionI<SetF> {
    public Sets() {
        super(() -> setFiConfigured());
    }

    @JavaLegacyBody
    public static <T> java.util.stream.Collector<T, ?, Set<T>> toSetOfUniques() {
        return java.util.stream.Collector.of(
                Sets::<T>setOfUniques,
                Set::addAll,
                (a, b) -> {
                    a.addAll(b);
                    return a;
                }
        );
    }

    @JavaLegacyBody
    @SafeVarargs
    public static <T> Set<T> merge(java.util.Collection<T>... collections) {
        final var rVal = configValue(Sets.class).<T>set();
        for (java.util.Collection<T> collection : collections) {
            rVal.addAll(collection);
        }
        return rVal;
    }

    public static <T> Set<T> setOfUniques() {
        if (INLINE_STANDARD_FACTORIES) {
            return setLegacyWrapper(new java.util.LinkedHashSet<>());
        } else {
            return configValue(Sets.class).<T>set();
        }
    }

    @JavaLegacyBody
    @SafeVarargs
    public static <T> Set<T> setOfUniques(T... args) {
        if (INLINE_STANDARD_FACTORIES) {
            return setLegacyWrapper(new java.util.LinkedHashSet<T>()).with(args);
        } else {
            return setOfUniques(java.util.Arrays.asList(args));
        }
    }

    public static <T> Set<T> setOfUniques(java.util.Collection<T> arg) {
        if (INLINE_STANDARD_FACTORIES) {
            return setLegacyWrapper(new java.util.LinkedHashSet<T>()).with(arg);
        } else {
            final var rVal = configValue(Sets.class).<T>set();
            rVal.addAll(arg);
            return rVal;
        }
    }
}
