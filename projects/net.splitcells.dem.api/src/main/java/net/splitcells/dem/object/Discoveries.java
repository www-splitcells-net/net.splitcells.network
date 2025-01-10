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
package net.splitcells.dem.object;

import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.resource.Trail;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class Discoveries<T> implements Discovery<T> {

    public static <R> Discovery<R> discoveryRoot() {
        return new Discoveries<>();
    }

    private final Map<String, Discovery<?>> children = map();
    private final Trail path;
    private final Optional<T> value;

    private Discoveries() {
        path = trail("");
        value = Optional.empty();
    }

    private Discoveries(Trail argTrail, Optional<T> argValue) {
        path = argTrail;
        value = argValue;
    }

    @Override
    public Map<String, Discovery<?>> children() {
        return children;
    }

    @Override
    public Trail path() {
        return path;
    }

    @Override
    public <R> Discovery<R> createChild(Class<R> clazz, R instance, String... relativePath) {
        return createChild(clazz, instance, 0, relativePath);
    }

    @Override
    public <R> Discovery<R> createChild(Class<R> clazz, R instance, int relativePathIndex, String... relativePath) {
        final String next = relativePath[relativePathIndex];
        if (relativePath.length - relativePathIndex == 1) {
            if (children.containsKey(next)) {
                throw executionException(tree("Cannot add child with already existing path.")
                        .withProperty("New child", instance.toString())
                        .withProperty("Existing child", children.get(next).toString())
                        .withProperty("Parent value", value.map(Object::toString).orElse("empty"))
                        .withProperty("Parent path", path.unixPathString()));
            }
            final var child = new Discoveries<>(trail(path.content().shallowCopy().withAppended(next))
                    , Optional.of(instance));
            children.put(next, child);
            return child;
        } else {
            if (children.containsKey(next)) {
                return ((Discovery<R>) children.get(next)).createChild(clazz, instance, ++relativePathIndex, relativePath);
            }
            final var child = new Discoveries<>(trail(path.content().shallowCopy().withAppended(next))
                    , Optional.empty());
            return child.createChild(clazz, instance, ++relativePathIndex, relativePath);
        }
    }


    @Override
    public <R> void removeChild(Discovery<R> child) {
        final var matches = children.flowMappingsByValue(child).toList();
        matches.forEach(m -> children.remove(m.getKey()));
    }

    @Override
    public <R> Optional<R> value(Class<? extends R> clazz) {
        return (Optional<R>) value;
    }
}
