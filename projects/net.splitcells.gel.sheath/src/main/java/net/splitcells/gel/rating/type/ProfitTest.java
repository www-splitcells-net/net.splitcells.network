/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
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
