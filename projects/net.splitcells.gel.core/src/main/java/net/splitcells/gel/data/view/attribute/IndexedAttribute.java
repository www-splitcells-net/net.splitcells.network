/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view.attribute;

import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;

import static java.util.stream.IntStream.range;

/**
 * <p>This is an alternative to {@link Attribute},
 * in order to access values of a {@link Line}.
 * The major difference is, that this class allows to access values faster than {@link Attribute}.
 * The downside of this class is,
 * that this alternative only works for the original {@link View} of the {@link Line} and any other {@link View},
 * where the {@link Attribute} of {@link IndexedAttribute} is at the same location at {@link View#headerView()}
 * for the original and the other {@link View}.</p>
 * <p>One could handle the indexes of the {@link View#headerView()} manually, but it is more error prune.
 * Also, this class allows one to enable a runtime check, where the {@link IndexedAttribute} is checked,
 * in order to verify, that the {@link IndexedAttribute} is used for the correct {@link View}.
 * Such a runtime check would be a lot harder, if the {@link View#headerView()} indexes are handled manually.</p>
 *
 * @param <T> This is the type of the attribute value.
 *            A {@link View#columnView(Attribute)} only holds values of this type.
 */
public class IndexedAttribute<T> {

    public static <A> IndexedAttribute<A> indexedAttribute(Attribute<A> attribute, View context) {
        return new IndexedAttribute<>(attribute, context);
    }

    private final Attribute<T> attribute;
    private final View context;
    private final int headerIndex;

    private IndexedAttribute(Attribute<T> attribute, View context) {
        this.attribute = attribute;
        this.context = context;
        final var headerView = context.headerView();
        headerIndex = range(0, headerView.size())
                .filter(i -> headerView.get(i).equals(attribute))
                .findFirst()
                .orElseThrow();
    }

    public int headerIndex() {
        return headerIndex;
    }

    public View context() {
        return context;
    }

    public Attribute<T> attribute() {
        return attribute;
    }
}
