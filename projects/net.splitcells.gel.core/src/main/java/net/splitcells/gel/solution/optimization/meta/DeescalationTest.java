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
package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.annotations.UnitTest;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.optimization.meta.Deescalation.deescalation;

public class DeescalationTest {
    @UnitTest
    public void test() {
        final var s = attribute(Integer.class, "s");
        final var d = attribute(Integer.class, "d");
        final var testData = defineProblem("test")
                .withDemandAttributes(d)
                .withDemands(rangeClosed(1, 10).mapToObj(i -> list((Object) 1)).collect(toList()))
                .withSupplyAttributes(s)
                .withSupplies(rangeClosed(1, 10).mapToObj(i -> list((Object) 1)).collect(toList()))
                .withConstraint(c -> {
                    c.then(lineValueRater(l -> l.value(s) == -1));
                    return c;
                })
                .toProblem()
                .asSolution();
        testData.constraint().rating().equalz(noCost());
        final List<Integer> results = list();
        final var testSubject = deescalation(i -> solution -> {
                    solution.assign(solution.demandsFree().unorderedLinesStream().findFirst().orElseThrow()
                            , solution.suppliesFree().unorderedLinesStream().findFirst().orElseThrow());
                    results.add(i);
                }
                , 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        rangeClosed(1, 10).forEach(j -> testSubject.optimize(testData));
        results.requireEqualityTo(rangeClosed(0, 9).mapToObj(i -> (Object) (-i)).collect(toList()));
    }
}
