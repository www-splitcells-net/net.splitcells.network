package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.gel.rating.type.Profit.profit;
import static net.splitcells.gel.rating.type.Profit.withoutProfit;
import static org.assertj.core.api.Assertions.assertThat;

public class ProfitTest {
	@Test
	public void testOrder() {
		assertThat(profit(0).compare_partially_to(profit(1)).get()).isEqualTo(LESSER_THAN);
		assertThat(profit(1).compare_partially_to(profit(0)).get()).isEqualTo(GREATER_THAN);
		assertThat(profit(-2).compare_partially_to(profit(-1)).get()).isEqualTo(LESSER_THAN);
		assertThat(withoutProfit().compare_partially_to(withoutProfit()).get()).isEqualTo(EQUAL);
	}
}
