/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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

        }, env -> {
            env.config()
                    .withConfigValue(MessageFilter.class, message -> true);
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
