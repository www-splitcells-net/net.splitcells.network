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
package net.splitcells.gel.data.table.attribute;

import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.atom.Bools.untrue;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.common.Language;
import org.w3c.dom.Element;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.atom.Bool;

public class ListAttribute<T> implements Attribute<List<T>> {

    private final Class<T> type;
    private final String name;

    public static <T> ListAttribute<T> listAttribute(Class<T> type, String name) {
        return new ListAttribute<>(type, name);
    }

    protected ListAttribute(Class<T> type, String name) {
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
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Bool isInstanceOf(Object arg) {
        if (arg instanceof List<?>) {
            return bool(
                    ((List<?>) arg).stream()
                            .filter(i -> i != null)
                            .allMatch(i -> type.isAssignableFrom(i.getClass())));
        } else {
            return untrue();
        }
    }

    @Override
    public Class<?> type() {
        return List.class;
    }

    @Override
    public void assertArgumentCompatibility(Object arg) {
        if (isInstanceOf(arg).isFalse()) {
            throw executionException("Given object not compatible to list attribute: name=" + name
                    + ", type=" + type
                    + ", givenType=" + arg.getClass()
                    + ", arg=" + arg);
        }
    }

    @Override
    public Perspective toPerspective() {
        return perspective(name)
                .withProperty(Language.NAME.value(), getClass().getSimpleName())
                .withProperty(Language.TYPE.value(), type.getSimpleName());
    }
}
