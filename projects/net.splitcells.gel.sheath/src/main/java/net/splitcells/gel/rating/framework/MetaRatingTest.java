package net.splitcells.gel.rating.framework;

import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.structure.MetaRatingI;
import net.splitcells.gel.rating.type.Profit;
import org.junit.jupiter.api.Test;

import static net.splitcells.gel.rating.type.Profit.profit;
import static org.assertj.core.api.Assertions.assertThat;

public class MetaRatingTest {

    @Test
    public void testCombinationOfPrimitives() {
        final var testSubject = MetaRatingI.metaRating().combine(profit(1));
        final var otherTestSubject = MetaRatingI.metaRating().combine(profit(3));
        MetaRating testProduct = testSubject.combine(otherTestSubject);
        assertThat(testProduct.content().get(Profit.class)).isEqualTo(profit(4));
        assertThat(testProduct.translate(Profit.class)).isEqualTo(profit(4));
    }
}
