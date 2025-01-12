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
 * <p>Generics are not used here,
 * because the types of a tree of {@link Discovery#value(Class)} are not intended to be uniform in any way.</p>
 */
public interface Discovery {
    /**
     * TODO Prevent modification by creating and returning a read-only version of {@link Map}.
     *
     * @return A {@link Map} from the childs name to the child itself.
     * The caller is not allowed to modify this.
     */
    Map<String, Discovery> children();

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

    Discovery createChild(Object instance, String... relativePath);

    Discovery createChild(Object instance, int relativePathIndex, String... relativePath);

    void removeChild(Discovery child);

    <R> Optional<R> value(Class<? extends R> clazz);
}
