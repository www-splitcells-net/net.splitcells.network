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
package net.splitcells.dem.environment.resource;

import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.dem.resource.communication.log.IsEchoToFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.resource.communication.Sender.stringSenderWithoutClosing;
import static net.splitcells.dem.resource.Files.createDirectory;

/**
 * TODO Use alternative async backend.
 */
public final class Console extends ResourceOptionI<Sender<String>> {

    public Console() {
        super(() -> {
            if (environment().config().configValue(IsEchoToFile.class)) {
                var consolePath
                        = environment().config().configValue(ProcessPath.class)
                        .resolve("console")
                        .resolve(
                                environment().config().configValue(StartTime.class)
                                        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss.nnnn")));
                createDirectory(consolePath);
                try {
                    return stringSender
                            (new FileOutputStream
                                    (consolePath.resolve("echo.xml")
                                            .toFile()));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return stringSenderWithoutClosing(System.out);
            }
        });
    }

    public static Sender<String> console() {
        return environment().config().configValue(Console.class);
    }
}
