/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.ProcessPath;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.resource.communication.interaction.Pdsui.pdsui;
import static net.splitcells.dem.resource.Files.createDirectory;

/**
 * TODO Log all used {@link LogMessage#path()} to dedicated file.
 *
 * TODO REMOVE This class seems to be a mess and was only used for tests.
 */
@Deprecated
@JavaLegacyArtifact
public class UiRouter implements Ui {

    public static UiRouter uiRouter(Predicate<LogMessage<Perspective>> messageFilter) {
        return new UiRouter(messageFilter);
    }

    private final Map<List<String>, Ui> routing = map();
    private final Predicate<LogMessage<Perspective>> messageFilter;

    private UiRouter(Predicate<LogMessage<Perspective>> messageFilter) {
        this.messageFilter = messageFilter;
    }

    @Override
    public <R extends ListWA<LogMessage<Perspective>>> R append(LogMessage<Perspective> arg) {
        if (messageFilter.test(arg)) {
            if (!routing.containsKey(arg.path())) {
                Path consolePath;
                if (arg.path().size() == 0) {
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
                            , pdsui(stringSender(new FileOutputStream(consolePath.toFile())), messageFilter));
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
        routing.values().forEach(Ui::close);
        routing.clear();
    }

    @Override
    public void flush() {
        routing.values().forEach(Ui::flush);
    }
}
