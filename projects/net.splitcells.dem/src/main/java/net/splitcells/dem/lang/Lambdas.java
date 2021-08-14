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
package net.splitcells.dem.lang;

import java.util.function.Function;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Lambdas {
    private Lambdas() {
        throw constructorIllegal();
    }

    public static <P, R> Function<P, R> describedFunction(Function<P, R> arg, String description) {
        return new Function<P, R>() {

            @Override
            public R apply(P p) {
                return arg.apply(p);
            }
            
            @Override
            public String toString() {
                return description;
            }
        };
    }
}
