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
package net.splitcells.gel.rating.framework;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.rating.type.Profit;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Profit.profit;
import static net.splitcells.dem.testing.Assertions.assertThrows;

public class MetaRatingMergerTest {

    @Test
    public void testCombinationOfSame() {
        final Map<Class<? extends Rating>, Rating> testContent = map();
        testContent.put(Profit.class, profit(3));
        final var testSubject = MetaRatingMergerI.metaRatingMerger(testContent);
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
        firstResult.content().get(Profit.class).compare_partially_to(profit(4)).get().requireEqualsTo(EQUAL);
        MetaRating secondResult = firstResult.combine(profit(3));
        secondResult.content().get(Profit.class).compare_partially_to(profit(7)).get().requireEqualsTo(EQUAL);
    }

    @Test
    public void testCombinationOfCompatible() {
        final Map<Class<? extends Rating>, Rating> testContent = map();
        testContent.put(Profit.class, profit(5));
        final var testSubject = MetaRatingMergerI.metaRatingMerger(testContent);
        testSubject.registerMerger(
                (base, addition) -> base.containsKey(Profit.class) && addition.containsKey(Cost.class),
                // TODO Use this for combination method library.
                (base, addition) -> {
                    // TODO Collection where Objects of distinct types can be stored in type safe
                    // manner.
                    Map<Class<? extends Rating>, Rating> rVal = map();
                    final var oldProfit = (Profit) base.get(Profit.class);
                    final var newProfit = (Cost) addition.get(Cost.class);
                    rVal.put(Profit.class, cost(newProfit.value() + oldProfit.value()));
                    return rVal;
                });
        MetaRating firstResult = testSubject.combine(cost(1));
        firstResult.content().get(Profit.class).compare_partially_to(cost(6)).get().requireEqualsTo(EQUAL);
    }

    @Test
    public void testCombinationOfIncompatible() {
        final Map<Class<? extends Rating>, Rating> testContent = map();
        testContent.put(Profit.class, profit(7));
        final var testSubject = MetaRatingMergerI.metaRatingMerger(testContent);
        assertThrows(RuntimeException.class, () -> {
            testSubject.registerMerger((base, addition) -> false, (base, addition) -> {
                throw new RuntimeException();
            });
            testSubject.combine(cost(1));
        });
    }
}
