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
package net.splitcells.dem.resource.host;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.dem.utils.ConstructorIllegal;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.resource.host.ShellResult.shellResult;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

@JavaLegacyArtifact
public final class SystemUtils {
    public SystemUtils() {
        throw constructorIllegal();
    }

    @JavaLegacyBody
    public static ShellResult runShellScript(String command, Path workingDirectory) {
        final var rVal = new StringBuffer();
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

    @JavaLegacyBody
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
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

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

    public static void executeProgram(String... command) {
        domsole().append(perspective("Executing program.").withChild(perspective(Arrays.toString(command))), LogLevel.DEBUG);
        final Process process;
        try {
            process = Runtime.getRuntime().exec(command);
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

    public static void executeShellCommand(String script) {
        executeProgram("/bin/bash", "-c", script);
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
