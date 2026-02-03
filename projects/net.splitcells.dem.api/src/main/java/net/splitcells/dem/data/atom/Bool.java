/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.atom;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.object.DeepCloneable;

/**
 * This is a boolean interface.
 */
public interface Bool extends DeepCloneable {

    @JavaLegacy
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
    default Bool required() {
        if (isFalse()) {
            throw new RuntimeException();
        }
        return this;
    }

    @ReturnsThis
    default Bool requireFalse() {
        if (isTrue()) {
            throw new RuntimeException();
        }
        return this;
    }
}
