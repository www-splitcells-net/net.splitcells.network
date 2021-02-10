package net.splitcells.gel.rating.framework;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.rating.structure.MetaRatingMerger;
import net.splitcells.gel.rating.structure.MetaRatingMergerI;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.rating.type.Profit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Profit.profit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MetaRatingMergerTest {

    private MetaRatingMerger testSubject;
    private Map<Class<? extends Rating>, Rating> testContent = map();

    @BeforeEach
    public void setup() {
        testContent.clear();
        testContent.put(Profit.class, profit(3));
        testSubject = MetaRatingMergerI.metaRatingMerger(testContent);
    }

    @Test
    public void testCombinationOfSame() {
        testSubject.registerMerger(
                (base, addition) -> base.containsKey(Profit.class) && addition.containsKey(Profit.class),
                (based, addition) -> {
                    final var oldProfit = (Profit) based.get(Profit.class);
                    /**
                     * Other {@link Rating} instances are not update and therefore these are incorrect.
                     */
                    Map<Class<? extends Rating>, Rating> rVal = map();
                    rVal.put(Profit.class, oldProfit.combine(addition.get(Profit.class)));
                    return rVal;
                });
        MetaRating firstResult = testSubject.combine(profit(1));
        assertThat(firstResult.content().get(Profit.class).compare_partially_to(profit(4)).get()).isEqualTo(EQUAL);
        MetaRating secondResult = firstResult.combine(profit(3));
        assertThat(secondResult.content().get(Profit.class).compare_partially_to(profit(7)).get()).isEqualTo(EQUAL);
    }

    @Test
    public void testCombinationOfCompatible() {
        testSubject.registerMerger(
                (base, addition) -> base.containsKey(Profit.class) && addition.containsKey(Cost.class),
                // TODO Use this for combination method library.
                (base, addition) -> {
                    // TODO Collection where Objects of distinct types can be stored in type safe
                    // manner.
                    Map<Class<? extends Rating>, Rating> rVal = map();
                    final var oldProfit = (Profit) base.get(Profit.class);
                    final var newProfit = (Cost) addition.get(Cost.class);
                    rVal.put(Profit.class, cost(newProfit.value() - oldProfit.value()));
                    return rVal;
                });
        MetaRating firstResult = testSubject.combine(cost(1));
        assertThat(firstResult.content().get(Profit.class).compare_partially_to(cost(-2)).get()).isEqualTo(EQUAL);
    }

    @Test
    public void testCombinationOfIncompatible() {
        assertThrows(RuntimeException.class, () -> {
            testSubject.registerMerger((base, addition) -> false, (base, addition) -> {
                throw new RuntimeException();
            });
            testSubject.combine(cost(1));
        });
    }
}
