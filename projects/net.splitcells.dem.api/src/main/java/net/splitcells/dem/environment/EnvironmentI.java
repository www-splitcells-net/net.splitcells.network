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
package net.splitcells.dem.environment;

import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.environment.config.ProgramLocalIdentity;
import net.splitcells.dem.environment.config.ProgramRepresentative;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.host.ProcessPath;

import static net.splitcells.dem.environment.config.framework.ConfigurationI.configuration;

public class EnvironmentI implements Environment {

    private final Configuration config = configuration();

    public static Environment create(Class<?> programRepresentative) {
        return new EnvironmentI(programRepresentative);
    }

    private EnvironmentI(Class<?> programRepresentative) {
        config.configValue(StartTime.class);
        config.withConfigValue(ProgramRepresentative.class, programRepresentative);
    }

    @Override
    public void init() {
        config.configValue(ProgramLocalIdentity.class);
        config.configValue(IsDeterministic.class);
        config.configValue(ProcessPath.class);
    }

    @Override
    public void start() {
        config.process(Service.class, s -> {
            s.start();
            return s;
        });
    }

    @Override
    public Configuration config() {
        return config;
    }

    @Override
    public void flush() {
        config.process(Flushable.class, r -> {
            r.flush();
            return r;
        });
    }

    @Override
    public void close() {
        config.process
                (Closeable.class
                        , r -> {
                            r.close();
                            return r;
                        });
    }

}
