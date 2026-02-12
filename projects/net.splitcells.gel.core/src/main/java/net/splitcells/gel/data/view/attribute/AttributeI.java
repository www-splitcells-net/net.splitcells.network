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

import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.order.Ordering.LESSER_THAN;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

import net.splitcells.dem.data.order.Comparison;
import net.splitcells.dem.data.order.Ordering;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.common.Language;

import net.splitcells.dem.data.atom.Bool;

import java.util.function.Function;

public final class AttributeI<T> implements Attribute<T> {

    public static final Comparison<Attribute<? extends Object>> CONTENT_COMPARISON = new Comparison<Attribute<? extends Object>>() {
        @Override
        public Ordering compareTo(Attribute<?> a, Attribute<?> b) {
            if (a.name().equals(b.name()) && a.type().equals(b.type())) {
                return EQUAL;
            }
            return LESSER_THAN;
        }
    };

    private final Class<?> type;
    private final String name;
    private final Function<String, T> deserializer;

    public static <T> Attribute<T> attribute(Class<T> type) {
        return new AttributeI<>(type, type.getSimpleName());
    }

    public static Attribute<Integer> integerAttribute(String name) {
        return new AttributeI<>(Integer.class, name, arg -> Integer.valueOf(arg));
    }

    public static Attribute<Float> floatAttribute(String name) {
        return new AttributeI<>(Float.class, name, arg -> Float.valueOf(arg));
    }

    public static Attribute<String> stringAttribute(String name) {
        return new AttributeI<>(String.class, name, arg -> arg);
    }

    public static <T> Attribute<T> attribute(Class<T> type, String name) {
        return new AttributeI<>(type, name);
    }

    public static Attribute<Object> attributeObject(Class<?> type, String name) {
        return new AttributeI<>(type, name);
    }

    private AttributeI(Class<?> type, String name) {
        this(type, name, arg -> {
            throw new UnsupportedOperationException();
        });
    }

    private AttributeI(Class<?> type, String name, Function<String, T> deserializer) {
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
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Bool isInstanceOf(Object arg) {
        return bool(type.isAssignableFrom(arg.getClass()));
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public void assertArgumentCompatibility(Object arg) {
        if (arg != null && !type.isAssignableFrom(arg.getClass())) {
            throw ExecutionException.execException("Given object not compatible to attribute: name=" + name
                    + ", type=" + type
                    + ", givenType=" + arg.getClass()
                    + ", arg=" + arg);
        }
    }

    @Override
    public Tree toTree() {
        return tree(Attribute.class.getSimpleName())
                .withProperty("Name", name)
                .withProperty("Type", type.getSimpleName());
    }

    @Override
    public T deserializeValue(String value) {
        return deserializer.apply(value);
    }

    @Override
    public String toString() {
        return "attribute: name = " + name + ", type = " + type.getName();
    }
}
