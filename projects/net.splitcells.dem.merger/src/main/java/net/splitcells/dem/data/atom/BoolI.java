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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.utils.CommonFunctions;

public class BoolI implements Bool {
    public static Bool bool(boolean truthValue) {
        return new BoolI(truthValue);
    }

    private final boolean value;

    @Deprecated
    private BoolI(boolean arg_val) {
        value = arg_val;
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
        return equals((Bool) arg).isTrue();
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(value);
    }

    @Override
    public BoolI clone() {
        return deepClone(BoolI.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R deepClone(Class<? extends R> arg) {
        return (R) new BoolI(this.value);
    }

    @JavaLegacyArtifact
    @Override
    public boolean toJavaPrimitive() {
        return value;
    }

    @Deprecated
    public <A extends Bool> Bool equalContent(A arg) {
        return Bools.bool(value == arg.toJavaPrimitive());
    }

}
