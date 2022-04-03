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
package net.splitcells.gel.data.allocation;

import net.splitcells.dem.environment.resource.ResourceOptionI;
import net.splitcells.gel.data.database.Database;

import static net.splitcells.dem.Dem.environment;

public class Allocationss extends ResourceOptionI<AllocationsFactory> {
    public Allocationss() {
        super(() -> new AllocationsIFactory());
    }

    public static Allocations allocations(String name, Database demands, Database supplies) {
        return environment().config().configValue(Allocationss.class).allocations(name, demands, supplies);
    }
}
