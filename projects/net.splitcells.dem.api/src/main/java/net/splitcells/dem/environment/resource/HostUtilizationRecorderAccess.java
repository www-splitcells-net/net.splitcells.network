/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.resource;

import net.splitcells.dem.data.set.list.ListView;

import java.time.Instant;

/**
 * Provides thread safe access to the data of {@link HostUtilizationRecorder}.
 */
public interface HostUtilizationRecorderAccess {
    ListView<Instant> times();

    ListView<Long> maxMemory();
}
