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
package net.splitcells.gel.data.view.attribute;

import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.Table;

import static java.util.stream.IntStream.range;

/**
 * <p>This is an alternative to {@link Attribute},
 * in order to access values of a {@link Line}.
 * The major difference is, that this class allows to access values faster than {@link Attribute}.
 * The downside of this class is,
 * that this alternative only works for the original {@link Table} of the {@link Line} and any other {@link Table},
 * where the {@link Attribute} of {@link IndexedAttribute} is at the same location at {@link Table#headerView()}
 * for the original and the other {@link Table}.</p>
 * <p>One could handle the indexes of the {@link Table#headerView()} manually, but it is more error prune.
 * Also, this class allows one to enable a runtime check, where the {@link IndexedAttribute} is checked,
 * in order to verify, that the {@link IndexedAttribute} is used for the correct {@link Table}.
 * Such a runtime check would be a lot harder, if the {@link Table#headerView()} indexes are handled manually.</p>
 *
 * @param <T> This is the type of the attribute value.
 *            A {@link Table#columnView(Attribute)} only holds values of this type.
 */
public class IndexedAttribute<T> {

    public static <A> IndexedAttribute<A> indexedAttribute(Attribute<A> attribute, Table context) {
        return new IndexedAttribute<A>(attribute, context);
    }

    private final Attribute<T> attribute;
    private final Table context;
    private final int headerIndex;

    private IndexedAttribute(Attribute<T> attribute, Table context) {
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

    public Table context() {
        return context;
    }

    public Attribute<T> attribute() {
        return attribute;
    }
}
