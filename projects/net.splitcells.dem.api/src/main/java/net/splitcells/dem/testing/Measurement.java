/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing;

public class Measurement {
    public static Measurement measurement(long time, long value) {
        return new Measurement(time, value);
    }

    private final long time;
    private final long value;

    private Measurement(long argTime, long argValue) {
        time = argTime;
        value = argValue;
    }

    public long time() {
        return time;
    }

    public long value() {
        return value;
    }
}
