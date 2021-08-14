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
package net.splitcells.dem.object;

import net.splitcells.dem.data.set.list.List;

import static net.splitcells.dem.data.set.list.Lists.list;

public interface Discoverable {

    /**
     * RENAME Find better name.
     */
    Discoverable NO_CONTEXT = () -> list();

    static Discoverable discoverable(List<String> path) {
        return () -> path;
    }

    /**
     * TODO Use {@link net.splitcells.dem.data.set.list.ListView}.
     * @return
     */
    List<String> path();
}
