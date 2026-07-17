/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.problem;


import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;

import java.util.function.Function;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;

public interface DefineSupply {

    default DefineConstraints withEmptySupplies(int supplyCount) {
        final List<List<Object>> supplies = list();
        rangeClosed(1, supplyCount).forEach(i -> supplies.add(list()));
        return withSupplies(supplies);
    }

    default DefineConstraints withNoSupplies() {
        return withSupplies(list());
    }

    DefineConstraints withSupplies(List<Object>... supplies);

    DefineConstraints withSupplies(List<List<Object>> supplies);

}
