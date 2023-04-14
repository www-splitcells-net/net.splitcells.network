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
package net.splitcells.gel.rating.rater;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.RegulatedLength.regulatedLength;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static org.assertj.core.api.Assertions.assertThat;

public class RegulatedLengthTest {
    @Test
    public void testRegulatedLength() {
        final var a = attribute(Integer.class);
        final var b = attribute(Integer.class);
        final var testSubjectContainer = defineProblem("regulated length test")
                .withDemandAttributes(a, b)
                .withDemands(list
                        (list(3, 3)
                                , list(3, 7)))
                .withSupplyAttributes()
                .withSupplies(list
                        (list()
                                , list()))
                .withConstraint(r -> {
                    r.then(regulatedLength(a, b));
                    return r;
                })
                .toProblem()
                .asSolution();
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());
        testSubjectContainer.allocate(testSubjectContainer.demandsFree().rawLine(0)
                , testSubjectContainer.suppliesFree().rawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());

        testSubjectContainer.allocate(testSubjectContainer.demandsFree().rawLine(1)
                , testSubjectContainer.suppliesFree().rawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(7));

        testSubjectContainer.remove(testSubjectContainer.rawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());

        testSubjectContainer.remove(testSubjectContainer.rawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());

        testSubjectContainer.allocate(testSubjectContainer.demandsFree().rawLine(1)
                , testSubjectContainer.suppliesFree().rawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(4));

        testSubjectContainer.allocate(testSubjectContainer.demandsFree().rawLine(0)
                , testSubjectContainer.suppliesFree().rawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(7));
    }
}
