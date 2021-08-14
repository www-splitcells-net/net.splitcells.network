/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.data.table.attribute;

import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.atom.Bools.untrue;

import java.util.Map;

import org.w3c.dom.Element;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.atom.Bool;

public class MapAttribute<T> implements Attribute<Map<Class<T>, T>> {

    private final Class<?> type;
    private final String name;

    @Deprecated
    public MapAttribute(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Bool isInstanceOf(Object arg) {
        if (arg instanceof Map) {
            return bool(((Map<Class<?>, ?>) arg).entrySet().stream()
                    .filter(i -> i != null)
                    .allMatch(i -> type.isAssignableFrom(i.getValue().getClass())
                            && type.isAssignableFrom(i.getKey())
                            && i.getKey().equals(i.getValue().getClass())
                    ));
        } else {
            return untrue();
        }
    }

    @Override
    public Element toDom() {
        return Xml.elementWithChildren(name
                , Xml.elementWithChildren(getClass().getSimpleName())
                , Xml.elementWithChildren(type.getSimpleName())
        );
    }
}
