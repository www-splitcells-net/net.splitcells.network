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
package net.splitcells.network.worker.via.java.execution;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.resource.host.HostFileSystem;
import net.splitcells.dem.resource.host.UserHomeFileSystem;

import java.util.Optional;
import java.util.function.Predicate;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;

public class WorkerExecutionConfig {
    public static WorkerExecutionConfig workerExecutionConfig(String name) {
        return new WorkerExecutionConfig(name);
    }

    private String name;
    private Optional<String> command = Optional.empty();
    private Optional<Trail> executablePath = Optional.empty();
    private Optional<String> classForExecution = Optional.empty();
    private boolean useHostDocuments = false;
    private boolean publishExecutionImage = false;
    private boolean verbose = false;
    private boolean onlyBuildImage = false;
    private boolean onlyExecuteImage = false;
    private Optional<String> cpuArchitecture = Optional.empty();
    /**
     * We do not want to execute shell commands by accident and damage the operating system,
     * which can be hard to detect, when the commands output is overlooked.
     * Not executing shell commands is at least not actively harmful, and
     * it is easier to detect, because some desired effect is missing.
     * If something is not executed by accident and is also not required to be executed
     * than the code is not of a good quality,
     * but at least nothing is being potentially harmed.
     */
    private boolean dryRun = true;
    private boolean usePlaywright = false;
    private boolean autoConfigureCpuArchExplicitly = false;
    private List<Integer> portPublishing = list();
    private Optional<String> executeViaSshAt = Optional.empty();
    private FileSystem fileSystem = configValue(HostFileSystem.class);
    private FileSystem userHome = configValue(UserHomeFileSystem.class);

    /**
     * @param arg Escapes a string,
     *            so it can be placed inside `"[...]"` of a shell script without changing the resulting string,
     *            when compared to {@code arg}.
     * @return Returns {@code arg} as an escaped string.
     */
    private String escape(String arg) {
        return "'"
                + arg.replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "")
                + "'";
    }

    public String shellArgumentString() {
        return shellArgumentString(a -> true);
    }

    public String shellArgumentString(Predicate<String> optionSelector) {
        var result = stringBuilder();
        result.append("--name=" + escape(name));
        if (optionSelector.test("command")) {
            command.ifPresent(c -> result.append(" --command=" + escape(c)));
        }
        if (optionSelector.test("execute-via-ssh-at")) {
            executeViaSshAt.ifPresent(e -> result.append(" --execute-via-ssh-at=" + escape(e)));
        }
        if (optionSelector.test("executable-path")) {
            executablePath.ifPresent(t -> result.append(" --executable-path=" + escape(t.unixPathString())));
        }
        if (optionSelector.test("class-for-execution")) {
            classForExecution.ifPresent(c -> result.append(" --class-for-execution=" + escape(c)));
        }
        if (optionSelector.test("cpu-architecture")) {
            cpuArchitecture.ifPresent(c -> result.append(" --cpu-architecture=" + escape(c + "")));
        }
        if (optionSelector.test("use-host-documents")) {
            result.append(" --use-host-documents=" + escape(useHostDocuments + ""));
        }
        if (optionSelector.test("publish-execution-image")) {
            result.append(" --publish-execution-image=" + escape(publishExecutionImage + ""));
        }
        if (optionSelector.test("verbose")) {
            result.append(" --verbose=" + escape(verbose + ""));
        }
        if (optionSelector.test("only-build-image")) {
            result.append(" --only-build-image=" + escape(onlyBuildImage + ""));
        }
        if (optionSelector.test("only-execute-image")) {
            result.append(" --only-execute-image=" + escape(onlyExecuteImage + ""));
        }
        if (optionSelector.test("dry-run")) {
            result.append(" --dry-run=" + escape(dryRun + ""));
        }
        if (optionSelector.test("use-playwright")) {
            result.append(" --use-playwright=" + escape(usePlaywright + ""));
        }
        if (optionSelector.test("auto-configure-cpu-architecture-explicitly")) {
            result.append(" --auto-configure-cpu-architecture-explicitly=" + escape(autoConfigureCpuArchExplicitly + ""));
        }
        return result.toString();
    }

    private WorkerExecutionConfig(String argName) {
        name = argName;
    }

    public WorkerExecutionConfig withFileSystem(FileSystem arg) {
        fileSystem = arg;
        return this;
    }

    public FileSystem fileSystem() {
        return fileSystem;
    }

    public WorkerExecutionConfig withExecuteViaSshAt(Optional<String> arg) {
        executeViaSshAt = arg;
        return this;
    }

    public Optional<String> executeViaSshAt() {
        return executeViaSshAt;
    }

    public WorkerExecutionConfig withPortPublishing(List<Integer> arg) {
        portPublishing = arg;
        return this;
    }

    public List<Integer> portPublishing() {
        return portPublishing;
    }

    public WorkerExecutionConfig withAutoConfigureCpuArchExplicitly(boolean arg) {
        autoConfigureCpuArchExplicitly = arg;
        return this;
    }

    public boolean autoConfigureCpuArchExplicitly() {
        return autoConfigureCpuArchExplicitly;
    }

    public WorkerExecutionConfig withUsePlaywright(boolean arg) {
        usePlaywright = arg;
        return this;
    }

    public boolean usePlaywright() {
        return usePlaywright;
    }

    public WorkerExecutionConfig withDryRun(boolean arg) {
        dryRun = arg;
        return this;
    }

    public boolean dryRun() {
        return dryRun;
    }

    public WorkerExecutionConfig withCpuArchitecture(Optional<String> arg) {
        cpuArchitecture = arg;
        return this;
    }

    public Optional<String> cpuArchitecture() {
        return cpuArchitecture;
    }

    public WorkerExecutionConfig withOnlyExecuteImage(boolean arg) {
        onlyExecuteImage = arg;
        return this;
    }

    public boolean onlyExecuteImage() {
        return onlyExecuteImage;
    }

    public WorkerExecutionConfig withOnlyBuildImage(boolean arg) {
        onlyBuildImage = arg;
        return this;
    }

    public boolean onlyBuildImage() {
        return onlyBuildImage;
    }

    public WorkerExecutionConfig withVerbose(boolean arg) {
        verbose = arg;
        return this;
    }

    public boolean verbose() {
        return verbose;
    }

    public WorkerExecutionConfig withPublishExecutionImage(boolean arg) {
        publishExecutionImage = arg;
        return this;
    }

    public boolean publishExecutionImage() {
        return publishExecutionImage;
    }

    public WorkerExecutionConfig withUseHostDocuments(boolean arg) {
        useHostDocuments = arg;
        return this;
    }

    public boolean useHostDocuments() {
        return useHostDocuments;
    }

    public WorkerExecutionConfig withClassForExecution(Optional<String> arg) {
        classForExecution = arg;
        return this;
    }

    public Optional<String> classForExecution() {
        return classForExecution;
    }

    public Optional<Trail> executablePath() {
        return executablePath;
    }

    public WorkerExecutionConfig withExecutablePath(Optional<Trail> arg) {
        executablePath = arg;
        return this;
    }

    public Optional<String> command() {
        return command;
    }

    public WorkerExecutionConfig withCommand(Optional<String> arg) {
        command = arg;
        return this;
    }

    public String name() {
        return name;
    }

    public FileSystem userHome() {
        return userHome;
    }

    public WorkerExecutionConfig withUserHome(FileSystem arg) {
        userHome = arg;
        return this;
    }
}
