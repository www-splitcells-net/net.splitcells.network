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
package net.splitcells.gel.data.assignment;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.AspectOrientedConstructorBase;
import net.splitcells.gel.data.database.Database;

import java.util.function.Function;

import static net.splitcells.dem.resource.AspectOrientedConstructorBase.aspectOrientedConstructor;

public class AssignmentsIFactory implements AssignmentsFactory {

    private final AspectOrientedConstructorBase<Assignments> aspects = aspectOrientedConstructor();

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }

    @Override
    public Assignments allocations(String name, Database demands, Database supplies) {
        return AssignmentsI.allocations(name, demands, supplies);
    }

    @Override
    public AspectOrientedConstructor withAspect(Function aspect) {
        return aspects.withAspect(aspect);
    }

    @Override
    public Assignments joinAspects(Assignments arg) {
        return aspects.joinAspects(arg);
    }
}
