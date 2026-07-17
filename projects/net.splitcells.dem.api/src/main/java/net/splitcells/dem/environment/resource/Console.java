/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.resource;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.AppendableList;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.communication.Sender;
import net.splitcells.dem.resource.communication.log.IsEchoToFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.resource.communication.Sender.stringSender;
import static net.splitcells.dem.resource.communication.Sender.stringSenderWithoutClosing;
import static net.splitcells.dem.resource.Files.createDirectory;

/**
 * <p>This is the primary basic text output of the program.</p>
 * <p>If {@link IsEchoToFile} is set to true, than the output is echoed to an {@link #CONSOLE_FILE_NAME} file.
 * <p>If {@link IsEchoToFile} is set to false, the output is written to {@link System#out}.</p>
 * <p>The format is like XML, but without the root element and the XML header.
 * In other words, this file is a collection of XML elements,
 * where just appending XML elements without doing anything else to such files results in still valid files.</p>
 * <p>This makes it possible to easily render this file, even though the file is not fully written yet.
 * This is often the case, when the program is still running.</p>
 * <p>TODO Use alternative async backend.</p>
 */
@JavaLegacy
public final class Console extends ResourceOptionImpl<Sender<String>> {

    public static final String CONSOLE_FILE_NAME = "echo.sum.xml";

    public Console() {
        super(() -> {
            final var systemOutSender = stringSenderWithoutClosing(System.out);

            if (environment().config().configValue(IsEchoToFile.class)) {
                var consolePath
                        = Files.usersStateFiles().resolve("src/main/sum.xml")
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
                        public <R extends AppendableList<String>> R append(String arg) {
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
