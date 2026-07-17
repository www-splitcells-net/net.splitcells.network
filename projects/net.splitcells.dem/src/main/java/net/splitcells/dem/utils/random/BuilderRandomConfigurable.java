/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.random;

import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.data.atom.Bool;

import java.util.Optional;

import static net.splitcells.dem.Dem.environment;

public class BuilderRandomConfigurable implements RndSrcF {

    private RndSrcF builderRandom;

    public static BuilderRandomConfigurable builderRandomConfigurable() {
        return new BuilderRandomConfigurable();
    }

    /**
     * If {@link IsDeterministic} is not set, the factory with the most secure {@link RndSrcF#rndCrypt()} has to be used.
     */
    private BuilderRandomConfigurable() {
        if (environment().config().configValue(IsDeterministic.class).isPresent()
                && environment().config().configValue(IsDeterministic.class).orElseThrow().isTrue()) {
            deterministicBuilder(environment().config().configValue(DeterministicRootSourceSeed.class));
        } else {
            builderRandom = new RndSrcStandardF();
        }
    }

    private BuilderRandomConfigurable(Optional<Bool> deterministic) {
        updateDeterminism(deterministic);
    }

    private BuilderRandomConfigurable(Long seed) {
        deterministicBuilder(seed);
    }

    public void updateDeterminism(Optional<Bool> arg) {
        if (arg.isEmpty() || arg.get().isTrue()) {
            deterministicBuilder(environment().config().configValue(DeterministicRootSourceSeed.class));
        } else if (arg.get().isFalse()) {
            this.builderRandom = new RndSrcStandardF();
        } else {
            throw new RuntimeException();
        }
    }

    private void deterministicBuilder(Long seed) {
        this.builderRandom = new BuilderRandomDeterministic(seed);
    }

    @Override
    public Randomness rnd(Long seed) {
        return builderRandom.rnd(seed);
    }

    @Override
    public Randomness rnd() {
        return builderRandom.rnd();
    }

    @Override
    public RndSrcCrypt rndCrypt() {
        return builderRandom.rndCrypt();
    }

}
