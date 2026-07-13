/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.random;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.random.BuilderRandomConfigurable.builderRandomConfigurable;

public class RandomnessSource {

    private static final BuilderRandomConfigurable factory = builderRandomConfigurable();

    private RandomnessSource() {
        throw constructorIllegal();
    }

    public static Randomness randomness() {
        return factory.rnd();
    }

    public static Randomness randomness(long seed) {
        return factory.rnd(seed);
    }

    public static Randomness cryptoRandomness() {
        return factory.rndCrypt();
    }

}
