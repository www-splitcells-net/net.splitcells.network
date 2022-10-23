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
import static net.splitcells.dem.utils.ExecutionException.executionException;

import org.w3c.dom.Element;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.atom.Bool;

public class MetaAttribute<T> implements Attribute<Class<T>> {

    private final Class<T> type;
    private final String name;

    public MetaAttribute(Class<T> type, String name) {
        this.type = type;
        this.name = name;
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
        return bool(type.isAssignableFrom((Class<?>) arg));
    }

    @Override
    public void assertArgumentCompatibility(Object arg) {
        if (isInstanceOf(arg).isFalse()) {
            throw executionException("Given object not compatible to attribute: name=" + name
                    + ", type=" + type
                    + ", givenType=" + arg.getClass()
                    + ", arg=" + arg);
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