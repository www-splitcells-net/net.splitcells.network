/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.problem;

import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.data.set.list.List;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;

public interface DefineDemands extends DefineSupplyAttributes {

    @ReturnsThis
    default DefineSupplyAttributes withEmptyDemands(int demandCount) {
        final List<List<Object>> demands = list();
        rangeClosed(1, demandCount).forEach(i -> demands.add(list()));
        return withDemands(demands);
    }

    default DefineSupplyAttributes withNoDemands() {
        return withDemands(list());
    }

    @ReturnsThis
    DefineSupplyAttributes withDemands(List<Object> demand, @SuppressWarnings("unchecked") List<Object>... demands);

    @ReturnsThis
    DefineSupplyAttributes withDemands(List<List<Object>> demands);
}
