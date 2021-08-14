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
package net.splitcells.dem.utils.random;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.MathUtils;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.MathUtils.acceptable;
import static net.splitcells.dem.utils.random.Randomness.assertPlausibility;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RandomnessTest {
    @Test
    public void testTruthValueOfChanceOfZero() {
        assertThat(randomness().truthValue(0)).isFalse();
    }

    @Test
    public void testTruthValueOfChanceOfHundredPercent() {
        assertThat(randomness().truthValue(1)).isTrue();
    }

    @Test
    public void testTruthValueOf() {
        final var chance = 0.75f;
        final var randomness = randomness(0L);
        final var truthCounter = new AtomicInteger(0);
        final var runs = 1_000;
        final var deviation = 0.1f;
        rangeClosed(1, runs).forEach(i -> {
            if (randomness.truthValue(chance)) {
                truthCounter.incrementAndGet();
            }
        });
        assertThat(truthCounter.get()).isLessThan((int) ((chance + deviation) * runs));
        assertThat(truthCounter.get()).isGreaterThan((int) ((chance - deviation) * runs));
    }

    @Test
    public void testAssertPlausibilityWithPlausible() {
        assertPlausibility(.5f, 100, 45);
    }

    @Test
    public void testAssertPlausibilityWithImplausible() {
        assertThrows(Error.class, () -> assertPlausibility(.1f, 100, 30));
    }

    @Test
    public void testChooseOneOfOnEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> randomness().chooseOneOf(list()));
    }

    @Test
    public void testRemoveOneOfOnEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> randomness().removeOneOf(list()));
    }

    @Test
    public void testChooseAtMostMultipleOf() {
        assertThat(randomness(0L).chooseAtMostMultipleOf(2, listWithValuesOf(1, 2, 3)))
                .hasSize(2);
    }

    @Test
    public void testIntegerWithMean() {
        final var min = 2;
        final var mean = 5;
        final var max = 7;
        final var randomness = randomness(0L);
        final var runs = 1_000;
        final var deviation = 0.1f;
        final var sum = rangeClosed(1, runs)
                .map(i -> randomness.integer(min, mean, max))
                .reduce((a, b) -> a + b)
                .getAsInt();
        assertThat(acceptable(sum, runs * mean)).isTrue();
    }

    @Test
    public void testIntegerWithMeanOfDouble() {
        final var min = 2;
        final var mean = 5.5d;
        final var max = 7;
        final var randomness = randomness(0L);
        final var runs = 1_000_000;
        final var deviation = 0.1f;
        final var sum = rangeClosed(1, runs)
                .map(i -> randomness.integer(min, mean, max))
                .reduce((a, b) -> a + b)
                .getAsInt();
        System.out.println((double) sum / runs);
        assertThat(acceptable(sum, runs * mean)).isTrue();
    }
}
