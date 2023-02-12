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
package net.splitcells.gel.data.table.attribute;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.gel.data.table.Line;

public interface Attribute<T> extends Domable {

    String name();

    default boolean equalz(Line arg) {
        return this == arg;
    }

    Bool isInstanceOf(Object arg);

    /**
     * Asserts that {@link #isInstanceOf} returns {@code true} given {@code arg}.
     * If this not the case, an exception with an description is thrown.
     *
     * @param arg Object Required To Be Usable For This
     */
    void assertArgumentCompatibility(Object arg);
    
    default T deserializeValue(String value) {
        throw new UnsupportedOperationException();
    }
    
}
