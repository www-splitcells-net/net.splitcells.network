/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.environment.resource;

import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.resource.communication.Sender.stringSenderWithoutClosing;
import static net.splitcells.dem.resource.host.Files.createDirectory;

/**
 * TODO Use alternative async backend.
 */
public final class Console extends ResourceI<Sender<String>> {

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
