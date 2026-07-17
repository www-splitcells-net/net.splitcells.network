/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.lambdas;

import java.util.function.Predicate;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class DescriptiveLambda {
    private DescriptiveLambda() {
        throw constructorIllegal();
    }

    public static <T> Predicate<T> describedPredicate(Predicate<T> predicate, String description) {
        return new Predicate<>() {

            @Override
            public boolean test(T t) {
                return predicate.test(t);
            }

            @Override
            public String toString() {
                return description;
            }
        };
    }
}
