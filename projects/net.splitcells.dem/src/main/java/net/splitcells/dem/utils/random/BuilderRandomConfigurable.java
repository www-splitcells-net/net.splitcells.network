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

import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.data.atom.Bool;

import java.util.Optional;

import static net.splitcells.dem.Dem.environment;

public class BuilderRandomConfigurable implements RndSrcF {

    private RndSrcF builderRandom;

    protected BuilderRandomConfigurable() {
        if (environment().config().configValue(IsDeterministic.class).isEmpty()
                || environment().config().configValue(IsDeterministic.class).orElseThrow().isTrue()) {
            deterministicBuilder(environment().config().configValue(DeterministicRootSourceSeed.class));
        } else {
            builderRandom = new RndSrcStandardF();
        }
    }

    protected BuilderRandomConfigurable(Optional<Bool> deterministic) {
        updateDeterminism(deterministic);
    }

    protected BuilderRandomConfigurable(Long seed) {
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
