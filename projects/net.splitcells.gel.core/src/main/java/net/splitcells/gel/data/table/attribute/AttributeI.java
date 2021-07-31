/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.data.table.attribute;

import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.data.atom.Bools.bool;

import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.common.Language;
import org.w3c.dom.Element;

import net.splitcells.dem.data.atom.Bool;

import java.util.function.Function;

public final class AttributeI<T> implements Attribute<T> {

    private final Class<T> type;
    private final String name;
    private final Function<String, T> deserializer;

    public static <T> Attribute<T> attribute(Class<T> type) {
        return new AttributeI<>(type, type.getSimpleName());
    }

    public static Attribute<Integer> integerAttribute(String name) {
        return new AttributeI<>(Integer.class, name, arg -> Integer.valueOf(arg));
    }

    public static Attribute<String> stringAttribute(String name) {
        return new AttributeI<>(String.class, name, arg -> arg);
    }

    public static <T> Attribute<T> attribute(Class<T> type, String name) {
        return new AttributeI<>(type, name);
    }

    private AttributeI(Class<T> type, String name) {
        this(type, name, arg -> {
            throw new UnsupportedOperationException();
        });
    }

    private AttributeI(Class<T> type, String name, Function<String, T> deserializer) {
        this.type = type;
        this.name = name;
        this.deserializer = deserializer;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object arg) {
        return super.equals(arg);
    }

    @Override
    public Bool isInstanceOf(Object arg) {
        return bool(type.isAssignableFrom(arg.getClass()));
    }

    @Override
    public Element toDom() {
        return Xml.elementWithChildren(Attribute.class.getSimpleName()
                , Xml.elementWithChildren(Language.NAME.value(), textNode(name))
                , Xml.elementWithChildren(type.getSimpleName())
        );
    }

    @Override
    public T deserializeValue(String value) {
        return deserializer.apply(value);
    }
}
