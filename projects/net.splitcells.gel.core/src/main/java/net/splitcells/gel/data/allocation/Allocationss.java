/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.allocation;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.view.attribute.Attribute;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Allocationss {
    private Allocationss() {
        throw constructorIllegal();
    }

    public static Allocations allocations(String name, Discoverable parent
            , List<Attribute<?>> demandHeader
            , List<Attribute<?>> supplyHeader) {
        return AllocationsI.allocations(name, parent, demandHeader, supplyHeader);
    }
}
