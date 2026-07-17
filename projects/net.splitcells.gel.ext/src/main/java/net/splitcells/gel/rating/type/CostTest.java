/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;
import static net.splitcells.dem.testing.Assertions.requireThrow;

public class CostTest {

    @Test
    public void t() {
        requireThrow(IllegalArgumentException.class, () -> cost(-1));
    }

    @Test
    public void testOrder() {
        assertThat(cost(0).compare_partially_to(cost(1)).orElseThrow()).isEqualTo(LESSER_THAN);
        assertThat(cost(1).compare_partially_to(cost(0)).orElseThrow()).isEqualTo(GREATER_THAN);
        assertThat(noCost().compare_partially_to(noCost()).orElseThrow()).isEqualTo(EQUAL);
    }
}
