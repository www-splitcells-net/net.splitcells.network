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

import net.splitcells.dem.resource.communication.log.LogLevel;

import java.util.Optional;
import java.util.function.Consumer;

import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.host.SystemUtils.executeShellCommand;

public class WorkerExecution implements Consumer<WorkerExecutionConfig> {
    public static WorkerExecution workerExecution() {
        return new WorkerExecution();
    }

    /**
     * We do not want to execute shell commands by accident and damage the operating system,
     * which can be hard to detect, when the commands output is overlooked.
     * Not executing shell commands is at least not actively harmful, and
     * it is easier to detect, because some desired effect is missing.
     * If something is not executed by accident and is also not required to be executed
     * than the code is not of a good quality,
     * but at least nothing is being potentially harmed.
     */
    private Optional<Boolean> dryRun = Optional.of(true);
    private String remoteExecutionScript = "";

    private WorkerExecution() {

    }

    public WorkerExecution withDryRun(Optional<Boolean> arg) {
        dryRun = arg;
        return this;
    }

    @Override
    public void accept(WorkerExecutionConfig config) {
        dryRun.ifPresent(config::withDryRun);
        if (config.executeViaSshAt().isPresent()) {
            remoteExecutionScript = "ssh "
                    + config.executeViaSshAt().get()
                    + " -t"
                    + " \"cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.execute "
                    + config.shellArgumentString(a -> !"execute-via-ssh-at".equals(a))
                    + "\"";
            if (config.verbose()) {
                logs().append("Executing: " + remoteExecutionScript, INFO);
            }
            if (!config.dryRun()) {
                executeShellCommand(remoteExecutionScript);
            }
        }
    }

    public String remoteExecutionScript() {
        return remoteExecutionScript;
    }
}
