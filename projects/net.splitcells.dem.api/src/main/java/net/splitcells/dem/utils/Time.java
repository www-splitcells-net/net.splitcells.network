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
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.resource.communication.log.LogLevel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

@JavaLegacy
public class Time {
    private Time() {
        throw constructorIllegal();
    }

    public static void reportRuntime(Runnable run, String taskName, LogLevel logLevel) {
        final var startTime = LocalDateTime.now();
        logs().append("Executing `" + taskName + "`.", logLevel);
        run.run();
        final var endTime = LocalDateTime.now();
        logs().append("`" + taskName + "` took " + ChronoUnit.SECONDS.between(startTime, endTime) + " seconds to execute.", logLevel);
    }
}
