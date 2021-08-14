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
    
    default T deserializeValue(String value) {
        throw new UnsupportedOperationException();
    }
    
}
