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
import net.splitcells.dem.environment.config.framework.ConfigDependencyRecording;
import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.host.ProcessPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.splitcells.dem.environment.config.framework.ConfigurationImpl.configuration;
import static net.splitcells.dem.resource.communication.log.Logs.logs;

public class EnvironmentI implements Environment {

    private final Configuration config = configuration();
    private final List<Class<? extends Cell>> dependencyCellStack = new ArrayList<>();

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
            logs().append("Starting `" + s.getClass().getName() + "` service.", LogLevel.DEBUG);
            s.start();
            return s;
        });
    }

    @Override
    public Configuration config() {
        return config;
    }

    @Override
    public <T extends Cell> Environment withCell(Class<T> clazz, Consumer<T> cellConsumer) {
        try {
            dependencyCellStack.add(clazz);
            if (dependencyCellStack.size() > 1) {
                config.configValue(ConfigDependencyRecording.class).recordDependency(dependencyCellStack.get(dependencyCellStack.size() - 2)
                        , dependencyCellStack.get(dependencyCellStack.size() - 1));
            }
            final var cell = config().withInitedOption(clazz).configValue(clazz);
            cell.accept(this);
            cellConsumer.accept((T) cell);
        } finally {
            dependencyCellStack.remove(dependencyCellStack.size() - 1);
        }
        return this;
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
        config.process(Closeable.class, r -> {
            logs().append("Closing `" + r.getClass().getName() + "`.", LogLevel.DEBUG);
            r.close();
            return r;
        });
    }

}
