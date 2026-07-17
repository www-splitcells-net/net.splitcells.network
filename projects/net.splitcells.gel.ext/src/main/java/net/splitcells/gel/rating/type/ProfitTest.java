/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.gel.rating.type.Profit.profit;
import static net.splitcells.gel.rating.type.Profit.withoutProfit;
import static org.assertj.core.api.Assertions.assertThat;

public class ProfitTest {
	@Test
	public void testOrder() {
		assertThat(profit(0).compare_partially_to(profit(1)).orElseThrow()).isEqualTo(LESSER_THAN);
		assertThat(profit(1).compare_partially_to(profit(0)).orElseThrow()).isEqualTo(GREATER_THAN);
		assertThat(profit(-2).compare_partially_to(profit(-1)).orElseThrow()).isEqualTo(LESSER_THAN);
		assertThat(withoutProfit().compare_partially_to(withoutProfit()).orElseThrow()).isEqualTo(EQUAL);
	}
}
