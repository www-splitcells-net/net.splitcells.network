/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

public class Time {
    public static final long ONE_SECOND_IN_NANOSECONDS = 1_000_000_000L;

    private Time() {

    }

    /**
     * @return The most precise and fast to determine amount of time elapsed since the programs starts.
     * This can only be used in order to measure the elapsed time in nanoseconds
     * without any guarantees regarding its actual precision.
     */
    public static long elapsedTimeInNanoSeconds() {
        return System.nanoTime();
    }

    public static double nanoToSeconds(long nanoSeconds) {
        return (double) nanoSeconds / ONE_SECOND_IN_NANOSECONDS;
    }

    public static long measureTimeInNanoSeconds(Runnable run) {
        final var startTime = elapsedTimeInNanoSeconds();
        run.run();
        return elapsedTimeInNanoSeconds() - startTime;
    }
}
