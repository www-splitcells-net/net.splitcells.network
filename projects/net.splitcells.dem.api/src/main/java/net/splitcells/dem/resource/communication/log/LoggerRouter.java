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
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.AppendableList;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.host.ProcessPath;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.resource.communication.log.LoggerImpl.logBasedOnPerspective;
import static net.splitcells.dem.resource.Files.createDirectory;

/**
 * TODO Log all used {@link LogMessage#path()} to dedicated file.
 *
 * TODO REMOVE This class seems to be a mess and was only used for tests.
 * This class is used in order to save statistics.
 * Unfortunately, this class is over specific to this task.
 * Instead the following is required: A Logger object,
 * with a default logger,
 * that allows one to inject log message interceptors.
 */
@Deprecated
@JavaLegacy
public class LoggerRouter implements Logger {

    public static LoggerRouter uiRouter(Predicate<LogMessage<Tree>> messageFilter) {
        return new LoggerRouter(messageFilter);
    }

    private final Map<List<String>, Logger> routing = map();
    private final Predicate<LogMessage<Tree>> messageFilter;

    private LoggerRouter(Predicate<LogMessage<Tree>> messageFilter) {
        this.messageFilter = messageFilter;
    }

    @Override
    public <R extends AppendableList<LogMessage<Tree>>> R append(LogMessage<Tree> arg) {
        if (messageFilter.test(arg)) {
            if (!routing.containsKey(arg.path())) {
                Path consolePath;
                if (arg.path().isEmpty()) {
                    consolePath = environment().config().configValue(ProcessPath.class)
                            .resolve("src")
                            .resolve("main")
                            .resolve("csv");
                    createDirectory(consolePath);
                    consolePath = consolePath.resolve
                            (environment()
                                    .config()
                                    .configValue(StartTime.class)
                                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss.nnnn")));
                } else if (arg.path().size() == 1) {
                    consolePath = environment().config().configValue(ProcessPath.class)
                            .resolve("src")
                            .resolve("main")
                            .resolve("csv");
                    createDirectory(consolePath);
                    // TODO HACK File Suffix
                    consolePath = consolePath.resolve(arg.path().get(0) + ".csv");
                } else {
                    final var filePath = Lists.listWithValuesOf(arg.path());
                    // TODO HACK File Suffix
                    final var file = filePath.remove(filePath.size() - 1) + ".csv";
                    consolePath = environment().config().configValue(ProcessPath.class)
                            .resolve("src")
                            .resolve("main")
                            .resolve("csv");
                    for (String e : filePath) {
                        consolePath = consolePath.resolve(e);
                    }
                    createDirectory(consolePath);
                    consolePath = consolePath.resolve(file);
                }
                try {
                    routing.put(arg.path()
                            , logBasedOnPerspective(stringSender(new FileOutputStream(consolePath.toFile())), messageFilter));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(arg.path().toString(), e);
                }
            }
            routing.get(arg.path()).append(arg);
        }
        return (R) this;
    }

    @Override
    public void close() {
        routing.values().forEach(Logger::close);
        routing.clear();
    }

    @Override
    public void flush() {
        routing.values().forEach(Logger::flush);
    }
}
