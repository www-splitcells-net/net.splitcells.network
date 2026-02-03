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
import net.splitcells.dem.utils.CommonFunctions;

public class BoolI implements Bool {
    public static Bool bool(boolean truthValue) {
        return new BoolI(truthValue);
    }

    private final boolean value;

    @Deprecated
    private BoolI(boolean argVal) {
        value = argVal;
    }

    @Override
    public boolean isTrue() {
        return value;
    }

    @Override
    public boolean isFalse() {
        return !value;
    }

    @Override
    public Bool or(Bool arg) {
        return new BoolI(value || arg.isTrue());
    }

    @Override
    public Bool set(boolean arg) {
        return new BoolI(arg);
    }

    @Override
    public Bool xor(Bool arg) {
        return new BoolI(value != arg.isTrue());
    }

    @Override
    public Bool not() {
        return new BoolI(!value);
    }

    @Override
    public Bool and(Bool arg) {
        return new BoolI(value && arg.isTrue());
    }

    public Bool equals(Bool arg) {
        if (value == arg.isTrue()) {
            return new BoolI(true);
        } else {
            return new BoolI(false);
        }
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Bool bool) {
            return bool.isTrue() == isTrue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R deepClone(Class<? extends R> arg) {
        return (R) new BoolI(this.value);
    }

    @JavaLegacy
    @Override
    public boolean toJavaPrimitive() {
        return value;
    }

    @Deprecated
    public <A extends Bool> Bool equalContent(A arg) {
        return Bools.bool(value == arg.toJavaPrimitive());
    }

}
