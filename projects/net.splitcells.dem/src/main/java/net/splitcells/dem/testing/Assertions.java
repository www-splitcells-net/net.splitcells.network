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
package net.splitcells.dem.testing;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.list.List;
import org.assertj.core.api.Condition;

import java.util.function.Predicate;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {
    private Assertions() {
        throw constructorIllegal();
    }

    public static <T> void assertComplies(T subject, Predicate<T> constraint, String description) {
        assertThat(subject).is(new Condition<T>(constraint, description));
    }
    
    public static void assertThrows(Class<? extends Throwable> expectedExceptionType, Runnable run) {
        try {
            run.run();
        } catch (Throwable th) {
            if (expectedExceptionType.isInstance(th)) {
                
            } else {
                throw new RuntimeException("Runnable should throw `" + expectedExceptionType + "` but did throw  ");
            }
        }
        throw new RuntimeException("Runnable should throw `" + expectedExceptionType + "` but did not.");
    }
}
