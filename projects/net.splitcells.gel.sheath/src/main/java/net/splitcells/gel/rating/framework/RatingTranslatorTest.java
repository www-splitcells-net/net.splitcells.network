package net.splitcells.gel.rating.framework;

import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.rating.type.Optimality;
import net.splitcells.gel.rating.type.Profit;
import org.junit.jupiter.api.Test;

import static net.splitcells.gel.rating.structure.RatingTranslatorI.ratingTranslator;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Optimality.optimality;
import static net.splitcells.gel.rating.type.Profit.profit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RatingTranslatorTest {
    @Test
    public void testIdentityTranslation() {
        final Cost rating = cost(-1);
        assertThat(ratingTranslator(Maps.<Class<? extends Rating>, Rating>map().with(Cost.class, rating)).translate(Cost.class)).isEqualTo(rating);
    }

    @Test
    public void testTranslatorChoice() {
        final var rating = cost(-1);
        final var expectedRatingClass = Profit.class;
        final var expectedRating = profit(1);
        final var unexpectedRatingClass = Optimality.class;
        final var unexpectedRating = optimality(0.5);
        final var testSubject = ratingTranslator(Maps.<Class<? extends Rating>, Rating>map().with(Cost.class, rating));
        testSubject.registerTranslator(Profit.class, a -> true, a -> expectedRating);
        testSubject.registerTranslator(Optimality.class, a -> false, a -> unexpectedRating);
        assertThat(testSubject.translate(expectedRatingClass)).isEqualTo(expectedRating);
        assertThrows(Throwable.class, () -> testSubject.translate(unexpectedRatingClass));
    }
}
