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
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.ColumnView;

import java.util.Collection;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.resource.communication.interaction.LogLevel.DEBUG;
import static net.splitcells.gel.common.Language.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO Make this an aspect in order to make it usable for other implementations of {@link Database}.
 * <p/>
 * TODO Require the usage of a non empty name during construction.
 * <p/>
 * TODO Invalidate Lines pointing to an index where values are already replaced.
 * <p/>
 * TODO PERFORMANCE Abstract Database implementation with generic storage in order to
 * simplify implementation and maintenance row and column based Databases.
 * <p/>
 * TODO Test consistency of meta data.
 * <p/>
 * TODO IDEA Implement Java collection interface.
 */
public class DatabaseIRef extends DatabaseI {
    @Deprecated
    protected DatabaseIRef(List<Attribute<? extends Object>> attribute) {
        super(attribute);
    }

    protected DatabaseIRef(String name, Discoverable parent, List<Attribute<Object>> header) {
        super(name, parent, header);
        assert this.attributes.size() == columns.size();
        header.requireUniqueness();
    }

    @Deprecated
    protected DatabaseIRef(List<Attribute<?>> header, Collection<List<Object>> linesValues) {
        super(header, linesValues);
    }

    protected DatabaseIRef(String name, Discoverable parent, Attribute<? extends Object>... header) {
        super(name, parent, header);
    }

    @Deprecated
    protected DatabaseIRef(Attribute<?>... header) {
        super(header);
    }

    /**
     * TODO PERFORMANCE Cache list views in Order to minimize number of objects.
     * <p/>
     * TODO Return an unmodifiable view of the column.
     *
     * @param attribute
     * @param <T>
     * @return
     */
    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(attributes.contains(attribute))
                    .describedAs(attributes.stream()
                            .map(a -> a.name() + ", ")
                            .reduce((a, b) -> a + b)
                            .orElse("")
                            + ": " + attribute.name())
                    .isTrue();
            assertThat(typed_column_index.containsKey(attribute))
                    .describedAs(attribute.name() + " is not present in "
                            + typed_column_index.keySet().stream()
                            .map(a -> a.name())
                            .reduce((a, b) -> a + ", " + b)
                            .get()
                    )
                    .isTrue();
        }
        return super.columnView(attribute);
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
            assert attributes.size() == line.context().headerView().size() : path() + "" + line.context().path();
            assert !lines.contains(line);
            assert line.index() >= rawLines.size() || rawLines.get(line.index()) == null : path().toString() + line.index();
        }
        range(0, attributes.size()).forEach(i -> {
            assert attributes.get(i).equals(line.context().headerView().get(i));
        });
        return super.add(line);
    }

    /**
     * @param lineValues TODO Support {@link net.splitcells.dem.lang.dom.Domable#toDom} for logging.
     */
    protected Line addTranslated(List<Object> lineValues, int index) {
        if (TRACING) {
            domsole().append(
                    event("addTranslatingAt." + Database.class.getSimpleName()
                            , path().toString()
                            , elementWithChildren("index", textNode("" + index))
                            , elementWithChildren("line-values", textNode(lineValues.toString()))
                    )
                    , this
                    , DEBUG
            );
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(lineValues.size()).isEqualTo(attributes.size());
            require(indexesOfFree.contains(index) || index >= rawLines.size());
            range(0, lineValues.size()).forEach(i -> attributes.get(i).isInstanceOf(lineValues.get(i)).required());
        }
        return super.addTranslated(lineValues, index);
    }

    /**
     * TODO REMOVE Code duplication of {@link DatabaseIRef#addTranslated} methods.
     */
    @Override
    public Line addTranslated(List<? extends Object> lineValues) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(lineValues.size()).isEqualTo(attributes.size());
            /**
             * TODO Check for {@link Attribute} compatibility and not Class compatibility.
             */
            lineValues.stream().forEach(e ->
                    assertThat(e).as("A line <%s> should not contain nulls.", lineValues)
                            .isNotNull());
            range(0, lineValues.size()).forEach(i -> attributes.get(i).isInstanceOf(lineValues.get(i)).required());
        }
        final var translatedAddition = super.addTranslated(lineValues);
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
    public void remove(int lineIndex) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(indexesOfFree).doesNotContain(lineIndex);
            assertThat(lineIndex).isNotNegative();
            assert lineIndex < rawLines.size() : lineIndex + ":" + rawLines.size() + path();
            assertThat(rawLines.get(lineIndex)).isNotNull();
            lines.hasOnlyOnce(rawLines.get(lineIndex));
            columns.forEach(column -> {
                assert lineIndex < column.size();
                assert rawLines.size() == column.size();
            });
            assert lineIndex < rawLines.size();
        }
        super.remove(lineIndex);
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
        super.remove(line);
    }
}
