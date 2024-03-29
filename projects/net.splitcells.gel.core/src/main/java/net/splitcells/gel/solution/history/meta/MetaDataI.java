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
package net.splitcells.gel.solution.history.meta;

import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.Xml.toPrettyString;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.gel.common.Language.*;

import java.util.Optional;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.perspective.Perspective;
import org.w3c.dom.Node;

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
        return toPerspective().toXmlString();
    }

    @Override
    public Node toDom() {
        final var dom = Xml.elementWithChildren(META_DATA.value());
        data.forEach((key, value) -> {
            final var data = Xml.elementWithChildren(META_DATA.value());
            final var keyData = Xml.elementWithChildren(KEY.value());
            keyData.appendChild(textNode(key.getName()));
            final var valueData = Xml.elementWithChildren(VALUE.value());
            {
                if (value instanceof Domable) {
                    valueData.appendChild(((Domable) value).toDom());
                } else {
                    valueData.appendChild(textNode(value.toString()));
                }
            }
            dom.appendChild(keyData);
            dom.appendChild(valueData);
        });
        return dom;
    }

    @Override
    public Perspective toPerspective() {
        final var dom = perspective(META_DATA.value());
        data.forEach((key, value) -> {
            final var data = perspective(META_DATA.value());
            final var keyData = perspective(KEY.value());
            keyData.withChild(perspective(key.getName()));
            final var valueData = perspective(VALUE.value());
            {
                if (value instanceof Domable) {
                    valueData.withChild(((Domable) value).toPerspective());
                } else {
                    valueData.withChild(perspective(value.toString()));
                }
            }
            dom.withChild(keyData);
            dom.withChild(valueData);
        });
        return dom;
    }
}