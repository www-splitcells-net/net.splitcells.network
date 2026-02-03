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
package net.splitcells.dem.resource.host;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.resource.communication.log.LogLevel;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.host.ShellResult.shellResult;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * TODO Use only {@link #executeShellCommand(String)}.
 * The third argument is the sh script to be executed.
 * The other functions do not have access to the same environment, as the user's default shell.
 * For instance, the PATH variable seems to be different.
 */
@JavaLegacy
public final class SystemUtils {
    private SystemUtils() {
        throw constructorIllegal();
    }

    @Deprecated
    public static ShellResult runShellScript(String command, Path workingDirectory) {
        final var rVal = new StringBuilder();
        final Process process;
        try {
            process = Runtime.getRuntime().exec
                    (command
                            , new String[0]
                            , workingDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            // FIXME Print Process output while waiting for process's completion.
            String inputLine = null;
            String errorLine = null;
            try {
                // Some commands wait for input although they do not needed to.
                process.getOutputStream().close();
                while ((inputLine = inputReader.readLine()) != null || (errorLine = errorReader.readLine()) != null) {
                    if (errorLine != null) {
                        rVal.append(errorLine);
                    }
                    if (inputLine != null) {
                        rVal.append(inputLine);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            process.waitFor();

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return shellResult(process.exitValue(), rVal.toString());
    }

    @Deprecated
    public static void executeShellScript(String command, Path workingDirectory) {
        // REMOVE or write output to log.
        System.out.println(command);
        final Process process;
        try {
            process = Runtime.getRuntime().exec
                    (command
                            , new String[0]
                            , workingDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            // FIXME Print Process output while waiting for process's completion.
            String inputLine = null;
            String errorLine = null;
            try {
                // Some commands wait for input although they do not needed to.
                process.getOutputStream().close();
                while ((inputLine = inputReader.readLine()) != null || (errorLine = errorReader.readLine()) != null) {

                    if (errorLine != null) {
                        System.out.println(errorLine);
                    }
                    if (inputLine != null) {
                        System.out.println(inputLine);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static void executeProgram(List<String> command, Path workingDirectory) {
        // REMOVE or write output to log.
        System.out.println(command);
        final Process process;
        try {
            process = Runtime.getRuntime().exec
                    (command.toArray(new String[command.size()])
                            , new String[0]
                            , workingDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            // FIXME Print Process output while waiting for process's completion.
            String inputLine = null;
            String errorLine = null;
            try {
                // Some commands wait for input although they do not needed to.
                process.getOutputStream().close();
                while ((inputLine = inputReader.readLine()) != null || (errorLine = errorReader.readLine()) != null) {

                    if (errorLine != null) {
                        System.out.println(errorLine);
                    }
                    if (inputLine != null) {
                        System.out.println(inputLine);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            process.waitFor();
        } catch (Throwable e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static void executeProgram(String... command) {
        logs().append(tree("Executing program.").withChild(tree(Arrays.toString(command))), LogLevel.DEBUG);
        final Process process;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            // FIXME Print Process output while waiting for process's completion.
            new Thread(() -> {
                String inputLine = null;
                while (process.isAlive()) {
                    try {
                        while ((inputLine = inputReader.readLine()) != null) {
                            System.out.println(inputLine);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
            new Thread(() -> {
                String errorLine = null;
                while (process.isAlive()) {
                    try {
                        while ((errorLine = errorReader.readLine()) != null) {
                            System.err.println(errorLine);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
            process.waitFor();
        } catch (Throwable e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static void executeShellCommand(String script) {
        executeProgram("sh", "-c", script);
    }

    /**
     * REMOVE
     * <p>
     * This is a general tactic in order to execute shell scripts:
     * https://stackoverflow.com/questions/26830617/java-running-bash-commands
     *
     * @return
     */
    @Deprecated
    private static File createTempScript(String script) {
        File tempScript;
        try {
            tempScript = File.createTempFile("script", null);
            try (Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript))) {
                PrintWriter printWriter = new PrintWriter(streamWriter);

                printWriter.println("#!/bin/bash");
                printWriter.println(script);

                printWriter.close();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return tempScript;
    }

}
