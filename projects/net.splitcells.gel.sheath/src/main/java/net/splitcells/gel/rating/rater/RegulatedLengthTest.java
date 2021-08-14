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
package net.splitcells.gel.rating.rater;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueSelector;
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
        final var testSubjectContainer = defineProblem()
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
        testSubjectContainer.allocate(testSubjectContainer.demandsUnused().getRawLine(0)
                , testSubjectContainer.suppliesFree().getRawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());

        testSubjectContainer.allocate(testSubjectContainer.demandsUnused().getRawLine(1)
                , testSubjectContainer.suppliesFree().getRawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(7));

        testSubjectContainer.remove(testSubjectContainer.getRawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());

        testSubjectContainer.remove(testSubjectContainer.getRawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(noCost());

        testSubjectContainer.allocate(testSubjectContainer.demandsUnused().getRawLine(1)
                , testSubjectContainer.suppliesFree().getRawLine(1));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(4));

        testSubjectContainer.allocate(testSubjectContainer.demandsUnused().getRawLine(0)
                , testSubjectContainer.suppliesFree().getRawLine(0));
        assertThat(testSubjectContainer.constraint().rating()).isEqualTo(cost(7));
    }
}
