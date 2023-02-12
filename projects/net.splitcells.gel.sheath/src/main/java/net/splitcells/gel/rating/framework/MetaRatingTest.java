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
package net.splitcells.gel.rating.framework;

import net.splitcells.gel.rating.type.Profit;
import org.junit.jupiter.api.Test;

import static net.splitcells.gel.rating.framework.MetaRatingI.metaRating;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.type.Profit.profit;
import static org.assertj.core.api.Assertions.assertThat;

public class MetaRatingTest {

    @Test
    public void testCombinationOfPrimitives() {
        final var testSubject = metaRating().combine(profit(1));
        final var otherTestSubject = metaRating().combine(profit(3));
        MetaRating testProduct = testSubject.combine(otherTestSubject);
        assertThat(testProduct.content().get(Profit.class)).isEqualTo(profit(4));
        assertThat(testProduct.translate(Profit.class)).isEqualTo(profit(4));
    }

    @Test
    public void testBetterThan() {
        final var testSubject = metaRating().combine(cost(1));
        final var otherTestSubject = metaRating().combine(noCost());
        assertThat(otherTestSubject.betterThan(testSubject)).isTrue();
        assertThat(testSubject.betterThan(otherTestSubject)).isFalse();
    }

    @Test
    public void testUnequalContent() {
        final var testSubject = metaRating().combine(profit(1));
        final var otherTestSubject = metaRating().combine(profit(3));
        assertThat(testSubject).isNotEqualTo(otherTestSubject);
    }
}
