/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.random;

import lombok.val;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.MathUtils;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.testing.Assertions.*;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.MathUtils.acceptable;
import static net.splitcells.dem.utils.random.Randomness.assertPlausibility;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static org.assertj.core.api.Assertions.assertThat;

public class RandomnessTest {
    @Test public void testIntegerFromDistribution() {
        final var testSubject = randomness(0L);
        requireThrow(() -> testSubject.integerFromDistribution(100f));
        requireThrow(() -> testSubject.integerFromDistribution());
        requireEquals(testSubject.integerFromDistribution(1f), 0);
        requireEquals(testSubject.integerFromDistribution(0f, 1f), 1);
        val samples = 1_000;
        int counter0 = 0, counter1 = 0, counter2 = 0;
        for (int i = 0; i < samples; ++i) {
            val result = testSubject.integerFromDistribution(0f, .7f, .3f);
            if (result == 0) {
                ++counter0;
            } else if (result == 1) {
                ++counter1;
            } else if (result == 2) {
                ++counter2;
            } else {
                throw execException(result + "");
            }
        }
        require(acceptable(((double) counter0 / samples) + 1f, 1f));
        require(acceptable(((double) counter1 / samples) + 1f, 1.7f));
        require(acceptable(((double) counter2 / samples) + 1f, 1.3f));
    }

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
        final var runs = 1_000;
        final var deviation = 0.1f;
        final var truthCount = rangeClosed(1, runs).map(i -> {
            if (randomness.truthValue(chance)) {
                return 1;
            } else {
                return 0;
            }
        }).reduce(0, (a, b) -> a + b);
        assertThat(truthCount).isLessThan((int) ((chance + deviation) * runs));
        assertThat(truthCount).isGreaterThan((int) ((chance - deviation) * runs));
    }

    @Test
    public void testAssertPlausibilityWithPlausible() {
        assertPlausibility(.5f, 100, 45);
    }

    @Test
    public void testAssertPlausibilityWithImplausible() {
        requireThrow(Error.class, () -> assertPlausibility(.1f, 100, 30));
    }

    @Test
    public void testChooseOneOfOnEmptyList() {
        requireThrow(IllegalArgumentException.class, () -> randomness().chooseOneOf(list()));
    }

    @Test
    public void testRemoveOneOfOnEmptyList() {
        requireThrow(IllegalArgumentException.class, () -> randomness().removeOneOf(list()));
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
        final var sum = rangeClosed(1, runs)
                .map(i -> randomness.integer(min, mean, max))
                .reduce((a, b) -> a + b)
                .getAsInt();
        assertThat(acceptable(sum, (double) runs * mean)).isTrue();
    }

    @Test
    public void testIntegerWithMeanOfDouble() {
        final var min = 2;
        final var mean = 5.5d;
        final var max = 7;
        final var randomness = randomness(0L);
        final var runs = 1_000;
        final var sum = rangeClosed(1, runs)
                .map(i -> randomness.integer(min, mean, max))
                .reduce((a, b) -> a + b)
                .getAsInt();
        assertThat(acceptable(sum, runs * mean)).isTrue();
    }
}
