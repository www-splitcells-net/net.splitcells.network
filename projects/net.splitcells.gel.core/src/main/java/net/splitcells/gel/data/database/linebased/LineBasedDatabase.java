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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.ColumnView;

import java.util.Optional;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.LineWithValues.lineWithValues;

public class LineBasedDatabase implements Database {

    private final String name;
    private final Optional<Discoverable> parent;
    private final List<Attribute<Object>> attributes;
    private final List<Attribute<? extends Object>> attributes2;
    private final List<Line> rawLines = list();
    private final Set<Line> lines = setOfUniques();

    private final List<AfterAdditionSubscriber> additionSubscriber = list();
    private final List<BeforeRemovalSubscriber> beforeRemovalSubscriber = list();
    private final List<BeforeRemovalSubscriber> afterRemovalSubscriber = list();
    private final Set<Integer> indexesOfFree = setOfUniques();

    public static Database lineBasedDatabase(String name, Optional<Discoverable> parent, List<Attribute<Object>> attributes) {
        return new LineBasedDatabase(name, parent, attributes);
    }

    private LineBasedDatabase(String name, Optional<Discoverable> parent, List<Attribute<Object>> attributes) {
        this.name = name;
        this.parent = parent;
        this.attributes = attributes;
        this.attributes2 = list();
        attributes2.addAll(attributes);
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public List<String> path() {
        return parent.map(Discoverable::path).orElse(list()).withAppended(name);
    }

    @Override
    public Line addTranslated(ListView<? extends Object> values) {
        final Line newLine;
        if (indexesOfFree.isEmpty()) {
            final int lineIndex = indexesOfFree.removeAny();
            newLine = lineWithValues(this, values, lineIndex);
            rawLines.set(lineIndex, newLine);
        } else {
            newLine = lineWithValues(this, values, rawLines.size());
            rawLines.add(newLine);
        }
        lines.add(newLine);
        additionSubscriber.forEach(subscriber -> subscriber.registerAddition(newLine));
        return newLine;
    }

    @Override
    public Line add(Line argLine) {
        final var newLine = lineWithValues(this, argLine.values(), argLine.index());
        final var rawLinesSize = rawLines.size();
        if (rawLinesSize > argLine.index()) {
            indexesOfFree.remove(argLine.index());
            rawLines.set(newLine.index(), newLine);
        } else if (rawLinesSize == argLine.index()) {
            rawLines.add(newLine);
        } else {
            range(rawLinesSize, argLine.index()).forEach(i -> {
                indexesOfFree.add(i);
                rawLines.add(null);
            });
            rawLines.add(newLine);
        }
        lines.add(newLine);
        additionSubscriber.forEach(subscriber -> subscriber.registerAddition(newLine));
        return newLine;
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        throw notImplementedYet();
    }

    @Override
    public void remove(int lineIndex) {
        throw notImplementedYet();
    }

    @Override
    public void remove(Line line) {
        throw notImplementedYet();
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        throw notImplementedYet();
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        throw notImplementedYet();
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        throw notImplementedYet();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        throw notImplementedYet();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        throw notImplementedYet();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        throw notImplementedYet();
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        throw notImplementedYet();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return rawLines;
    }

    @Override
    public int size() {
        return lines.size();
    }

    @Override
    public List<Line> rawLines() {
        return rawLines;
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        throw notImplementedYet();
    }
}
