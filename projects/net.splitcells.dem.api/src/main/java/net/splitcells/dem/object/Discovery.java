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

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.resource.Trail;

import java.util.Optional;

/**
 * <p>{@link Discovery} is used, in order to store and coordinate kind of long living objects in a tree structure.
 * The ID of any node is defined by the {@link #path()} from the root to the object,
 * where one path element corresponds to a node's {@link #name()}.</p>
 * <p>Generics are not used here,
 * because the types of a tree of {@link Discovery#value(Class)} are not intended to be uniform in any way.</p>
 */
public interface Discovery {
    /**
     * TODO Prevent modification by creating and returning a read-only version of {@link Map}.
     *
     * @return A {@link Map} from the child's name to the child itself.
     * The caller is not allowed to modify this.
     */
    Map<String, Discovery> children();

    default Discovery childByPath(String... path) {
        return childByPath(0, path);
    }

    default Discovery childByPath(int index, String... path) {
        if (index == path.length - 1) {
            return children().get(path[index]);
        }
        return children().get(path[index]).childByPath(index + 1, path);
    }

    /**
     * @return This is the name of the node, which is the last element of the {@link #path()}.
     */
    default String name() {
        final var content = path().content();
        if (content.isEmpty()) {
            return "";
        }
        return content.get(content.size() - 1);
    }

    /**
     * @return Basically a unique ID, that should be unique inside an {@link Dem#process(Runnable)},
     * for a given time.
     */
    Trail path();

    /**
     * This method fails, if there is already a {@link Discovery} in the tree with the same {@link #path()}.
     *
     * @param instance
     * @param relativePath
     * @return This is the new {@link Discovery} created for the given instance.
     * The {@link #path()} of the new instance is the concatenation of this {@link #path()} and the given {@code relativePath}.
     */
    Discovery createChild(Object instance, String... relativePath);

    /**
     * This is a helper function, that works like {@link #createChild(Object, String...)},
     * with the difference, that all elements of {@code relativePath},
     * that have a smaller index as {@code relativePathIndex} are ignored.
     *
     * @param instance
     * @param relativePathIndex
     * @param relativePath
     * @return
     */
    Discovery createChild(Object instance, int relativePathIndex, String... relativePath);

    /**
     * @param child Removes this {@code child} from {@link #children()} and thereby marks the {@link #value(Class)}
     *              of {@code child} as disposable.
     */
    void removeChild(Discovery child);

    /**
     * @param clazz
     * @param <R>
     * @return This returns the kind of long living object, that is linked to this {@link Discovery}.
     */
    <R> Optional<R> value(Class<? extends R> clazz);
}
