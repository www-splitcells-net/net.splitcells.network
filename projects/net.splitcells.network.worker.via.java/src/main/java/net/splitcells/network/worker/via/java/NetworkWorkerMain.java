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

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.network.worker.via.java.NetworkWorker.networkWorker;

public class NetworkWorkerMain {
    public static void main(String... args) {
        final var options = new Options();
        final var testAtRemote = Option.builder()
                .argName("test-at-remote")
                .hasArg()
                .required(true)
                .longOpt("test-at-remote")
                .desc("This is the ssh address for testing in the form of `username@host`.")
                .build();
        final var bootstrapRemote = Option.builder()
                .argName("bootstrap-remote")
                .hasArg()
                .required(true)
                .longOpt("bootstrap-remote")
                .desc("This is the ssh address for bootstrapping in the form of `username@host`.")
                .build();
        options.addOption(testAtRemote);
        final var parser = new DefaultParser();
        final var formatter = new HelpFormatter();
        try {
            final var cmd = parser.parse(options, args);
            if (cmd.hasOption(testAtRemote)) {
                networkWorker().testAtRemote(cmd.getParsedOptionValue(testAtRemote), c -> c.withDryRun(false));
            } else if (cmd.hasOption(bootstrapRemote)) {
                networkWorker().bootstrapRemote(cmd.getParsedOptionValue(bootstrapRemote), c -> c.withDryRun(false));
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
}
