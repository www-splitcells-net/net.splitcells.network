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
package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.table.attribute.Attributes.attributeATO;


public class DatabaseSF {

    public static List<Database> emptyDatabase() {
        return list(Databases.database());
    }

    public static List<Database> emptyDatabase2() {
        return list(Databases.database(attributeATO()));
    }

    public static List<Function<List<Attribute<?>>, Database>> testDatabaseFactory() {
        List<Function<List<Attribute<?>>, Database>> tester = list((a) -> DatabaseIRef.databaseIRef(Databases.database(a)));
        return tester;
    }

}
