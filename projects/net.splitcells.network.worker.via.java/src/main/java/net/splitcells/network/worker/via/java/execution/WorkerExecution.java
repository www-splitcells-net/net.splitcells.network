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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.host.CurrentFileSystem;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.host.SystemUtils.executeShellCommand;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.toBytes;

/**
 * <p>Executes something based on the given {@link WorkerExecutionConfig}.
 * This class only stores the result of the execution.
 * Any input is handled by {@link WorkerExecutionConfig}.</p>
 * <p>Strings instead of {@link StringBuilder} are used, so that replace methods can be used.</p>
 * <p>sh is used explicitly instead of the default shell, because on many servers custom shells like fish are used,
 * that have different shell syntaxes, but provide good UI features for users.</p>
 * <p>The implementation tries to create one single script for the shell execution,
 * in order to simplify the overview and ease the understanding of the shell commands.
 * Thereby, one could even store the generated scripts and execute these without using Java at all,
 * which makes the hole thing more portable as well.</p>
 * <p>TODO IDEA Currently, everything is stored in `$HOME/.local/state/$(executionName)/*`.
 * If more strict file isolation is required, in order to prevent file accidents,
 * a namespace could be used, that is implemented as a hidden parent folder for the execution folder:
 * `$HOME/.local/state/.$namespace/$(executionName)/*`.
 * See `repo.process` for inspiration.
 * Of course, different users could be used instead.</p>
 * <p>TODO The way, that the execution strings are created is too complex.
 * From the feature complexity it should be a relatively simple string templating like $NAME_FOR_EXECUTION and
 * no arbitrary replacement of strings in the template should be done,
 * like it is on `-v $HOME/.local/state/$(executionName)/Documents:/root/Documents`.
 * In other words, separate the configuration via {@link WorkerExecutionConfig} more strictly from the template processing.</p>
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
            executionName="$(executionName)"
            executionCommand="$executionCommand"
            # Prepare file system.
            mkdir -p $HOME/.local/state/$(executionName)/.m2/
            mkdir -p $HOME/.local/state/$(executionName)/.ssh/
            mkdir -p $HOME/.local/state/$(executionName)/.local/dumps
            mkdir -p $HOME/.local/state/$(executionName)/Documents/
            mkdir -p ./target/
            test -f target/program-$(executionName) && chmod +x target/program-$(executionName) # This file does not exist, when '--executable-path' is not set.
            podman build -f "target/Dockerfile-$(executionName)" \\
                --tag "localhost/$(executionName)"  \\
                --arch string \\
                $additionalArguments \\
                --log-level=warn # `--log-level=warn` is podman's default.
                # Logging is used, in order to better understand build runtime performance.
            """;

    private static final String PREPARE_EXECUTION_WITHOUT_BUILD_TEMPLATE = """
            set -e
            set -x
            executionName="$(executionName)"
            # TODO executionCommand is currently not used.
            executionCommand="$executionCommand"
            # Prepare file system.
            mkdir -p $HOME/.local/state/$(executionName)/.m2/
            mkdir -p $HOME/.local/state/$(executionName)/.ssh/
            mkdir -p $HOME/.local/state/$(executionName)/.local/
            mkdir -p $HOME/.local/state/$(executionName)/Documents/
            mkdir -p ./target/
            test -f target/program-$(executionName) && chmod +x target/program-$(executionName) # This file does not exist, when '--executable-path' is not set.
            """;

    private static final String EXECUTE_VIA_PODMAN_TEMPLATE = """
            set -x
            podman run --name "$(executionName)" \\
              --network slirp4netns:allow_host_loopback=true \\
              $additionalArguments \\
              --rm \\
              -v $HOME/.local/state/$(executionName)/Documents:/root/Documents \\
              -v $HOME/.local/state/$(executionName)/.ssh:/root/.ssh \\
              -v $HOME/.local/state/$(executionName)/.m2:/root/.m2 \\
              -v $HOME/.local/state/$(executionName)/.local:/root/.local/state/$(executionName)/.local \\
              "$podmanParameters" \\
              "localhost/$(executionName)"
              #
              # allow_host_loopback is required, so that the software in the container can connect to the host.
            """;

    private static final String PUBLISH_VIA_PODMAN_TEMPLATE = """
            podman tag $(executionName):latest codeberg.org/splitcells-net/$(executionName):latest
            podman push codeberg.org/splitcells-net/$(executionName):latest
            """;

    private boolean wasExecuted = false;
    private @Accessors(chain = true) @Setter @Getter String remoteExecutionScript = "";
    private String executionScript = "";
    private String dockerFile = "";
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
            executeRemotelyViaSsh(config);
            return;
        }
        executeLocally(config);
    }

    private void executeRemotelyViaSsh(WorkerExecutionConfig config) {
        val executeViaSshAt = config.executeViaSshAt().orElseThrow();
        val username = executeViaSshAt.split("@")[0];
        final String preparingNetworkLogPullScript;
        final String closingPullNetworkLogScript;
        final String mainRemoteExecutionScript;
        if (config.isPullNetworkLog()) {
            // TODO I don't know why, but using multi line strings here brakes the grammar check.
            preparingNetworkLogPullScript = "# Preparing Execution via Network Log Pull\n"
                    + "if ssh -q $(executeViaSshAt) \"sh -c '[ -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log ]'\"\n"
                    + "then\n"
                    + "  cd ../net.splitcells.network.log\n"
                    + "  git config remote.$(executeViaSshAt).url >&- || git remote add $(executeViaSshAt) $(executeViaSshAt):/home/$(username)/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log\n"
                    + "  git remote set-url $(executeViaSshAt) $(executeViaSshAt):/home/$(username)/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log\n"
                    + "  git remote set-url --push $(executeViaSshAt) $(executeViaSshAt):/home/$(username)/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log\n"
                    + "  git pull $(executeViaSshAt) master\n"
                    + "  cd ../net.splitcells.network\n"
                    + "fi\n";
        } else {
            preparingNetworkLogPullScript = "";
        }
        // `-t` prevents errors, when a command like sudo is executed.
        mainRemoteExecutionScript = "# Execution Main Task Remotely\n"
                + "ssh " + executeViaSshAt + " /bin/sh << EOF\n"
                + "  set -e\n"
                + "  if [ ! -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network ]; then\n"
                + "    mkdir -p ~/.local/state/net.splitcells.network.worker/repos/public/\n"
                + "    cd ~/.local/state/net.splitcells.network.worker/repos/public/\n"
                + "    git clone https://codeberg.org/splitcells-net/net.splitcells.network.git\n"
                + "  fi\n"
                + "  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network \\\n  && bin/worker.execute \\\n"
                + "    "
                + config.shellArgumentString(a -> !"execute-via-ssh-at".equals(a)).replace("\\\n", "\\\n   ")
                + "\nEOF";
        if (config.isPullNetworkLog()) {
            // TODO I don't know why, but using multi line strings here brakes the grammar check.
            closingPullNetworkLogScript = "# Closing Execution via Network Log Pull\n"
                    + "cd ../net.splitcells.network.log\n"
                    + "git config remote.$(executeViaSshAt).url >&- || git remote add $(executeViaSshAt) $(executeViaSshAt):/home/$(username)/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log\n"
                    + "git remote set-url $(executeViaSshAt) $(executeViaSshAt):/home/$(username)/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log\n"
                    + "git remote set-url --push $(executeViaSshAt) $(executeViaSshAt):/home/$(username)/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log\n"
                    + "git pull $(executeViaSshAt) master\n";
        } else {
            closingPullNetworkLogScript = "";
        }
        remoteExecutionScript = formatDocument(formatSection(preparingNetworkLogPullScript)
                + formatSection(mainRemoteExecutionScript)
                + formatSection(closingPullNetworkLogScript))
                .replace("$(executeViaSshAt)", executeViaSshAt)
                .replace("$(username)", username)
                .replace("$(executionName)", config.name());
        if (!config.dryRun()) {
            logs().append("Executing script: \n" + remoteExecutionScript, INFO);
            executeShellCommand(remoteExecutionScript);
        } else {
            logs().append("Generated script: \n" + remoteExecutionScript, INFO);
        }
    }

    private String formatDocument(String arg) {
        if (arg.endsWith("\n\n")) {
            return arg.substring(0, arg.length() - 1);
        }
        return arg;
    }

    private String formatSection(String arg) {
        if (arg.isBlank()) {
            return "";
        }
        if (arg.endsWith("\n")) {
            return arg + "\n";
        }
        return arg + "\n\n";
    }

    private void executeLocally(WorkerExecutionConfig config) {
        configValue(CurrentFileSystem.class).createDirectoryPath("./target");
        dockerFile = DOCKERFILE_SERVICE_TEMPLATE;
        if (config.command().isPresent()) {
            dockerFile += "ENTRYPOINT " + config.command().get();
        } else if (config.executablePath().isPresent()) {
            programName = "program-" + config.name();
            config.currentFileSystem().copyFileFromTo(config.executablePath().get()
                    , trail("target", programName));
            dockerFile += "ADD ./" + programName + " /root/program\n";
            dockerFile += "ENTRYPOINT /root/program";
        } else if (config.classForExecution().isPresent()) {
            dockerFile += JAVA_CLASS_EXECUTION_TEMPLATE;
            dockerFile += dockerFile.replace("$CLASS_FOR_EXECUTION", config.classForExecution().get());
        } else {
            throw execException("Either `--command`, `--executable-path` or `--class-for-execution` needs to be set.");
        }
        if (config.usePlaywright()) {
            dockerFile = dockerFile.replace("$ContainerSetupCommand", "RUN cd /root/opt/$NAME_FOR_EXECUTION/ && mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args=\"install-deps\"\n");
        } else {
            dockerFile = dockerFile.replace("$ContainerSetupCommand", "\n");
        }
        dockerFile = dockerFile.replace("$NAME_FOR_EXECUTION", config.name());
        dockerFilePath = "target/Dockerfile-" + config.name();
        config.currentFileSystem().replaceFile(dockerFilePath, toBytes(dockerFile));
        config.currentFileSystem().replaceFile("target/net.splitcells.network.worker.pom.xml", toBytes(POM));
        if (config.onlyExecuteImage()) {
            executionScript = PREPARE_EXECUTION_WITHOUT_BUILD_TEMPLATE;
        } else {
            executionScript = PREPARE_EXECUTION_TEMPLATE;
        }
        if (config.publishExecutionImage()) {
            executionScript += PUBLISH_VIA_PODMAN_TEMPLATE;
        } else if (!config.onlyBuildImage()) {
            executionScript += EXECUTE_VIA_PODMAN_TEMPLATE;
        }
        if (config.command().isPresent()) {
            executionScript = executionScript.replace("\"$executionCommand\""
                    , "'" + config.command().orElseThrow().replace("'", "'\\''") + "'");
        }
        if (config.autoConfigureCpuArchExplicitly()) {
            executionScript = executionScript.replace("\n    --arch string \\\n"
                    , "\n    --arch " + System.getProperty("os.arch") + " \\\n");
        }
        if (config.cpuArchitecture().isEmpty()) {
            executionScript = executionScript.replace("\n    --arch string \\\n", "\n");
        }
        if (config.verbose()) {
            executionScript = executionScript.replace("--log-level=info", "--log-level=debug");
        }
        if (config.useHostDocuments()) {
            // TODO This replacement is done in a dirty way. Use a template variable instead.
            executionScript = executionScript.replace("-v $HOME/.local/state/$(executionName)/Documents:/root/Documents \\", "-v $HOME/Documents:/root/Documents \\");
        }
        if (config.fileSystem().isFile(PODMAN_FLAGS_CONFIG_FILES.unixPathString())) {
            executionScript = executionScript.replace("$additionalArguments \\", (config.fileSystem().readString(PODMAN_FLAGS_CONFIG_FILES.unixPathString()) + "\\").replace("\n", ""));
        } else {
            executionScript = executionScript.replace("$additionalArguments \\", "\\");
        }
        if (config.isPullNetworkLog()) {
            throw execException("Pulling the network log is only possible for remote executions.");
        }
        if (config.verbose() || config.dryRun()) {
            logs().append("Executing script: " + executionScript);
        }
        if (config.dryRun()) {
            return;
        }
    }

    public String dockerfile() {
        return dockerFile;
    }

    public String programName() {
        return programName;
    }

    public String dockerFilePath() {
        return dockerFilePath;
    }

    public String executionScript() {
        return executionScript;
    }
}
