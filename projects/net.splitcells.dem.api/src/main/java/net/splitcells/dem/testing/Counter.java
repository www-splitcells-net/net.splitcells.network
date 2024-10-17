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
package net.splitcells.dem.testing;

import net.splitcells.dem.data.set.list.List;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Time.ONE_SECOND_IN_NANOSECONDS;
import static net.splitcells.dem.resource.Time.elapsedTimeInNanoSeconds;
import static net.splitcells.dem.testing.Measurement.measurement;

public class Counter {

    public static Counter counter() {
        return new Counter();
    }

    private final List<Measurement> measurements = list();
    private long lastMeasurementTime = elapsedTimeInNanoSeconds();
    private long currentCount = 0;

    private Counter() {

    }

    public void count(long times) {
        final long currentTime = elapsedTimeInNanoSeconds();
        if (currentTime - lastMeasurementTime > ONE_SECOND_IN_NANOSECONDS) {
            measurements.add(measurement(currentTime, currentCount));
            lastMeasurementTime = currentTime;
            currentCount = 0;
        } else {
            currentCount += times;
        }
    }

    /**
     * TODO This method currently does not calculate the average correctly,
     * as fo
     */
    public double averageCount() {
        long sum = 0;
        for (final var measurement : measurements) {
            sum += measurement.value();
        }
        if (sum == 0) {
            return 0d;
        }
        if (measurements.size() == 1) {
            return sum;
        }
        return (double) sum / measurements.size();
    }

    public List<Measurement> measurements() {
        return measurements;
    }
}
