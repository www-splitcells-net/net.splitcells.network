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

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.apache.commons.cli.*;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.network.worker.via.java.execute.ExecuteConfig.executeConfig;

@JavaLegacy
public class ExecuteMain {
    public static void main(String... args) {
        final var options = new Options();
        final var name = Option.builder("n")
                .argName("name")
                .hasArg()
                .required(true)
                .longOpt("name")
                .desc("This is the descriptive name of the task being executed.")
                .build();
        options.addOption(name);
        final var command = Option.builder("c")
                .argName("command")
                .hasArg()
                .required(false)
                .longOpt("command")
                .desc("This is the shell command, to execute the task."
                        + "It consists of only one program call."
                        + "If scripts need to be executed one can use `sh -c \"[script]\"`."
                        + " Use this only, in order to integrate the environment with the software."
                        + " Prefer `--class-for-execution` instead.")
                .build();
        options.addOption(command);
        final var executablePath = Option.builder("executablePath")
                .argName("executablePath")
                .hasArg()
                .required(false)
                .longOpt("executable-path")
                .desc("Executes the given executable file. Only set this option or --command.")
                .build();
        options.addOption(executablePath);
        final var classForExecution = Option.builder("classForExecution")
                .argName("classForExecution")
                .hasArg()
                .required(false)
                .longOpt("class-for-execution")
                .desc("This Java class is executed.")
                .build();
        options.addOption(classForExecution);
        final var useHostDocuments = Option.builder("useHostDocuments")
                .argName("useHostDocuments")
                .hasArg()
                .required(false)
                .longOpt("use-host-documents")
                .desc("Determines whether to mount `~/Documents` or not. This should be avoided, in order to avoid file dependencies to the host system, which makes the execution more portable.")
                .build();
        options.addOption(useHostDocuments);
        final var publishExecutionImage = Option.builder("publishExecutionImage")
                .argName("publishExecutionImage")
                .hasArg()
                .required(false)
                .longOpt("publish-execution-image")
                .desc("If set to true, the given command is not executed, but a container image is published instead.")
                .build();
        options.addOption(publishExecutionImage);
        final var verbose = Option.builder("v")
                .argName("verbose")
                .hasArg()
                .required(false)
                .longOpt("verbose")
                .desc("If set to true, the output is verbose.")
                .build();
        options.addOption(verbose);
        final var onlyBuildImage = Option.builder("onlyBuildImage")
                .argName("onlyBuildImage")
                .hasArg()
                .required(false)
                .longOpt("only-build-image")
                .desc("If set to true, the created image is not executed.")
                .build();
        options.addOption(onlyBuildImage);
        final var onlyExecuteImage = Option.builder("onlyExecuteImage")
                .argName("onlyExecuteImage")
                .hasArg()
                .required(false)
                .longOpt("only-execute-image")
                .desc("If set to true, the previously created image is executed without building it.")
                .build();
        options.addOption(onlyExecuteImage);
        final var cpuArchitecture = Option.builder("cpuArchitecture")
                .argName("cpuArchitecture")
                .hasArg()
                .required(false)
                .longOpt("cpu-architecture")
                .desc("Set the cpu architecture for the execution.")
                .build();
        options.addOption(cpuArchitecture);
        final var dryRun = Option.builder("dryRun")
                .argName("dryRun")
                .hasArg()
                .required(false)
                .longOpt("dry-run")
                .desc("If true, commands are only prepared and no commands are executed.")
                .build();
        options.addOption(dryRun);
        final var usePlaywright = Option.builder("usePlaywright")
                .argName("usePlaywright")
                .hasArg()
                .required(false)
                .longOpt("use-playwright")
                .desc("If true, Playwright is installed for the execution.")
                .build();
        options.addOption(usePlaywright);
        final var autoConfigureCpuArchExplicitly = Option.builder("autoConfigureCpuArchExplicitly")
                .argName("autoConfigureCpuArchExplicitly")
                .hasArg()
                .required(false)
                .longOpt("auto-configure-cpu-architecture-explicitly")
                .desc("If set to false, the command's backend automatically determines the CPU architecture."
                        + " If set to true, the CPU architecture will be determined by this command and the determined architecture is propagated to the commands backend explicitly."
                        + " This is useful, because some tools have not a good CPU auto detection (i.e. Podman on RISC-V cannot find the fitting images based on the CPU arch automatically).")
                .build();
        options.addOption(autoConfigureCpuArchExplicitly);
        final var portPublishing = Option.builder("portPublishing")
                .argName("portPublishing")
                .hasArg()
                .required(false)
                .longOpt("port-publishing")
                .desc("This is a comma separated list of `host-port:container-port`, that describes the port forwarding on the host.")
                .build();
        options.addOption(portPublishing);
        final var executeViaSshAt = Option.builder("executeViaSshAt")
                .argName("executeViaSshAt")
                .hasArg()
                .required(false)
                .longOpt("executeViaSshAt")
                .desc("Execute the given command at an remote server via SSH. The format is `[user]@[address/network name]`.")
                .build();
        options.addOption(executeViaSshAt);

        final var parser = new DefaultParser();
        final var formatter = new HelpFormatter();
        try {
            final var cmd = parser.parse(options, args);
            final var config = executeConfig(cmd.getOptionValue(name))
                    .withCommand(Optional.ofNullable(cmd.getOptionValue(command)))
                    .withExecutablePath(
                            Optional.ofNullable(cmd.getOptionValue(executablePath)).map(ep -> trail(ep)))
                    .withClassForExecution(Optional.ofNullable(cmd.getParsedOptionValue(classForExecution)))
                    .withCpuArchitecture(Optional.ofNullable(cmd.getParsedOptionValue(cpuArchitecture)))
                    .withExecuteViaSshAt(Optional.ofNullable(cmd.getParsedOptionValue(executeViaSshAt)));
            if (cmd.hasOption(useHostDocuments)) {
                config.withUseHostDocuments(cmd.getParsedOptionValue(useHostDocuments));
            }
            if (cmd.hasOption(publishExecutionImage)) {
                config.withPublishExecutionImage(cmd.getParsedOptionValue(publishExecutionImage));
            }
            if (cmd.hasOption(verbose)) {
                config.withVerbose(cmd.getParsedOptionValue(verbose));
            }
            if (cmd.hasOption(onlyBuildImage)) {
                config.withOnlyBuildImage(cmd.getParsedOptionValue(onlyBuildImage));
            }
            if (cmd.hasOption(onlyExecuteImage)) {
                config.withOnlyExecuteImage(cmd.getParsedOptionValue(onlyExecuteImage));
            }
            if (cmd.hasOption(dryRun)) {
                config.withDryRun(cmd.getParsedOptionValue(dryRun));
            }
            if (cmd.hasOption(usePlaywright)) {
                config.withUsePlaywright(cmd.getParsedOptionValue(usePlaywright));
            }
            if (cmd.hasOption(autoConfigureCpuArchExplicitly)) {
                config.withAutoConfigureCpuArchExplicitly(cmd.getParsedOptionValue(autoConfigureCpuArchExplicitly));
            }
            if (cmd.hasOption(portPublishing)) {
                config.withPortPublishing(listWithValuesOf(cmd.getOptionValue(autoConfigureCpuArchExplicitly)
                        .split(","))
                        .mapped(Integers::parse));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            formatter.printHelp("worker.execute", options);
            System.exit(1);
        }
    }

    private ExecuteMain() {

    }
}
