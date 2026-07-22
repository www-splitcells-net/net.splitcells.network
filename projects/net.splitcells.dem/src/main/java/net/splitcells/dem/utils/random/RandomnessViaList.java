/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.random;

import lombok.val;
import net.splitcells.dem.data.set.list.List;

import java.util.Random;

import static net.splitcells.dem.utils.MathUtils.*;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * <p>This is a deterministic random value generator based on a given list of numbers.</p>
 * <p>TODO This implementation currently focuses on being easily and quickly to be developed and
 * not to be 100% randomness. Improve this as needed.</p>
 */
public class RandomnessViaList implements Randomness {
    public static Randomness randomnessViaList(List<Double> argValues) {
        return new RandomnessViaList(argValues);
    }

    private final List<Double> values;
    private int currentIndex = -1;

    private RandomnessViaList(List<Double> argValues) {
        values = argValues;
    }

    private Double nextValue() {
        return values.get(modulus(++currentIndex, values.size() - 1));
    }

    @Override public float floating(float min, float max) {
        val startVal = nextValue();
        if (min <= startVal && startVal <= max) {
            return doubleToFloat(startVal);
        }
        val maxedVal = modulus(roundToInt(startVal), roundToInt(max));
        if (maxedVal < min) {
            return min;
        }
        return maxedVal;
    }

    @Override public int integer(Integer min, Integer max) {
        val nextValue = modulus(roundToInt(nextValue()), max);
        if (nextValue < min) {
            return min;
        }
        return nextValue;
    }

    @Override public Random asRandom() {
        throw notImplementedYet();
    }
}
