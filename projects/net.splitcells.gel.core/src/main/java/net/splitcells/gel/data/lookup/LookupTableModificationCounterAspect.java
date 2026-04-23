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
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.data.set.SetT;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class LookupTableModificationCounterAspect implements LookupTable {
    public static LookupTable lookupViewModificationCounterAspect(LookupTable lookupTable) {
        return new LookupTableModificationCounterAspect(lookupTable);
    }

    private final LookupTable lookupTable;

    private LookupTableModificationCounterAspect(LookupTable argLookupTable) {
        lookupTable = argLookupTable;
    }

    @Override
    public void register(Line line) {
        configValue(LookupModificationCounter.class).count(this, 1 + (long) line.values().size());
        lookupTable.register(line);
    }

    @Override
    public void removeRegistration(Line line) {
        configValue(LookupModificationCounter.class).count(this, 1 + (long) line.values().size());
        lookupTable.removeRegistration(line);
    }

    @Override
    public View base() {
        return lookupTable.base();
    }

    @Override
    public SetT<Integer> contentIndexes() {
        return lookupTable.contentIndexes();
    }

    @Override
    public String name() {
        return lookupTable.name();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return lookupTable.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return lookupTable.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return lookupTable.columnView(attribute);
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return lookupTable.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return lookupTable.rawLinesView();
    }

    @Override
    public int size() {
        return lookupTable.size();
    }

    @Override
    public List<Line> rawLines() {
        return lookupTable.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        return lookupTable.lookupEquals(attribute, values);
    }

    @Override
    public DiscoverableRenderer discoverableRenderer() {
        return lookupTable.discoverableRenderer();
    }

    @Override
    public Object identity() {
        return lookupTable.identity();
    }

    @Override
    public Tree toTree() {
        return lookupTable.toTree();
    }

    @Override
    public ListView<String> path() {
        return lookupTable.path();
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof LookupTable view) {
            return view.identity() == lookupTable.identity();
        }
        throw ExecutionException.execException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return lookupTable.hashCode();
    }
}
