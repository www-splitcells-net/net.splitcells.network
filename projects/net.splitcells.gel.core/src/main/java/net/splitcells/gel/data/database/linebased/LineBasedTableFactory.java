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
package net.splitcells.gel.data.database.linebased;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.AspectOrientedConstructorBase;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.database.Table;
import net.splitcells.gel.data.database.TableFactory;
import net.splitcells.gel.data.view.attribute.Attribute;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.resource.AspectOrientedConstructorBase.aspectOrientedConstructor;
import static net.splitcells.dem.resource.ConnectingConstructorI.connectingConstructor;
import static net.splitcells.gel.data.database.linebased.LineBasedTable.lineBasedDatabase;

public class LineBasedTableFactory implements TableFactory {

    private final AspectOrientedConstructorBase<Table> aspects = aspectOrientedConstructor();
    private final ConnectingConstructor<Table> connector = connectingConstructor();

    public static TableFactory lineBasedDatabaseFactory() {
        return new LineBasedTableFactory();
    }

    private LineBasedTableFactory() {

    }

    @Override
    public AspectOrientedConstructor<Table> withAspect(Function<Table, Table> aspect) {
        aspects.withAspect(aspect);
        return this;
    }

    @Override
    public Table joinAspects(Table arg) {
        return aspects.joinAspects(arg);
    }

    @Override
    public ConnectingConstructor<Table> withConnector(Consumer<Table> connector) {
        this.connector.withConnector(connector);
        return this;
    }

    @Override
    public Table connect(Table subject) {
        return connector.connect(subject);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }

    @Override
    public Table database(String name, Attribute<?>... attributes) {
        final var database = joinAspects(lineBasedDatabase(name, Optional.empty()
                , listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a)));
        connector.connect(database);
        return database;
    }

    @Override
    public Table database(Attribute<?>... attributes) {
        final var database = joinAspects(lineBasedDatabase(getClass().getSimpleName(), Optional.empty()
                , listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a)));
        connector.connect(database);
        return database;
    }

    @Override
    public Table database(List<Attribute<?>> attributes) {
        final var database = joinAspects(lineBasedDatabase(getClass().getSimpleName(), Optional.empty()
                , attributes.mapped(a -> (Attribute<Object>) a)));
        connector.connect(database);
        return database;
    }

    @Override
    public Table database2(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        final var database = joinAspects(lineBasedDatabase(name, Optional.of(parent), attributes));
        connector.connect(database);
        return database;
    }

    @Override
    public Table database(List<Attribute<?>> attributes, List<List<Object>> linesValues) {
        final var database = joinAspects(lineBasedDatabase("", Optional.empty()
                , attributes.mapped(a -> (Attribute<Object>) a)));
        connector.connect(database);
        linesValues.forEach(database::addTranslated);
        return database;
    }

    @Override
    public Table database(String name, Discoverable parent, Attribute<?>... attributes) {
        final var database = joinAspects(lineBasedDatabase(name, Optional.empty()
                , listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a)));
        connector.connect(database);
        return database;
    }

    @Override
    public Table database(String name, Discoverable parent, List<Attribute<?>> attributes) {
        final var database = joinAspects(lineBasedDatabase(name, Optional.empty()
                , listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a)));
        connector.connect(database);
        return database;
    }
}
