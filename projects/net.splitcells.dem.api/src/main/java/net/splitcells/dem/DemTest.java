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
package net.splitcells.dem;

import net.splitcells.dem.resource.communication.log.MessageFilter;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static net.splitcells.dem.Dem.sleepAtLeast;
import static net.splitcells.dem.data.atom.Bools.require;

/**
 * Used for experiments.
 * <p>
 * maven.execute net.splitcells.dem.DemTest
 */
public class DemTest {
    public static void main(String... args) {
        Dem.process(() -> {
            
        }, (env) -> {
            env.config()
                    .withConfigValue(MessageFilter.class, (message) -> true);
        });
    }

    @Test
    public void testSleepAtLeast() {
        final var start = Instant.now();
        final var waitingTime = 1000l;
        sleepAtLeast(waitingTime);
        final var end = Instant.now();
        require(Duration.between(start, end).toMillis() >= waitingTime);
    }
}
