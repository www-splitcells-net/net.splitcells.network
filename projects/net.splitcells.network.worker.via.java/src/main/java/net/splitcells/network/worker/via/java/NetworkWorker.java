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

import net.splitcells.network.worker.via.java.execution.WorkerExecution;
import net.splitcells.network.worker.via.java.execution.WorkerExecutionConfig;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.network.worker.via.java.execution.WorkerExecution.workerExecution;
import static net.splitcells.network.worker.via.java.execution.WorkerExecutionConfig.workerExecutionConfig;

public class NetworkWorker {

    public static void main(String... args) {
        final var options = new Options();
        final var testAtRemote = Option.builder()
                .argName("test-at-remote")
                .hasArg()
                .required(false)
                .longOpt("test-at-remote")
                .desc("This is the ssh address for testing in the form of `username@host`.")
                .build();
        final var bootstrapRemote = Option.builder()
                .argName("bootstrap-remote")
                .hasArg()
                .required(false)
                .longOpt("bootstrap-remote")
                .desc("This is the ssh address for bootstrapping in the form of `username@host`.")
                .build();
        options.addOption(testAtRemote);
        options.addOption(bootstrapRemote);
        final var parser = new DefaultParser();
        final var formatter = new HelpFormatter();
        try {
            final var cmd = parser.parse(options, args);
            if (cmd.hasOption(testAtRemote)) {
                networkWorker().testAtRemote(cmd.getParsedOptionValue(testAtRemote), c -> c.withDryRun(false));
            } else if (cmd.hasOption(bootstrapRemote)) {
                networkWorker().bootstrapRemote(cmd.getParsedOptionValue(bootstrapRemote), c -> c.withDryRun(false).withVerbose(true));
            } else {
                logs().append("No action to be done is present in the arguments.");
                System.exit(1);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            formatter.printHelp("worker", options);
            System.exit(1);
        }
    }

    public static NetworkWorker networkWorker() {
        return new NetworkWorker();
    }

    private NetworkWorker() {

    }

    public WorkerExecution bootstrapRemote(String hostname) {
        return bootstrapRemote(hostname, a -> a);
    }

    public WorkerExecution bootstrapRemote(String hostname, Function<WorkerExecutionConfig, WorkerExecutionConfig> defaultConfig) {
        final var config = defaultConfig.apply(workerExecutionConfig("net.splitcells.network.worker"))
                .withExecuteViaSshAt(Optional.of(hostname))
                .withCommand(Optional.of("cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap"))
                .withAutoConfigureCpuArchExplicitly(true);
        return workerExecution(config);
    }

    public WorkerExecution testAtRemote(String hostname) {
        return testAtRemote(hostname, a -> a);
    }

    public WorkerExecution testAtRemote(String hostname, Function<WorkerExecutionConfig, WorkerExecutionConfig> defaultConfig) {
        final var config = defaultConfig.apply(workerExecutionConfig("net.splitcells.network.worker"))
                .withExecuteViaSshAt(Optional.of(hostname))
                .withCommand(Optional.of("cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap && bin/repos.build"))
                .withAutoConfigureCpuArchExplicitly(true);
        return workerExecution(config);
    }
}
