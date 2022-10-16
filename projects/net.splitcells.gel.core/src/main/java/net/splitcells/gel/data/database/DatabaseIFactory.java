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
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.AspectOrientedConstructorBase;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.table.attribute.Attribute;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.list.ListI.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.resource.AspectOrientedConstructorBase.aspectOrientedConstructor;
import static net.splitcells.dem.resource.ConnectingConstructorI.connectingConstructor;

public class DatabaseIFactory implements DatabaseFactory {

    private final AspectOrientedConstructorBase<Database> aspects = aspectOrientedConstructor();
    private final ConnectingConstructor<Database> connector = connectingConstructor();

    public static DatabaseFactory databaseFactory(Consumer<Database> databaseConsumer) {
        return new DatabaseIFactory(databaseConsumer);
    }

    public static DatabaseFactory databaseFactory() {
        return new DatabaseIFactory();
    }

    private final Consumer<Database> databaseConsumer;

    private DatabaseIFactory(Consumer<Database> databaseConsumer) {
        this.databaseConsumer = databaseConsumer;
    }

    private DatabaseIFactory() {
        databaseConsumer = x -> {
        };
    }

    @Override
    public Database database(String name, Attribute<? extends Object>... attributes) {
        final var database = joinAspects(new DatabaseI(name, null, attributes));
        connector.connect(database);
        return database;
    }

    @Override
    public Database database(Attribute<? extends Object>... attributes) {
        final var database= joinAspects(new DatabaseI(attributes));
        connector.connect(database);
        return database;
    }

    @Override
    public Database database(List<Attribute<?>> attributes) {
        final var database = joinAspects(new DatabaseI(attributes));
        connector.connect(database);
        return database;
    }

    @Override
    public Database database2(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        final var database = joinAspects(new DatabaseI(name, parent, attributes));
        connector.connect(database);
        return database;
    }

    @Override
    public Database database(List<Attribute<? extends Object>> attributes, List<List<Object>> linesValues) {
        final var database = joinAspects(new DatabaseI(attributes, linesValues));
        connector.connect(database);
        return database;
    }

    @Override
    @Deprecated
    public Database database(String name, Discoverable parent, Attribute<? extends Object>... attributes) {
        final var database = joinAspects(new DatabaseI(name, parent, listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a)));
        connector.connect(database);
        return database;
    }

    @Override
    public Database database(String name, Discoverable parent, List<Attribute<? extends Object>> attributes) {
        final var database = joinAspects(new DatabaseI(name, parent, attributes.mapped(a -> (Attribute<Object>) a)));
        connector.connect(database);
        return database;
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }

    @Override
    public AspectOrientedConstructor<Database> withAspect(Function<Database, Database> aspect) {
        return aspects.withAspect(aspect);
    }

    @Override
    public Database joinAspects(Database arg) {
        return aspects.joinAspects(arg);
    }

    @Override
    public ConnectingConstructor withConnector(Consumer<Database> connector) {
        this.connector.withConnector(connector);
        return this;
    }

    @Override
    public void connect(Database subject) {
        connector.connect(subject);
    }
}
