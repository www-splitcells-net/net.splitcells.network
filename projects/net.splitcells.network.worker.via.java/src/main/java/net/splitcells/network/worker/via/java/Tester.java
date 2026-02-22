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
package net.splitcells.network.worker.via.java;

import lombok.val;
import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.resource.host.HostName;
import net.splitcells.network.worker.via.java.repo.ProjectsFolder;

import java.time.LocalDate;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.testing.Test.testFunctionality;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.network.worker.via.java.Logger.logger;

/**
 * <p>Executes the standard tests and writes the results into the network log repo.
 * This is the minimal amount of tests, that should be done, before something is released.</p>
 * <p>Maven's Surefire plugin is not suited for that,
 * as setting a custom test execution listener via Maven is complicated,
 * which is required for writing the test results into the network log.
 * Therefore, a dedicated main function is used.</p>
 * <p>TODO Check environment variable NET_SPLITCELLS_PROJECT_REPOS_PATH and
 * if set, use this for {@link ProjectsFolder}.</p>
 */
public class Tester {
    public static void main(String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Exactly one argument is required, but " + args.length + " were given. The argument is the id of the test executor.");
        }
        test(args[0], ".local/state/net.splitcells.network.worker/repos/public/");
    }
    
    public static void test(String hostname, String projectsFolder) {
        Dem.process(() -> {
                    val logger = logger();
                    val executionSuccess = testFunctionality(list(logger));
                    final double executionResult;
                    if (executionSuccess) {
                        executionResult = 1d;
                    } else {
                        executionResult = 0d;
                    }
                    logger.logExecutionResults(Tester.class
                            , "execution"
                            , config().configValue(HostName.class)
                            , LocalDate.now()
                            , "tester-success"
                            , executionResult);
                }
                , env -> {
                    env.config()
                            .withConfigValue(ProgramName.class, hostname)
                            .withConfigValue(ProjectsFolder.class, userHome(projectsFolder))
                            .withConfigValue(NetworkWorkerLogFileSystem.class
                                    , fileSystemOnLocalHost(config()
                                            .configValue(ProjectsFolder.class)
                                            .resolve("net.splitcells.network.log")));
                });
    }

    private Tester() {
        throw constructorIllegal();
    }
}
