/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view;

import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.attribute.IndexedAttribute;

import static java.util.stream.IntStream.range;

public class LineWithValues implements Line {

    public static Line lineWithValues(View context, ListView<?> values, int index) {
        return new LineWithValues(context, values, index);
    }

    private final View context;
    private final ListView<Attribute<Object>> header;
    private final ListView<?> values;
    private final int index;

    private LineWithValues(View context, ListView<?> values, int index) {
        this.context = context;
        this.values = values;
        this.index = index;
        header = context.headerView();
    }

    @Override
    public <T> T value(Attribute<T> attribute) {
        final var attributeIndex = range(0, header.size())
                .filter(h -> header.get(h).equals(attribute))
                .findFirst()
                .orElseThrow();
        return (T) values.get(attributeIndex);
    }

    @Override
    public <T> T value(IndexedAttribute<T> attribute) {
        return (T) values.get(attribute.headerIndex());
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public View context() {
        return context;
    }

    @Override
    public ListView<Object> values() {
        return (ListView<Object>) values;
    }
}
