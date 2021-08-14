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
package net.splitcells.gel.data.lookup;

import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public class LookupIFactory implements LookupFactory {
    @Override
    public <R> Lookup<R> lookup(Table table, Attribute<R> attribute) {
        return new LookupI<>(table, attribute);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
