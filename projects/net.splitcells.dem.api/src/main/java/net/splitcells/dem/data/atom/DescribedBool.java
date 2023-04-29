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

import java.util.function.Supplier;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class DescribedBool implements Bool {

    public static Bool describedBool(boolean content, String description) {
        return new DescribedBool(content, () -> description);
    }

    public static Bool describedBool(boolean content, Supplier<String> description) {
        return new DescribedBool(content, description);
    }

    private final boolean content;
    private final Supplier<String> description;

    private DescribedBool(boolean content, Supplier<String> description) {
        this.content = content;
        this.description = description;
    }

    @Override
    public boolean toJavaPrimitive() {
        return content;
    }

    @Override
    public Bool set(boolean arg) {
        throw notImplementedYet();
    }

    @Override
    public Bool or(Bool arg) {
        throw notImplementedYet();
    }

    @Override
    public Bool xor(Bool arg) {
        throw notImplementedYet();
    }

    @Override
    public Bool not() {
        throw notImplementedYet();
    }

    @Override
    public Bool and(Bool arg) {
        throw notImplementedYet();
    }

    @Override
    public boolean isTrue() {
        return content;
    }

    @Override
    public boolean isFalse() {
        return !content;
    }

    @Override
    public Bool required() {
        if (!content) {
            throw new RuntimeException("The following is required, but not true: " + description.get());
        }
        return this;
    }

    @Override
    public Bool requireFalse() {
        if (content) {
            throw new RuntimeException("The following is prohibited, but is true: " + description.get());
        }
        return this;
    }
}
