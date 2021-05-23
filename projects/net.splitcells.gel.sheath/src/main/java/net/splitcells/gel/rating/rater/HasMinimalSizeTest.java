package net.splitcells.gel.rating.rater;

import net.splitcells.gel.constraint.type.Then;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.rating.rater.HasMinimalSize.hasMinimalSize;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

public class HasMinimalSizeTest {
    @Test
    public void testRating() {
        final var lineSupplier = database();
        final var testValue = Then.then(hasMinimalSize(2));
        assertThat(testValue.complying()).isEmpty();
        assertThat(testValue.defying()).isEmpty();
        final var firstTestValue = lineSupplier.addTranslated(list());
        final var secondTestValue = lineSupplier.addTranslated(list());
        final var thirdTestVaLUE = lineSupplier.addTranslated(list());
        {
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.register(firstTestValue);
            assertThat(testValue.defying()).hasSize(1);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(cost(1));
        }
        {
            testValue.register(secondTestValue);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).hasSize(2);
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.register(thirdTestVaLUE);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).hasSize(3);
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.register_before_removal(thirdTestVaLUE);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).hasSize(2);
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
        {
            testValue.register_before_removal(secondTestValue);
            assertThat(testValue.defying()).hasSize(1);
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(cost(1));
        }
        {
            testValue.register_before_removal(firstTestValue);
            assertThat(testValue.defying()).isEmpty();
            assertThat(testValue.complying()).isEmpty();
            assertThat(testValue.rating()).isEqualTo(noCost());
        }
    }
}
