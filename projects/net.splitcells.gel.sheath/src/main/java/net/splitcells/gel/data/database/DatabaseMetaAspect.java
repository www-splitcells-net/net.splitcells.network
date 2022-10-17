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
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.w3c.dom.Node;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.resource.communication.interaction.LogLevel.DEBUG;
import static net.splitcells.gel.common.Language.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>This aspect adds mainly logging and runtime check functionality to {@link Database} instances.</p>
 * <p>TODO Make this an aspect in order to make it usable for other implementations of {@link Database}.<p/>
 * <p>TODO Require the usage of a non empty name during construction.<p/>
 * <p>TODO Invalidate Lines pointing to an index where values are already replaced.<p/>
 * <p>TODO PERFORMANCE Abstract Database implementation with generic storage in order to
 * simplify implementation and maintenance row and column based Databases.
 * <p/>
 * <p>TODO IDEA Implement Java collection interface.</p>
 */
public class DatabaseMetaAspect implements Database {

    public static Database databaseIRef(Database database) {
        return new DatabaseMetaAspect(database);
    }

    final Database database;

    private DatabaseMetaAspect(Database database) {
        this.database = database;
    }

    /**
     * TODO PERFORMANCE No copies have to be created, as it is guaranteed that a Line
     * does not change its content during its life cycle. This is important for
     * constraints.
     * <p/>
     * TODO Test whether the line is added to the correct place.
     * <p/>
     * TODO FIX Why does List<?> not work?
     * <p/>
     * TODO PERFORMANCE Reduce the high number of copies.
     *
     * @param line
     * @return
     */
    @Override
    public Line add(Line line) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assert database.headerView().size() == line.context().headerView().size() : path() + "" + line.context().path();
            assert !database.lines().contains(line);
            assert line.index() >= database.rawLines().size() || database.rawLines().get(line.index()) == null : path().toString() + line.index();
            range(0, database.headerView().size()).forEach(i -> {
                assert database.headerView().get(i).equals(line.context().headerView().get(i));
            });
        }
        return database.add(line);
    }

    @Override
    public void remove(int lineIndex) {

    }

    /**
     * TODO REMOVE Code duplication of {@link DatabaseMetaAspect#addTranslated} methods.
     */
    @Override
    public Line addTranslated(List<? extends Object> lineValues) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(lineValues.size()).isEqualTo(database.headerView().size());
            /**
             * TODO Check for {@link Attribute} compatibility and not Class compatibility.
             */
            lineValues.stream().forEach(e ->
                    assertThat(e).as("A line <%s> should not contain nulls.", lineValues)
                            .isNotNull());
            range(0, lineValues.size()).forEach(i -> database.headerView().get(i).isInstanceOf(lineValues.get(i)).required());
        }
        final var translatedAddition = database.addTranslated(lineValues);
        if (TRACING) {
            domsole().append(
                    event("addTranslating." + Database.class.getSimpleName()
                            , path().toString()
                            , elementWithChildren("index", textNode("" + translatedAddition.index()))
                            , elementWithChildren("line-values", textNode(lineValues.toString())))
                    , this, DEBUG
            );
        }
        return translatedAddition;
    }

    @Override
    public void remove(Line line) {
        if (TRACING) {
            domsole().append(event(REMOVE.value()
                                    + PATH_ACCESS_SYMBOL.value()
                                    + Database.class.getSimpleName()
                            , path().toString()
                            , elementWithChildren(LINE.value(), line.toDom()))
                    , this, LogLevel.DEBUG);
        }
        database.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        database.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        database.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        database.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public Node toDom() {
        return database.toDom();
    }

    @Override
    public List<String> path() {
        return database.path();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return database.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return database.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return database.columnView(attribute);
    }

    @Override
    public List<Column<Object>> columnsView() {
        return database.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return database.rawLinesView();
    }

    @Override
    public int size() {
        return database.size();
    }

    @Override
    public List<Line> rawLines() {
        return database.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        return database.lookupEquals(attribute, values);
    }

    @Override
    public Object identity() {
        return database.identity();
    }
}
