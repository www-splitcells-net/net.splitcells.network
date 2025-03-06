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
package net.splitcells.gel.editor.lang;

import net.splitcells.dem.data.atom.Thing;

public final class ReferenceDescription<T> implements ArgumentDescription {

    public static <R> ReferenceDescription<R> referenceDescription(String name, Class<? extends R> clazz) {
        return new ReferenceDescription<>(name, clazz);
    }

    private final String name;
    private final Class<? extends T> clazz;

    private ReferenceDescription(String argName, Class<? extends T> argClazz) {
        name = argName;
        clazz = argClazz;
    }

    public String name() {
        return name;
    }

    public Class<? extends T> clazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof ReferenceDescription<?> other) {
            return name.equals(other.name()) && clazz.equals(other.clazz());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(name, clazz);
    }
}
