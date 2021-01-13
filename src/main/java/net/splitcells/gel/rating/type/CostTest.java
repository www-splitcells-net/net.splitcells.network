package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

public class CostTest {

    @Test
    public void testOrder() {
        assertThat(cost(0).compare_partially_to(cost(1)).get()).isEqualTo(GREATER_THAN);
        assertThat(cost(1).compare_partially_to(cost(0)).get()).isEqualTo(LESSER_THAN);
        assertThat(cost(-2).compare_partially_to(cost(-1)).get()).isEqualTo(GREATER_THAN);
        assertThat(noCost().compare_partially_to(noCost()).get()).isEqualTo(EQUAL);
    }
}
