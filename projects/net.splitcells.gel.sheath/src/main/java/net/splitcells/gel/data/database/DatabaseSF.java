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
        List<Function<List<Attribute<?>>, Database>> tester = list((a) -> DatabaseMetaAspect.databaseIRef(Databases.database(a)));
        return tester;
    }

}
