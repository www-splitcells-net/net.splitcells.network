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
package net.splitcells.network.worker.via.java.execute;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.Trail;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;

public class ExecuteConfig {
    public static ExecuteConfig executeConfig(String name) {
        return new ExecuteConfig(name);
    }

    private String name;
    private Optional<String> command = Optional.empty();
    private Optional<Trail> executablePath = Optional.empty();
    private Optional<String> classForExecution = Optional.empty();
    private boolean useHostDocuments = false;
    private boolean publishExecutionImage = false;
    private boolean verbose = false;
    private boolean onlyBuildImage = false;
    public boolean onlyExecuteImage = false;
    private Optional<String> cpuArchitecture = Optional.empty();
    private boolean dryRun = false;
    private boolean usePlaywright = false;
    private boolean autoConfigureCpuArchExplicitly = false;
    private List<Integer> portPublishing = list();
    private Optional<String> executeViaSshAt = Optional.empty();

    private ExecuteConfig(String argName) {
        name = argName;
    }

    public ExecuteConfig executeViaSshAt(Optional<String> arg) {
        executeViaSshAt = arg;
        return this;
    }

    public Optional<String> executeViaSshAt() {
        return executeViaSshAt;
    }

    public ExecuteConfig portPublishing(List<Integer> arg) {
        portPublishing = arg;
        return this;
    }

    public List<Integer> portPublishing() {
        return portPublishing;
    }

    public ExecuteConfig autoConfigureCpuArchExplicitly(boolean arg) {
        autoConfigureCpuArchExplicitly = arg;
        return this;
    }

    public boolean autoConfigureCpuArchExplicitly() {
        return autoConfigureCpuArchExplicitly;
    }

    public ExecuteConfig usePlaywright(boolean arg) {
        usePlaywright = arg;
        return this;
    }

    public boolean usePlaywright() {
        return usePlaywright;
    }

    public ExecuteConfig dryRun(boolean arg) {
        dryRun = arg;
        return this;
    }

    public boolean dryRun() {
        return dryRun;
    }

    public ExecuteConfig cpuArchitecture(Optional<String> arg) {
        cpuArchitecture = arg;
        return this;
    }

    public Optional<String> cpuArchitecture() {
        return cpuArchitecture;
    }

    public ExecuteConfig onlyExecuteImage(boolean arg) {
        onlyExecuteImage = arg;
        return this;
    }

    public boolean onlyExecuteImage() {
        return onlyExecuteImage;
    }

    public ExecuteConfig onlyBuildImage(boolean arg) {
        onlyBuildImage = arg;
        return this;
    }

    public boolean onlyBuildImage() {
        return onlyBuildImage;
    }

    public ExecuteConfig verbose(boolean arg) {
        verbose = arg;
        return this;
    }

    public boolean verbose() {
        return verbose;
    }

    public ExecuteConfig publishExecutionImage(boolean arg) {
        publishExecutionImage = arg;
        return this;
    }

    public boolean publishExecutionImage() {
        return publishExecutionImage;
    }

    public ExecuteConfig useHostDocuments(boolean arg) {
        useHostDocuments = arg;
        return this;
    }

    public boolean useHostDocuments() {
        return useHostDocuments;
    }

    public ExecuteConfig classForExecution(Optional<String> arg) {
        classForExecution = arg;
        return this;
    }

    public Optional<String> classForExecution() {
        return classForExecution;
    }

    public Optional<Trail> executablePath() {
        return executablePath;
    }

    public ExecuteConfig withExecutablePath(Optional<Trail> arg) {
        executablePath = arg;
        return this;
    }

    public Optional<String> command() {
        return command;
    }

    public ExecuteConfig withCommand(Optional<String> arg) {
        command = arg;
        return this;
    }

    public String name() {
        return name;
    }
}
