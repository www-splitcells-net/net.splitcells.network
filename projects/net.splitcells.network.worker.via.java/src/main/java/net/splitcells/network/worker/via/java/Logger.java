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

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.Files;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.resource.ContentType.CSV;
import static net.splitcells.dem.resource.communication.log.LogLevel.WARNING;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.network.worker.via.java.repo.Repositories.repository;

/**
 * <p>Logs the runtime of tests into a project folder
 * and commits these logs.
 * It is assumed, that the folder of the project is under version control.</p>
 * <p>The entries are stored at "./src/main/csv/**".
 * The relative path of each entry represents the package, the test case and the executor of the test:
 * "./src/main/csv/&lt;package&gt;/&lt;test case&gt;/&lt;id of the executor&gt;.csv".
 * The executors hardware and software should not change between each run,
 * in order to make it easily possible to find regressions.</p>
 * <p>Each CSV file contains the 2 columns "Date" and "Execution Time".
 * The first one has the executions date of the test case.
 * The second one contains the execution time for the corresponding test case.</p>
 * <p>{@link #BUILDER_RUNTIME_LOG}"/&lt;host&gt;.csv" contains the start times of all runs for a given host.
 * </p>
 */
@JavaLegacy
public class Logger implements TestExecutionListener {

    public static Logger logger() {
        return logger(config().configValue(NetworkWorkerLogFileSystem.class));
    }

    private static Logger logger(FileSystem networkProject) {
        return new Logger(networkProject);
    }

    private static final String BUILDER_RUNTIME_LOG = "net/splitcells/network/logger/builder/runtime";

    private final FileSystem logProject;
    private final Map<TestIdentifier, Long> testToStartTime = map();

    private Logger(FileSystem logProject) {
        this.logProject = logProject;
    }

    public void logExecutionResults(String subject, String executor, LocalDate localDate, String resultType
            , double result) {
        final var projectFolder = "src/main/" + CSV.codeName() + "/" + subject + "/";
        logProject.createDirectoryPath(projectFolder);
        final var projectPath = projectFolder + executor + "." + CSV.codeName();
        if (!logProject.isFile(projectPath)) {
            logProject.writeToFile(Path.of(projectPath)
                    , ("Date," + resultType + Files.newLine()).getBytes(StandardCharsets.UTF_8));
        }
        logProject.appendToFile(Path.of(projectPath)
                , (localDate + "," + result + Files.newLine()).getBytes(StandardCharsets.UTF_8));
    }

    public String readExecutionResults(String subject, String executor) {
        final var projectFolder = "src/main/" + CSV.codeName() + "/" + subject + "/";
        logProject.createDirectoryPath(projectFolder);
        return logProject.readString(projectFolder + executor + "." + CSV.codeName());
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        testToStartTime.put(testIdentifier, System.nanoTime());
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        final var testPath = parseTestIdentifier(testIdentifier.getUniqueId());
        if (testPath.isPresent()) {
            final var endDateTime = System.nanoTime();
            final var startDateTime = testToStartTime.get(testIdentifier);
            final var runTime = (endDateTime - startDateTime) / 1_000_000_000d;
            logExecutionResults(testPath.get()
                    , config().configValue(ProgramName.class)
                    , LocalDate.now()
                    , "Execution Time"
                    , runTime);
        }

    }

    protected Optional<String> parseTestIdentifier(String testIdentifier) {
        final var splitTestIdentifier = list(testIdentifier.split("/"));
        final Optional<String> testPath;
        if (splitTestIdentifier.size() == 1) {
            testPath = Optional.of(BUILDER_RUNTIME_LOG);
        } else {
            testPath = splitTestIdentifier
                    .withRemovedByIndex(0)
                    .stream()
                    .map(e -> e.replace("()", ""))
                    .map(e -> e.replace("[", ""))
                    .map(e -> e.replace("]", ""))
                    .map(e -> e.split(":")[1])
                    .map(e -> e.replace(".", "/"))
                    .map(e -> e.replaceAll("[^a-zA-Z-_/1-8]", "_"))
                    .reduce((a, b) -> a + "/" + b)
                    .map(e -> e.replaceAll("/+", "/"));
        }
        return testPath;
    }

    public void commit() {
        logProject.javaLegacyPath().ifPresentOrElse(path -> repository(path).commitAll()
                , () -> logs().append("Persistence for network log not supported.", WARNING));
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        commit();
    }

}
