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

import java.util.Optional;
import java.util.function.Consumer;

public class WorkerExecution implements Consumer<WorkerExecutionConfig> {
    public static WorkerExecution workerExecution() {
        return new WorkerExecution();
    }

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
            remoteExecutionScript = config.shellArgumentString();
        }
    }

    public String remoteExecutionScript() {
        return remoteExecutionScript;
    }
}
