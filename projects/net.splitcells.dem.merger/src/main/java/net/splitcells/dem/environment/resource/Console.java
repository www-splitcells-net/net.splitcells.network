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

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.ListWA;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.resource.Paths;
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
@JavaLegacyArtifact
public final class Console extends ResourceOptionI<Sender<String>> {

    public static final String CONSOLE_FILE_NAME = "echo.xml";

    public Console() {
        super(() -> {
            final var systemOutSender = stringSenderWithoutClosing(System.out);

            if (environment().config().configValue(IsEchoToFile.class)) {
                var consolePath
                        = Paths.usersStateFiles().resolve("src/main/xml")
                        .resolve(Dem.configValue(ProgramName.class).replace('.', '/'))
                        .resolve("console")
                        .resolve(
                                environment().config().configValue(StartTime.class)
                                        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss.nnnn"))
                                        .replace('.', '/'));
                createDirectory(consolePath);
                try {
                    final var fileOutSender = stringSender
                            (new FileOutputStream
                                    (consolePath.resolve(CONSOLE_FILE_NAME)
                                            .toFile()));
                    return new Sender<>() {

                        @Override
                        public void flush() {
                            systemOutSender.flush();
                            fileOutSender.flush();
                        }

                        @Override
                        public void close() {
                            systemOutSender.close();
                            fileOutSender.close();
                        }

                        @Override
                        public <R extends ListWA<String>> R append(String arg) {
                            systemOutSender.append(arg);
                            return fileOutSender.append(arg);
                        }
                    };
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return systemOutSender;
            }
        });
    }

    public static Sender<String> console() {
        return environment().config().configValue(Console.class);
    }
}
