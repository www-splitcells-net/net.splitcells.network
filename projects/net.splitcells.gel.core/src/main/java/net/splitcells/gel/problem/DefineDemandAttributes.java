/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;

public interface DefineDemandAttributes {
    DefineDemands withDemandAttributes(Attribute<?>... demandAttributes);

    DefineDemands withDemandAttributes2(List<Attribute<Object>> demandAttributes);

    DefineDemands withDemandAttributes(List<Attribute<?>> demandAttributes);

    DefineSupplyAttributes withDemands(Table demands);
}
