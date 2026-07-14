/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.random;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.MathUtils;

import java.util.Random;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class RandomnessViaList implements Randomness {
    private Randomness randomnessViaList(List<Double> argValues) {
        return new RandomnessViaList(argValues);
    }

    private final List<Double> values;
    private int currentIndex = -1;

    private RandomnessViaList(List<Double> argValues) {
        values = argValues;
    }

    private Double nextValue() {
        return values.get(MathUtils.modulus(++currentIndex, values.size() - 1));
    }

    @Override public float floating(float min, float max) {
        throw notImplementedYet();
    }

    @Override public int integer(Integer min, Integer max) {
        throw notImplementedYet();
    }

    @Override public Random asRandom() {
        throw notImplementedYet();
    }
}
