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
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.Flows;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.ArrayList;

@JavaLegacy
public class ListI<T> extends ArrayList<T> implements List<T> {
    public static <T> List<T> _list() {
        return new ListI<>();
    }

    private ListI() {
    }

    @Override
    public void prepareForSizeOf(int targetSize) {
        this.ensureCapacity(targetSize * 2);
    }

    @Override
    public Flow<T> stream() {
        return Flows.flow(super.stream());
    }
}
