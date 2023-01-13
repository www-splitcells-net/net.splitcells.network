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
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CostTest {

    @Test
    public void t() {
        assertThrows(IllegalArgumentException.class, () -> cost(-1));
    }

    @Test
    public void testOrder() {
        assertThat(cost(0).compare_partially_to(cost(1)).orElseThrow()).isEqualTo(LESSER_THAN);
        assertThat(cost(1).compare_partially_to(cost(0)).orElseThrow()).isEqualTo(GREATER_THAN);
        assertThat(noCost().compare_partially_to(noCost()).orElseThrow()).isEqualTo(EQUAL);
    }
}
