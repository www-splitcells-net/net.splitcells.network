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

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.host.CurrentFileSystem;
import net.splitcells.dem.utils.StringUtils;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.resource.Files.copyFileFrom;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.host.SystemUtils.executeShellCommand;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;

/**
 * <p>Executes something based on the given {@link WorkerExecutionConfig}.
 * This class only stores the result of the execution.
 * Any input is handled by {@link WorkerExecutionConfig}.</p>
 * <p>Strings instead of {@link StringBuilder} are used, so that replace methods can be used.</p>
 */
public class WorkerExecution {
    public static WorkerExecution workerExecution(WorkerExecutionConfig config) {
        final var workerExecution = new WorkerExecution();
        workerExecution.execute(config);
        return workerExecution;
    }

    private static final Trail PODMAN_FLAGS_CONFIG_FILES = trail(".config/net.splitcells.network.worker/execute.podman.flags");

    private static final String DOCKERFILE_SERVICE_TEMPLATE = """
            FROM docker.io/eclipse-temurin:21-jdk-noble
            RUN apt-get clean
            RUN apt-get update # This fixes install errors. It is unknown why this is the case.
            RUN apt-get install --yes maven git python3 pip
            ADD net.splitcells.network.worker.pom.xml /root/opt/$NAME_FOR_EXECUTION/pom.xml
            # RUN pip install --break-system-packages playwright
            # RUN playwright install --with-deps firefox # Install all OS dependencies, that are required for Playwright. Otherwise, Playwright cannot be used in Java.
            # RUN cd /root/opt/$NAME_FOR_EXECUTION/ && mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"
            $ContainerSetupCommand
            VOLUME /root/.local/
            VOLUME /root/Documents/
            VOLUME /root/.ssh/
            VOLUME /root/.m2/
            """;

    private static final String POM = """
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                <groupId>net.splitcells</groupId>
                <artifactId>network.worker.container.pom</artifactId>
                <version>1.0-SNAPSHOT</version>
                <dependencies>
                    <dependency>
                        <groupId>com.microsoft.playwright</groupId>
                        <artifactId>playwright</artifactId>
                        <version>1.45.0</version>
                    </dependency>
                </dependencies>
            </project>
            """;

    private static final String JAVA_CLASS_EXECUTION_TEMPLATE = """
            COPY deployable-jars/* /root/opt/$NAME_FOR_EXECUTION/jars/
            WORKDIR /root/opt/$NAME_FOR_EXECUTION/
            ENTRYPOINT ["/opt/java/openjdk/bin/java"]
            CMD ["-XX:ErrorFile=/root/.local/state/$NAME_FOR_EXECUTION/.local/dumps/hs_err_pid_%p.log", "-cp", "./jars/*", "$CLASS_FOR_EXECUTION"]
            """;

    private static final String PREPARE_EXECUTION_TEMPLATE = """
            set -e
            set -x
            executionName="$executionName"
            executionCommand="$executionCommand"
            # Prepare file system.
            mkdir -p $HOME/.local/state/$executionName/.m2/
            mkdir -p $HOME/.local/state/$executionName/.ssh/
            mkdir -p $HOME/.local/state/$executionName/.local/dumps
            mkdir -p $HOME/.local/state/$executionName/Documents/
            mkdir -p ./target/
            test -f target/program-$executionName && chmod +x target/program-$executionName # This file does not exist, when '--executable-path' is not set.
            podman build -f "target/Dockerfile-$executionName" \\
                --tag "localhost/$executionName"  \\
                --arch string \\
                $additionalArguments \\
                --log-level=warn # `--log-level=warn` is podman's default.
                # Logging is used, in order to better understand build runtime performance.
            """;

    private static final String PREPARE_EXECUTION_WITHOUT_BUILD_TEMPLATE = """
            set -e
            set -x
            executionName="$executionName"
            # TODO executionCommand is currently not used.
            executionCommand="$executionCommand"
            # Prepare file system.
            mkdir -p $HOME/.local/state/$executionName/.m2/
            mkdir -p $HOME/.local/state/$executionName/.ssh/
            mkdir -p $HOME/.local/state/$executionName/.local/
            mkdir -p $HOME/.local/state/$executionName/Documents/
            mkdir -p ./target/
            test -f target/program-$executionName && chmod +x target/program-$executionName # This file does not exist, when '--executable-path' is not set.
            """;

    private static final String EXECUTE_VIA_PODMAN_TEMPLATE = """
            set -x
            podman run --name "$executionName" \\
              --network slirp4netns:allow_host_loopback=true \\
              $additionalArguments \\
              --rm \\
              -v $HOME/.local/state/$executionName/Documents:/root/Documents \\
              -v $HOME/.local/state/$executionName/.ssh:/root/.ssh \\
              -v $HOME/.local/state/$executionName/.m2:/root/.m2 \\
              -v $HOME/.local/state/$executionName/.local:/root/.local \\
              "$podmanParameters" \\
              "localhost/$executionName"
              #
              # allow_host_loopback is required, so that the software in the container can connect to the host.
            """;

    private static final String PUBLISH_VIA_PODMAN_TEMPLATE = """
            podman tag $executionName:latest codeberg.org/splitcells-net/$executionName:latest
            podman push codeberg.org/splitcells-net/$executionName:latest
            """;

    private boolean wasExecuted = false;
    private String remoteExecutionScript = "";
    private String dockerfile = "";
    private String dockerFilePath = "";
    private String programName = "";

    private WorkerExecution() {

    }

    private void execute(WorkerExecutionConfig config) {
        if (wasExecuted) {
            throw execException(getClass().getSimpleName() + " instance cannot be executed twice.");
        }
        wasExecuted = true;
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
                executeShellCommand(remoteExecutionScript.toString());
            }
            return;
        }
        configValue(CurrentFileSystem.class).createDirectoryPath("./target");
        dockerfile = DOCKERFILE_SERVICE_TEMPLATE;
        if (config.command().isPresent()) {
            dockerfile += "ENTRYPOINT " + config.command().get();
        } else if (config.executablePath().isPresent()) {
            programName = "program-" + config.name();
            copyFileFrom(Path.of(config.executablePath().get().unixPathString()), Path.of("./target/" + programName.get()));
            dockerfile += "ADD ./" + programName + " /root/program\n";
            dockerfile += "ENTRYPOINT /root/program";
        } else {
            throw execException("Either `--command`, `--executable-path` or `--class-for-execution` needs to be set.");
        }
        if (config.usePlaywright()) {
            dockerfile = dockerfile.replace("$ContainerSetupCommand", "RUN cd /root/opt/$NAME_FOR_EXECUTION/ && mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args=\"install-deps\"\n");
        } else {
            dockerfile = dockerfile.replace("$ContainerSetupCommand", "\n");
        }
        dockerfile = dockerfile.replace("$NAME_FOR_EXECUTION", config.name());
        dockerFilePath = "target/Dockerfile-" + config.name();
    }

    public String remoteExecutionScript() {
        return remoteExecutionScript;
    }

    public String dockerfile() {
        return dockerfile;
    }

    public String programName() {
        return programName;
    }
}
