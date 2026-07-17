/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
