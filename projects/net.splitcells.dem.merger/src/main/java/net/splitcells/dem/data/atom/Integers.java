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
package net.splitcells.dem.data.atom;

import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class Integers {
    private Integers() {
        throw constructorIllegal();
    }

    @JavaLegacyBody
    public static boolean isEven(Integer arg) {
        return arg % 2 == 0;
    }

    public static void requireEqualInts(int a, int b) {
        if (a != b) {
            throw executionException("Ints should be equals, but are not: " + a + ", " + b);
        }
    }
}
