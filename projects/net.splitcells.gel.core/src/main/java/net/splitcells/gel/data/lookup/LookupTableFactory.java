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
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface LookupTableFactory extends ConnectingConstructor<LookupTable> {

    /**
     * @param table The {@link Table} on which the lookup will be performed.
     * @param name  This is the name of the {@link LookupTable} being constructed.
     * @return An instance, where no {@link Line} of {@link Table} is {@link LookupTable#register(Line)}.
     */
    LookupTable lookupTable(Table table, String name);

    /**
     * @param table     The {@link Table} on which the lookup will be performed.
     * @param attribute The {@link Attribute}, that will be looked up.
     * @return An instance, where no {@link Line} of {@link Table} is {@link LookupTable#register(Line)}.
     */
    LookupTable lookupTable(Table table, Attribute<?> attribute);

    LookupTable lookupTable(Table table, Attribute<?> attribute, boolean cacheRawLines);
}
