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

import net.splitcells.network.worker.via.java.execute.WorkerExecution;
import net.splitcells.network.worker.via.java.execute.WorkerExecutionConfig;

import java.util.Optional;

import static net.splitcells.network.worker.via.java.execute.WorkerExecution.workerExecution;
import static net.splitcells.network.worker.via.java.execute.WorkerExecutionConfig.workerExecutionConfig;

public class NetworkWorker {
    public static NetworkWorker networkWorker() {
        return new NetworkWorker();
    }

    private NetworkWorker() {

    }

    public WorkerExecution testAtRemote(String hostname) {
        final var workerExecution = workerExecution();
        workerExecution.accept(workerExecutionConfig("net.splitcells.network.worker")
                .withExecuteViaSshAt(Optional.of(hostname))
                .withCommand(Optional.of("cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap.container"))
                .withAutoConfigureCpuArchExplicitly(true)
        );
        return workerExecution;
    }
}
