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

import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.atom.Bools.untrue;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;


import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.common.Language;

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
    public Class<?> type() {
        return Map.class;
    }

    @Override
    public void assertArgumentCompatibility(Object arg) {
        if (arg != null && isInstanceOf(arg).isFalse()) {
            throw ExecutionException.execException("Given object not compatible to attribute: name=" + name
                    + ", type=" + type
                    + ", givenType=" + arg.getClass()
                    + ", arg=" + arg);
        }
    }

    @Override
    public Tree toTree() {
        return tree(name)
                .withProperty(Language.NAME.value(), getClass().getSimpleName())
                .withProperty(Language.TYPE.value(), type.getSimpleName());
    }
}
