/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.AspectOrientedConstructorBase;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.view.attribute.Attribute;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.resource.AspectOrientedConstructorBase.aspectOrientedConstructor;
import static net.splitcells.dem.resource.ConnectingConstructorI.connectingConstructor;
import static net.splitcells.gel.data.table.TableI.tableI;

public class TableIFactory implements TableFactory {

    private final AspectOrientedConstructorBase<Table> aspects = aspectOrientedConstructor();
    private final ConnectingConstructor<Table> connector = connectingConstructor();

    public static TableFactory databaseFactory(Consumer<Table> databaseConsumer) {
        return new TableIFactory(databaseConsumer);
    }

    public static TableFactory databaseFactory() {
        return new TableIFactory();
    }

    private final Consumer<Table> databaseConsumer;

    private TableIFactory(Consumer<Table> databaseConsumer) {
        this.databaseConsumer = databaseConsumer;
    }

    private TableIFactory() {
        databaseConsumer = x -> {
        };
    }

    @Override
    public Table table(String name, Attribute<? extends Object>... attributes) {
        return connector.connect(joinAspects(tableI(name, null, attributes)));
    }

    @Override
    public Table table(Attribute<? extends Object>... attributes) {
        return connector.connect(joinAspects(tableI(attributes)));
    }

    @Override
    public Table table(List<Attribute<?>> attributes) {
        return connector.connect(joinAspects(tableI(attributes)));
    }

    @Override
    public Table table2(String name, Discoverable parent, List<Attribute<Object>> attributes) {
        return connector.connect(joinAspects(tableI(name, parent, attributes)));
    }

    @Override
    public Table table(List<Attribute<? extends Object>> attributes, List<List<Object>> linesValues) {
        return connector.connect(joinAspects(tableI(attributes, linesValues)));
    }

    @Override
    @Deprecated
    public Table table(String name, Discoverable parent, Attribute<? extends Object>... attributes) {
        final var database = joinAspects(tableI(name, parent, listWithValuesOf(attributes).mapped(a -> (Attribute<Object>) a)));
        return connector.connect(database);
    }

    @Override
    public Table table(String name, Discoverable parent, List<Attribute<? extends Object>> attributes) {
        final var database = joinAspects(tableI(name, parent, attributes.mapped(a -> (Attribute<Object>) a)));
        return connector.connect(database);
    }

    @Override
    public void close() {
        // Nothing needs to be done.
    }

    @Override
    public void flush() {
        // Nothing needs to be done.
    }

    @Override
    public AspectOrientedConstructor<Table> withAspect(Function<Table, Table> aspect) {
        return aspects.withAspect(aspect);
    }

    @Override
    public Table joinAspects(Table arg) {
        return aspects.joinAspects(arg);
    }

    @Override
    public ConnectingConstructor withConnector(Consumer<Table> connector) {
        this.connector.withConnector(connector);
        return this;
    }

    @Override
    public Table connect(Table subject) {
        return connector.connect(subject);
    }
}
