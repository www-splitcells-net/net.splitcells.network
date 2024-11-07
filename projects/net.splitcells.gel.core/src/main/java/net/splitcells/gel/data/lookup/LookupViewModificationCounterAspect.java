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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.table.TableModificationCounter;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class LookupViewModificationCounterAspect implements LookupView {
    public static LookupView lookupViewModificationCounterAspect(LookupView lookupView) {
        return new LookupViewModificationCounterAspect(lookupView);
    }
    private final LookupView lookupView;
    private LookupViewModificationCounterAspect(LookupView argLookupView) {
        lookupView = argLookupView;
    }
    @Override
    public void register(Line line) {
        configValue(LookupModificationCounter.class).count(this, 1 + (long) line.values().size());
        lookupView.register(line);
    }

    @Override
    public void removeRegistration(Line line) {
        configValue(LookupModificationCounter.class).count(this, 1 + (long) line.values().size());
        lookupView.removeRegistration(line);
    }

    @Override
    public View base() {
        return lookupView.base();
    }

    @Override
    public String name() {
        return lookupView.name();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return lookupView.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return lookupView.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return lookupView.columnView(attribute);
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return lookupView.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return lookupView.rawLinesView();
    }

    @Override
    public int size() {
        return lookupView.size();
    }

    @Override
    public List<Line> rawLines() {
        return lookupView.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        return lookupView.lookupEquals(attribute, values);
    }

    @Override
    public Object identity() {
        return lookupView.identity();
    }

    @Override
    public Tree toTree() {
        return lookupView.toTree();
    }

    @Override
    public List<String> path() {
        return lookupView.path();
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof LookupView) {
            final var castedArg = (LookupView) arg;
            return castedArg.identity() == lookupView.identity();
        }
        throw executionException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return lookupView.hashCode();
    }
}
