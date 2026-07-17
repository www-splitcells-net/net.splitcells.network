/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history.meta;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.common.Language.*;

import java.util.Optional;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;

public class MetaDataI implements MetaDataView, MetaDataWriter {
    public static MetaDataI metaData() {
        return new MetaDataI();
    }

    private final Map<Class<?>, Object> data = map();

    private MetaDataI() {
    }

    @Override
    public <A> MetaDataWriter with(Class<A> type, A value) {
        if (data.containsKey(type)) {
            throw new IllegalArgumentException(type.getName());
        }
        data.put(type, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> value(Class<T> tips) {
        return (Optional<T>) Optional.ofNullable(data.get(tips));
    }

    @Override
    public String toString() {
        return toTree().toXmlString();
    }

    @Override
    public Tree toTree() {
        final var dom = tree(META_DATA.value());
        data.forEach((key, value) -> {
            final var keyData = tree(KEY.value());
            keyData.withChild(tree(key.getName()));
            final var valueData = tree(VALUE.value());
            {
                if (value instanceof Domable domable) {
                    valueData.withChild(domable.toTree());
                } else {
                    valueData.withChild(tree(value.toString()));
                }
            }
            dom.withChild(keyData);
            dom.withChild(valueData);
        });
        return dom;
    }
}