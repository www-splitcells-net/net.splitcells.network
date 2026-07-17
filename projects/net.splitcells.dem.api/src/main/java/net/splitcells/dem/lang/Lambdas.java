/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
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
