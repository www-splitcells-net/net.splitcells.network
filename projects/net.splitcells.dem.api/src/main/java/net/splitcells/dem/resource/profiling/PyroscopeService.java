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
package net.splitcells.dem.resource.profiling;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.environment.resource.ResourceOption;
import net.splitcells.dem.environment.resource.Service;

import static net.splitcells.dem.Dem.configValue;

/**
 * Uses Pyroscope in order to do Java profiling.
 */
public class PyroscopeService implements ResourceOption<Service> {
    @Override public Service defaultValue() {
        return new Service() {

            @Override public void flush() {
                // No flush operation is available and needed for Pyroscope.
            }

            @Override public void close() {
                PyroscopeAgent.stop();
            }

            @Override public void start() {
                PyroscopeAgent.start(new Config.Builder()
                        .setApplicationName(configValue(ProgramName.class))
                        .setServerAddress(configValue(PyroscopeServerUrl.class))
                        .setProfilingEvent(EventType.ITIMER)
                        .setFormat(Format.JFR)
                        .build());
            }
        };
    }
}
