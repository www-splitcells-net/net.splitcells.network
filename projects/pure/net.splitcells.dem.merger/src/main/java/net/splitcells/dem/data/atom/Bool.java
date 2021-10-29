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

import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.object.DeepCloneable;

public interface Bool extends DeepCloneable {

    boolean toJavaPrimitive();

    Bool set(boolean arg);

    Bool or(Bool arg);

    Bool xor(Bool arg);

    Bool not();

    Bool and(Bool arg);

    default Bool nand(Bool arg) {
        return this.and(arg).not();
    }

    default Bool nor(Bool arg) {
        return or(arg).not();
    }
    
    default Bool xnor(Bool arg) {
        return xor(arg).not();
    }

    boolean isTrue();

    boolean isFalse();

    @SuppressWarnings("unchecked")
    @ReturnsThis
    default <R extends DeepCloneable> R required() {
        if (isFalse()) {
            throw new RuntimeException();
        }
        return (R) this;
    }
}
