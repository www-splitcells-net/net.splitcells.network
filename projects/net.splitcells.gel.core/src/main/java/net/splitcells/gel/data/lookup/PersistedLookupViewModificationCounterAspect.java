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
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class PersistedLookupViewModificationCounterAspect implements PersistedLookupView {
    public static PersistedLookupView lookupViewModificationCounterAspect(PersistedLookupView persistedLookupView) {
        return new PersistedLookupViewModificationCounterAspect(persistedLookupView);
    }
    private final PersistedLookupView persistedLookupView;
    private PersistedLookupViewModificationCounterAspect(PersistedLookupView argPersistedLookupView) {
        persistedLookupView = argPersistedLookupView;
    }
    @Override
    public void register(Line line) {
        configValue(LookupModificationCounter.class).count(this, 1 + (long) line.values().size());
        persistedLookupView.register(line);
    }

    @Override
    public void removeRegistration(Line line) {
        configValue(LookupModificationCounter.class).count(this, 1 + (long) line.values().size());
        persistedLookupView.removeRegistration(line);
    }

    @Override
    public View base() {
        return persistedLookupView.base();
    }

    @Override
    public String name() {
        return persistedLookupView.name();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return persistedLookupView.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return persistedLookupView.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return persistedLookupView.columnView(attribute);
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return persistedLookupView.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return persistedLookupView.rawLinesView();
    }

    @Override
    public int size() {
        return persistedLookupView.size();
    }

    @Override
    public List<Line> rawLines() {
        return persistedLookupView.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        return persistedLookupView.lookupEquals(attribute, values);
    }

    @Override
    public DiscoverableRenderer discoverableRenderer() {
        return persistedLookupView.discoverableRenderer();
    }

    @Override
    public Object identity() {
        return persistedLookupView.identity();
    }

    @Override
    public Tree toTree() {
        return persistedLookupView.toTree();
    }

    @Override
    public List<String> path() {
        return persistedLookupView.path();
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof PersistedLookupView) {
            final var castedArg = (PersistedLookupView) arg;
            return castedArg.identity() == persistedLookupView.identity();
        }
        throw executionException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return persistedLookupView.hashCode();
    }
}
