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
import net.splitcells.dem.environment.resource.ResourceOptionI;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.view.attribute.Attribute;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.gel.data.database.TableIFactory.databaseFactory;

public class Databases extends ResourceOptionI<TableFactory> {
    public Databases() {
        super(() -> databaseFactory());
    }

    @SafeVarargs
    public static Table database(String name, Attribute<? extends Object>... attributes) {
        return environment().config().configValue(Databases.class).database(name, attributes);
    }

    /**
     * TODO REMOVE Every database should have a name.
     *
     * @param attributes
     * @return
     */
    @SafeVarargs
    @Deprecated
    public static Table database(Attribute<? extends Object>... attributes) {
        return environment().config().configValue(Databases.class).database(attributes);
    }

    /**
     * TODO REMOVE Every database should have a name.
     *
     * @param attributes
     * @param linesValues
     * @return
     */
    @Deprecated
    public static Table database(List<Attribute<? extends Object>> attributes, List<List<Object>> linesValues) {
        return environment().config().configValue(Databases.class).database(attributes, linesValues);
    }

    public static Table database(String name, Discoverable parent, List<Attribute<? extends Object>> attributes) {
        return environment().config().configValue(Databases.class).database(name, parent, attributes);
    }

    /**
     * TODO REMOVE Every database should have a name.
     *
     * @param attributes
     * @return
     */
    @Deprecated
    public static Table database(List<Attribute<?>> attributes) {
        return environment().config().configValue(Databases.class).database(attributes);
    }

    /**
     * TODO REMOVE Every database should have a name.
     *
     * @param attributes
     * @return
     */
    @Deprecated
    public static Table database2(List<Attribute<Object>> attributes) {
        return environment().config().configValue(Databases.class).database(attributes.mapped(a -> (Attribute<Object>) a));
    }

    public static Table database2(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        return environment().config().configValue(Databases.class).database2(name, parent, attributes);
    }

    /**
     * TODO REMOVE Every database should have a name.
     *
     * @param attributes
     * @return
     */
    @Deprecated
    public static List<Attribute<? extends Object>> objectAttributes(List<Attribute<Object>> attributes) {
        return attributes.stream()
                .map(a -> (Attribute<? extends Object>) a)
                .collect(toList());
    }
}
