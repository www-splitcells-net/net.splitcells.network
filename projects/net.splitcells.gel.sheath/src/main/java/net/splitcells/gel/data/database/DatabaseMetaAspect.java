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
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.w3c.dom.Node;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.event;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.resource.communication.interaction.LogLevel.DEBUG;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.testing.Assertions.requireNull;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.common.Language.LINE;
import static net.splitcells.gel.common.Language.PATH_ACCESS_SYMBOL;
import static net.splitcells.gel.common.Language.REMOVE;

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
            database.headerView().requireSizeOf(line.context().headerView().size());
            database.lines().requireComplianceByEveryElementWith(e -> !line.equalsTo(e));
            describedBool(line.index() >= database.rawLines().size()
                            || database.rawLines().get(line.index()) == null
                    , () -> "path: " + path().toString() + ", line.index(): " + line.index())
                    .required();
            range(0, database.headerView().size()).forEach(i -> {
                requireEquals(database.headerView().get(i), line.context().headerView().get(i));
            });
        }
        return database.add(line);
    }

    @Override
    public void remove(int lineIndex) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            if (database.rawLinesView().size() <= lineIndex) {
                throw executionException(perspective("Cannot remove line by index, because the index is bigger than the biggest index in the database.")
                        .withText("lineIndex = " + lineIndex)
                        .withText("database = " + database.path())
                        .withText("database.size() = " + database.size()));
            }
            if (database.rawLinesView().get(lineIndex) == null) {
                throw executionException(perspective("Cannot remove line by index, because this line was already removed.")
                        .withText("lineIndex = " + lineIndex)
                        .withText("database = " + database.path()));
            }
        }
        database.remove(lineIndex);
    }

    /**
     * TODO REMOVE Code duplication of {@link DatabaseMetaAspect#addTranslated} methods.
     */
    @Override
    public Line addTranslated(List<? extends Object> lineValues) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            /**
             * TODO Check for {@link Attribute} compatibility and not Class compatibility.
             */
            lineValues.requireSizeOf(database.headerView().size());
            lineValues.stream().forEach(e ->
                    requireNotNull(e, "The line " + lineValues + " should not contain nulls."));
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

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Database) {
            final var castedArg = (Database) arg;
            return identity().equals(castedArg.identity());
        }
        throw executionException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return database.hashCode();
    }

    @Override
    public List<Line> orderedLines() {
        return database.orderedLines();
    }
}
