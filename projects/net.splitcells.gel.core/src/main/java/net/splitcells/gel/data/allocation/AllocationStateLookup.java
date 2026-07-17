/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.allocation;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.table.AfterAdditionSubscriber;
import net.splitcells.gel.data.table.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

import java.util.function.Predicate;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class AllocationStateLookup implements Table {

    public static Table allocationStateLookup(View view, Predicate<Line> selector) {
        return new AllocationStateLookup(view, selector);
    }

    public static Table allocationStateLookup(View view, Predicate<Line> selector, List<Attribute<?>> relevantAttributes) {
        return new AllocationStateLookup(view, selector);
    }

    private AllocationStateLookup(View view, Predicate<Line> selector) {

    }

    @Override
    public Object identity() {
        throw notImplementedYet();
    }

    @Override
    public List<String> path() {
        throw notImplementedYet();
    }

    @Override
    public Line addTranslated(ListView<Object> lineValues, int index) {
        throw notImplementedYet();
    }

    @Override
    public Line addTranslated(ListView<?> values) {
        throw notImplementedYet();
    }

    @Override
    public Line add(Line line) {
        throw notImplementedYet();
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
    public String name() {
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
        throw notImplementedYet();
    }

    @Override
    public int size() {
        throw notImplementedYet();
    }

    @Override
    public List<Line> rawLines() {
        throw notImplementedYet();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        throw notImplementedYet();
    }

    @Override
    public DiscoverableRenderer discoverableRenderer() {
        throw notImplementedYet();
    }
}
