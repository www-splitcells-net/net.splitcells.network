package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.database.Databases;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.MinimalDistance.minimalDistance;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

public class MinimalDistanceTest {

    @Test
    public void testRating() {
        final var attribute = attribute(Double.class);
        final var lineSupplier = Databases.database(attribute);
        final var testSubject = then(minimalDistance(attribute, 2));
        {
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        final var testValues = Lists.list
                (lineSupplier.addTranslated(list(1.0))
                        , lineSupplier.addTranslated(list(3.0))
                        , lineSupplier.addTranslated(list(4.0))
                        , lineSupplier.addTranslated(list(1.0))
                );
        {
            testSubject.register(testValues.get(0));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register(testValues.get(1));
            assertThat(testSubject.lines().size()).isEqualTo(2);
            assertThat(testSubject.lineProcessing().size()).isEqualTo(2);
            assertThat(testSubject.complying()).hasSize(2);
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register(testValues.get(2));
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(cost(1));
        }
        {
            testSubject.register(testValues.get(3));
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.defying()).hasSize(4);
            assertThat(testSubject.rating()).isEqualTo(cost(2));
        }
        {
            testSubject.register_before_removal(testValues.get(0));
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(cost(1));
        }
        {
            testSubject.register_before_removal(testValues.get(1));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(2);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register_before_removal(testValues.get(2));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register_before_removal(testValues.get(3));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
    }

    @Test
    public void test_simple_neighbour_defiance() {
        final var attribute = attribute(Double.class);
        final var lineProducer = Databases.database(attribute);
        final var testSubject = then(minimalDistance(attribute, 2));
        {
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        final var testValues = list
                (lineProducer.addTranslated(list(1.0))
                        , lineProducer.addTranslated(list(1.0))
                        , lineProducer.addTranslated(list(1.0))
                );
        {
            testSubject.register(testValues.get(0));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register(testValues.get(1));
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(testSubject.rating()).isEqualTo(cost(1));
        }
        {
            testSubject.register(testValues.get(2));
            assertThat(testSubject.defying()).hasSize(3);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(3));
        }
        {
            testSubject.register_before_removal(testValues.get(0));
            assertThat(testSubject.defying()).hasSize(2);
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(cost(1));
        }
        {
            testSubject.register_before_removal(testValues.get(1));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(1);
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
        {
            testSubject.register_before_removal(testValues.get(2));
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).isEmpty();
            assertThat(testSubject.rating()).isEqualTo(noCost());
        }
    }
}
