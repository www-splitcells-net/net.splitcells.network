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

import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.data.atom.Bool;

import java.util.Optional;

import static net.splitcells.dem.Dem.environment;

public class BuilderRandomConfigurable implements RndSrcF {

    private RndSrcF builderRandom;

    protected BuilderRandomConfigurable() {
        if (environment().config().configValue(IsDeterministic.class).isEmpty()
                || environment().config().configValue(IsDeterministic.class).get().isTrue()) {
            deterministic_builder(environment().config().configValue(DeterministicRootSourceSeed.class));
        } else {
            builderRandom = new RndSrcStandardF();
        }
    }

    protected BuilderRandomConfigurable(Optional<Bool> deterministic) {
        update_determinism(deterministic);
    }

    protected BuilderRandomConfigurable(Long seed) {
        deterministic_builder(seed);
    }

    public void update_determinism(Optional<Bool> arg) {
        if (arg.isEmpty() || arg.get().isTrue()) {
            deterministic_builder(environment().config().configValue(DeterministicRootSourceSeed.class));
        } else if (arg.isPresent() && arg.get().isFalse()) {
            this.builderRandom = new RndSrcStandardF();
        } else {
            throw new RuntimeException();
        }
    }

    private void deterministic_builder(Long seed) {
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
