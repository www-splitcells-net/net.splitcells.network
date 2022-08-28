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