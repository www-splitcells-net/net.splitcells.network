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

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.utils.MathUtils;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY_KEY;
import static net.splitcells.dem.utils.MathUtils.*;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static org.assertj.core.api.Assertions.assertThat;

public interface Randomness {

    /**
     * Creates a deterministic {@link Randomness}, that returns it's values according to the given
     * {@link Iterator} of values. This useful in order to write simple tests.
     *
     * @param valueList Requires values returned by the {@link Randomness} to be created.
     * @return Deterministic {@link Randomness}
     */
    static Randomness listBasedRandomness(Iterator<Integer> valueList) {
        return new Randomness() {

            @Override public float floating(float min, float max) {
                return valueList.next();
            }

            @Override
            public int integer(Integer min, Integer max) {
                return valueList.next();
            }

            @Override
            public Random asRandom() {
                throw notImplementedYet();
            }
        };
    }

    default int integer() {
        return integer(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    float floating(float min, float max);

    int integer(final Integer min, final Integer max);

    default int integer(int min, double mean, int max) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(intToDouble(min))
                    .describedAs("min: " + min + " mean: " + mean + " max: " + max)
                    .isLessThanOrEqualTo(mean);
            assertThat(mean)
                    .describedAs("min: " + min + " mean: " + mean + " max: " + max)
                    .isLessThanOrEqualTo(max);
        }
        final var distance = distance(min, max);
        final var distanceHalf = roundToInt(distance / 2d);
        if (truthValue((mean) / MathUtils.intToDouble(max))) {
            return integer(min + distanceHalf, max);
        } else {
            return integer(min, min + distanceHalf);
        }
    }

    default int integer(int min, int mean, int max) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(min).isLessThanOrEqualTo(mean);
            assertThat(mean).isLessThanOrEqualTo(max);
        }
        if (truthValue()) {
            return integer(min, mean);
        } else {
            return integer(mean, max);
        }
    }

    /**
     *
     * @param distribution Contains the distribution weights of the indexes.
     *                     The sum of distribution should be very near to 1.
     *                     A check of {@link MathUtils#acceptable(double, double)} regarding the sum
     *                     should return true.
     * @return <p>Chooses random index of the distribution argument.
     * The weighting of the indexes corresponds to the distribution values.</p>
     *
     */
    default int integerFromDistribution(float[] distribution) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            float sum = 0f;
            for (float d : distribution) {
                sum += d;
            }
            require(MathUtils.acceptable(sum, 1d));
        }
        float rndChoice = floating(0, 1);
        float currentSum = 0f;
        for (int i = 0; i < distribution.length - 1; ++i) {
            currentSum += distribution[i];
            if (rndChoice < currentSum) {
                return i;
            }
        }
        return distribution.length - 1;
    }

    default boolean truthValue() {
        return 1 == integer(0, 1);
    }

    default boolean truthValue(double chance) {
        return truthValue(doubleToFloat(chance));
    }

    default boolean truthValue(float chance) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(chance)
                    .withFailMessage("Invalid chance given.")
                    .isBetween(0f, 1f);
        }
        final int scaleFactor = 10_000;
        final var scaledChance = chance * scaleFactor;
        return scaledChance >= integer(0, scaleFactor);
    }

    default String readableAsciiString(int size) {
        final StringBuilder rBase = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            rBase.append((char) asRandom().nextInt(126));
        }
        return rBase.toString();
    }

    @JavaLegacyArtifact
    Random asRandom();

    default <T> List<T> chooseAtMostMultipleOf(int numberOfThingsToChoose, List<T> args) {
        if (args.isEmpty()) {
            return list();
        }
        if (numberOfThingsToChoose >= args.size()) {
            return listWithValuesOf(args);
        }
        final List<T> chosenArgs = list();
        range(0, numberOfThingsToChoose)
                .forEach(i -> {
                    final var nextIndexCandidate = this.integer(0, args.size() - 1);
                    chosenArgs.add(args.remove(nextIndexCandidate));
                });
        return chosenArgs;
    }

    default <T> T chooseOneOf(java.util.List<T> arg) {
        if (arg.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return arg.get(asRandom().nextInt(arg.size()));
    }

    default <T> T chooseOneOf(List<T> arg) {
        if (arg.isEmpty()) {
            throw new IllegalArgumentException("Argument should not be empty.");
        }
        return arg.get(asRandom().nextInt(arg.size()));
    }

    default <T> T removeOneOf(List<T> arg) {
        if (arg.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return arg.remove(asRandom().nextInt(arg.size()));
    }

    static void assertPlausibility(double chance, int tries, int positives) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(chance).isBetween(0d, 1d);
            assertThat(tries).isGreaterThan(-1);
            assertThat(positives).isGreaterThan(-1);
        }
        final var actualChance = abs((float) positives / (float) tries);
        final var chanceDiff = actualChance - chance;
        final var isPlausible = chanceDiff < 0.1;
        assertThat(isPlausible).withFailMessage("actualChance: " + actualChance + "chanceDiff: " + chanceDiff).isTrue();
    }
}
