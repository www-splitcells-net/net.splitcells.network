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
package net.splitcells.gel.data.view;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.lang.namespace.NameSpaces.HTML;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.common.Language.CONTENT;
import static net.splitcells.gel.common.Language.INDEX;
import static net.splitcells.gel.common.Language.TYPE;
import static net.splitcells.gel.common.Language.VALUE;
import static net.splitcells.gel.data.view.LinePointerI.linePointer;

import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.TreeI;
import net.splitcells.dem.object.Convertible;
import net.splitcells.dem.object.Merger;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.attribute.IndexedAttribute;

/**
 * TODO {@link Line}s and {@link View}s should be typed. Use a meta {@link Attribute}, which
 * supports multiple types and just use one attribute type per data base.
 */
public interface Line extends Domable, Convertible {

    static List<?> concat(Line... lines) {
        final List<Object> concatenation = list();
        for (var line : lines) {
            final var columns = line.context().columnsView();
            range(0, columns.size()).forEach(i -> concatenation.add(columns.get(i).get(line.index())));
        }
        return concatenation;
    }

    /**
     * <p>Retrieves a value associated with this {@link Line} and {@link Attribute}.
     * In order for this to work,
     * {@link #context()} needs to contain this {@link Attribute} in its {@link View#headerView()}.</p>
     * <p>If possible, {@link #value(IndexedAttribute)} should be used instead,
     * in order to get a better runtime performance.</p>
     *
     * @param attribute Identifies, which value of a {@link Line} should be retrieved.
     * @param <T>       This is the type of the value and the type parameter of the attribute.
     * @return This is the value of the attribute.
     */
    <T> T value(Attribute<T> attribute);

    /**
     * <p>Retrieves a value associated with this {@link Line} and {@link Attribute}.
     * In order for this to work,
     * {@link #context()} needs to contain this {@link IndexedAttribute#attribute()} in its {@link View#headerView()}
     * at the same position as {@link IndexedAttribute#headerIndex()}.</p>
     *
     * @param attribute Identifies, which value of a {@link Line} should be retrieved.
     * @param <T>       This is the type of the value and the type parameter of the attribute.
     * @return This is the value of the attribute.
     */
    <T> T value(IndexedAttribute<T> attribute);

    /**
     * Minimize usage of index as it is prone to errors.
     * Access by identity is more secure, than access by integer,
     * because one can do calculations with indexes,
     * which is prone to errors.
     * See references vs pointers in programming languages.
     *
     * @return The index of the {@link Line}in {@link Line#context}.
     */
    int index();

    default LinePointer toLinePointer() {
        return linePointer(context(), index());
    }

    View context();

    default boolean equalsTo(Line other) {
        return index() == other.index() && context().equals(other.context());
    }

    default boolean isValid() {
        return null != context().rawLinesView().get(index());
    }

    default List<String> toStringList() {
        return listWithValuesOf
                (context().headerView().stream()
                        .map(attribute -> value(attribute).toString())
                        .collect(toList()));
    }

    default boolean equalContents(Line other) {
        return values().equals(other.values());
    }

    default ListView<Object> values() {
        return context()
                .headerView()
                .stream()
                .map(attribute -> value(attribute))
                .collect(toList());
    }

    default Tree toHtmlPerspective() {
        final var perspective = TreeI.tree(Line.class.getSimpleName(), HTML);
        perspective.withProperty("div", HTML, "" + index());
        context().headerView().forEach(attribute -> {
            final var value = context().columnView(attribute).get(index());
            final Tree domValue;
            if (value == null) {
                domValue = tree("");
            } else {
                if (value instanceof Domable dom) {
                    domValue = dom.toTree();
                } else {
                    domValue = TreeI.tree(value.toString(), STRING);
                }
            }
            final var valuePerspective = TreeI.tree(VALUE.value(), HTML);
            valuePerspective.withProperty(TYPE.value(), HTML, attribute.name());
            valuePerspective.withChild(TreeI.tree(CONTENT.value(), HTML).withChild(domValue));
            perspective.withChild(valuePerspective);
        });
        return perspective;
    }

    @Override
    default Tree toTree() {
        final var root = tree(Line.class.getSimpleName());
        root.withProperty(INDEX.value(), "" + index());
        context().headerView().forEach(attribute -> {
            final var attributeValue = context().columnView(attribute).get(index());
            final Tree attributeRender;
            if (attributeValue == null) {
                attributeRender = tree("");
            } else {
                if (attributeValue instanceof Domable dom) {
                    attributeRender = dom.toTree();
                } else {
                    attributeRender = tree(attributeValue.toString());
                }
            }
            root.withProperty(TYPE.value(), attribute.name());
            root.withProperty(VALUE.value(), attributeRender);
        });
        return root;
    }

    default Merger merge(Merger merger) {
        merger.requireIsRecording();
        context().headerView().forEach(h -> {
            merger.merge(h.name(), "" + value(h));
        });
        return merger;
    }
}
