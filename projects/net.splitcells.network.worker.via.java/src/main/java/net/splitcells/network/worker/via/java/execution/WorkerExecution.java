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
import net.splitcells.dem.resource.host.CurrentFileSystem;

import java.util.Optional;
import java.util.function.Consumer;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.host.SystemUtils.executeShellCommand;

public class WorkerExecution {
    public static WorkerExecution workerExecution(WorkerExecutionConfig config) {
        final var workerExecution = new WorkerExecution();
        workerExecution.execute(config);
        return workerExecution;
    }

    private String remoteExecutionScript = "";

    private WorkerExecution() {

    }

    private void execute(WorkerExecutionConfig config) {
        if (config.executeViaSshAt().isPresent()) {
            // `-t` prevents errors, when a command like sudo is executed.
            remoteExecutionScript = "ssh "
                    + config.executeViaSshAt().orElseThrow()
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
            return;
        }
        configValue(CurrentFileSystem.class).createDirectoryPath("./target");
    }

    public String remoteExecutionScript() {
        return remoteExecutionScript;
    }
}
