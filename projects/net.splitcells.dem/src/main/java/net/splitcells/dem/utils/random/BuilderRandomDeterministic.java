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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

@JavaLegacyArtifact
public class BuilderRandomDeterministic implements RndSrcF {

    private final Random seedGenerator;
    // HACK constructor argument should be parameterized
    private final SecureRandom seedGeneratorCrypt;

    public BuilderRandomDeterministic(long seed) {
        seedGenerator = new Random(seed);
        seedGeneratorCrypt = new SecureRandom(
                ByteBuffer.allocate(Long.BYTES).putLong(seed).array());
    }

    @Override
    public Randomness rnd(Long seed) {
        return new JavaRandomWrapper(new Random(seed));
    }

    @Override
    public Randomness rnd() {
        return new JavaRandomWrapper(new Random(seedGenerator.nextLong()));
    }

    @Override
    public RndSrcCrypt rndCrypt() {
        return new JavaRandomWrapper(
                new SecureRandom(seedGeneratorCrypt.generateSeed(20)));
    }

}
