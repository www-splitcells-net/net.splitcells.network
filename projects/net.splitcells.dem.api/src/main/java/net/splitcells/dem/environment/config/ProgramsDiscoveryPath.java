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
package net.splitcells.dem.environment.config;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.object.Discoverable;

import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;

/**
 * Represents the root path of the currently running application.
 * This is used,
 * in order to create a tree containing all {@link Discoverable} objects of the currently running program and
 * thereby provide an easy data structure to observe the program's major objects.
 * This is intended to be easily integrated into the web server or filesystem.
 */
public class ProgramsDiscoveryPath implements Option<Discoverable> {
    @Override
    public Discoverable defaultValue() {
        return () -> Lists.list(simplifiedName(Dem.environment().config().configValue(ProgramRepresentative.class)).split("\\."));
    }
}
