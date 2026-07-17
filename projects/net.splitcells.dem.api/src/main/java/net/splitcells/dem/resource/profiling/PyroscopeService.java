/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * Uses Pyroscope in order to do Java profiling.
 */
@JavaLegacy
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

    @Override public Optional<Tree> serialize(Service currentValue) {
        return Optional.of(tree(getClass() + " is enabled."));
    }
}
