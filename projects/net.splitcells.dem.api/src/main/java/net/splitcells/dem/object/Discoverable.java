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

import net.splitcells.dem.data.set.list.List;

import static net.splitcells.dem.data.set.list.Lists.list;

public interface Discoverable {

    Discoverable NO_CONTEXT = () -> list();

    static Discoverable discoverable(List<String> path) {
        return () -> path;
    }

    /**
     * TODO Use {@link net.splitcells.dem.data.set.list.ListView}.
     *
     * @return
     */
    List<String> path();

    default Discoverable child(List<String> extension) {
        final var rThis = this;
        return new Discoverable() {
            @Override
            public List<String> path() {
                return rThis.path().shallowCopy().withAppended(extension);
            }
        };
    }
}
