#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

sh is used explicitly instead of the default shell, because on many servers custom shells like fish are used,
that have different shell syntaxes, but provide good UI features for users.

The implementation tries to create one single script for the shell execution,
in order to simplify the overview and ease the understanding of the shell commands.
Thereby, one could even store the generated scripts and execute these without using Python at all,
which makes the hole thing more portable as well

The processing and string templating are separated as much as possible
by writing to any variable or attribute only once.
These variables are only used as an argument for other variables or
as variables in template strings.

TODO Create test for all `worker.execute` flags.
TODO Create option to delete things like `.m2`, `repos` and `net.splitcells.shell.commands.managed`,
     so it can be made sure, that bootstrapping actually works.
TODO Use string's substitute method, instead of string's replace.
TODO Support deployment to Docker and Kubernetes.
TODO Support executing as systemd service. Create a user service for that.
TODO Currently, there is not distinction between the name of the thing being executed and its namespace.
     This will probably create problems in the future, where you have multiple containers with the same namespace,
     but with different commands.
TODO IDEA Currently, everything is stored in `$HOME/.local/state/$(executionName)/*`.
     If more strict file isolation is required, in order to prevent file accidents,
     a namespace could be used, that is implemented as a hidden parent folder for the execution folder:
     `$HOME/.local/state/.$namespace/$(executionName)/*`.
     See `repo.process` for inspiration.
     Of course, different users could be used instead.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["Contributors To The `net.splitcells.*` Projects"]
__copyright__ = "Copyright 2024"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import argparse
import logging
import os
import platform
import shutil
import subprocess
import sys

from pathlib import Path

PODMAN_FLAGS_CONFIG_FILE = Path.home().joinpath(".config/net.splitcells.network.worker/execute.podman.flags")

TEMPORARY_FILE_PREFIX = "temporary-"
"""This file name prefix is used, to make it easy to delete just temporary services of an user."""

DOCKERFILE_SERVICE_TEMPLATE = """
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
"""

CONTAINER_POM = """
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
"""

JAVA_CLASS_EXECUTION_TEMPLATE = """
COPY deployable-jars/* /root/opt/$NAME_FOR_EXECUTION/jars/
WORKDIR /root/opt/$NAME_FOR_EXECUTION/
ENTRYPOINT ["/opt/java/openjdk/bin/java"]
CMD ["-XX:ErrorFile=/root/.local/state/$NAME_FOR_EXECUTION/.local/dumps/hs_err_pid_%p.log", "-cp", "./jars/*", "$CLASS_FOR_EXECUTION"]
"""

PREPARE_EXECUTION_TEMPLATE = """
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
podman build -f "target/Dockerfile-$(executionName)" \
    --tag "localhost/$(executionName)"  \
    --arch string \
    $additionalArguments \
    --log-level=warn # `--log-level=warn` is podman's default.
    # Logging is used, in order to better understand build runtime performance.
"""

PREPARE_EXECUTION_WITHOUT_BUILD_TEMPLATE = """
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
"""

EXECUTE_VIA_PODMAN_TEMPLATE = """
set -x
podman run --name "$(executionName)" \
  --network slirp4netns:allow_host_loopback=true \
  $additionalArguments \
  --rm \
  -v $HOME/.local/state/$(executionName)/Documents:/root/Documents \
  -v $HOME/.local/state/$(executionName)/.ssh:/root/.ssh \
  -v $HOME/.local/state/$(executionName)/.m2:/root/.m2 \
  -v $HOME/.local/state/$(executionName)/.local:/root/.local/state/$(executionName)/.local \
  "$podmanParameters" \
  "localhost/$(executionName)"
  #
  # allow_host_loopback is required, so that the software in the container can connect to the host.
"""

PUBLISH_VIA_PODMAN_TEMPLATE = """
podman tag $(executionName):latest codeberg.org/splitcells-net/$(executionName):latest
podman push codeberg.org/splitcells-net/$(executionName):latest
"""

PROGRAMS_DESCRIPTION = """Executes a given program as isolated as possible with all program files being persisted at `$HOME/.local/state/$executionName/` and user input being located at `$HOME/Documents`.
Executions with different names have different persisted file locations and are therefore isolated more clearly, whereas executions with the same name are assumed to share data.
This is the CLI interface to the Splitcells Network Worker.
Exactly one of arguments --name, --test-remote or --bootstrap-remote has to be set,
in order to execute this program.
"""

CLI_FLAG_COMMAND_HELP = """This is the shell command, to execute the task.
It consists of only one program call.
If scripts need to be executed one can use `sh -c "[script]"`.
Use this only, in order to integrate the environment with the software.
Prefer `--class-for-execution` instead."""

CLI_FLAG_AUTO_CPU_ARCH_HELP = """If set to false, the command's backend automatically determines the CPU architecture.
If set to true, the CPU architecture will be determined by this command and the determined architecture is propagated to the commands backend explicitly.
This is useful, because some tools have not a good CPU auto detection (i.e. Podman on RISC-V cannot find the fitting images based on the CPU arch automatically)."""

class WorkerExecution:
    was_executed = False
    remote_execution_script = ""
    local_execution_script = ""
    docker_file = ""
    docker_file_path = ""
    program_name = ""
    def execute(self, config):
        if self.was_executed:
            raise Exception("A WorkerExecution instance cannot be executed twice.")
        self.was_executed = True
        if config.executeViaSshAt is None:
            self.executeRemotelyViaSsh(config)
        else:
            self.executeLocally(config)
    def executeRemotelyViaSsh(self, config):
        return
    def executeLocally(self, config):
        return
def str2bool(arg):
    # The stringification of the truth boolean is `True` in Python 3 and therefore this capitalization is supported as well.
    return arg == 'true' or arg == 'True'
if __name__ == '__main__':
    # TODO Is the following really needed? If not remove the comment, and the hyphenless flag names. For each argument flag, there is an alternative name without hyphens ('-'), so these can easily be printed out and reused for this program. See `executeViaSshAt`.
    parser = argparse.ArgumentParser(description=PROGRAMS_DESCRIPTION)
    parser.add_argument('--name', dest='name', required=False, help="This is the name of the task being executed.")
    parser.add_argument('--bootstrap-remote', dest='bootstrapRemote', required=False, help="This is the ssh address for bootstrapping in the form of `username@host`. If this is set, other parameters are set automatically as well, in order to bootstrap the Network repos on a remote server in a standardized way.")
    parser.add_argument('--test-remote', dest='testRemote', required=False, help="This is the ssh address for testing in the form of `username@host`. If this is set, other parameters are set automatically as well, in order to test the Network repos on a remote server in a standardized way.")
    parser.add_argument('--pull-network-log', dest='pullNetworkLog', required=False, type=str2bool, default=False, help="If set to true, the repo `net.splitcells.network.log` will be pulled from the remote.")
    parser.add_argument('--command', required=False, dest='command', help=CLI_FLAG_COMMAND_HELP)
    parser.add_argument('--executable-path', '--executablePath', dest='executablePath', help="Executes the given executable file. Only set this option or --command.")
    parser.add_argument('--class-for-execution', '--classForExecution', dest='classForExecution', help="This Java class is executed.")
    # TODO --use-host-documents is probably not needed anymore, as there is not a concrete use case for this at the moment.
    parser.add_argument('--use-host-documents', '--useHostDocuments', dest='useHostDocuments', required=False, type=str2bool, default=False, help="Determines whether to mount `~/Documents` or not. This should be avoided, in order to avoid file dependencies to the host system, which makes the execution more portable.")
    parser.add_argument('--publish-execution-image', '--publishExecutionImage', dest='publishExecutionImage', required=False, type=str2bool, default=False, help="If set to true, the given command is not executed, but a container image is published instead.")
    parser.add_argument('--verbose', dest='verbose', required=False, type=str2bool, default=False, help="If set to true, the output is verbose.")
    parser.add_argument('--only-build-image', '--onlyBuildImage', dest='onlyBuildImage', required=False, type=str2bool, default=False, help="If set to true, the created image is not executed.")
    parser.add_argument('--only-execute-image', '--onlyExecuteImage', dest='onlyExecuteImage', required=False, type=str2bool, default=False, help="If set to true, the previously created image is executed without building it.")
    parser.add_argument('--cpu-architecture', '--cpuArchitecture', dest='cpuArchitecture', help="Set the cpu architecture for the execution.")
    parser.add_argument('--dry-run', '--dryRun', dest='dryRun', required=False, type=str2bool, default=False, help="If true, commands are only prepared and no commands are executed.")
    parser.add_argument('--is-daemon', '--isDaemon', dest='isDaemon', required=False, type=str2bool, default=False, help="If this is true, the process is executed in the background.")
    parser.add_argument('--use-playwright', '--usePlaywright', dest='usePlaywright', required=False, type=str2bool, default=False, help="If true, playwright is installed for the execution.")
    parser.add_argument('--auto-configure-cpu-architecture-explicitly', '--autoConfigureCpuArchExplicitly', dest='autoConfigureCpuArchExplicitly', required=False, type=str2bool, default=True, help=CLI_FLAG_AUTO_CPU_ARCH_HELP)
    parser.add_argument('--port-publishing', '--portPublishing', dest='portPublishing', help="This is a comma separated list of `host-port:container-port`, that describes the port forwarding on the host.")
    parser.add_argument('--execute-via-ssh-at', '--executeViaSshAt', dest='executeViaSshAt', help="Execute the given command at an remote server via SSH. The format is `[user]@[address/network name]`.")
    parser.add_argument('--flat-folders', '--flatFolders', dest='flatFolders', required=False, type=str2bool, default=False, help="If this is set to true, the `~/.local/state/$executionName` is not mapped to `~/.local/state/$executionName/.local/state/$executionName` via containers.")
    parsedArgs = parser.parse_args()
    if parsedArgs.name is not None:
        exit(0)
    if parsedArgs.bootstrapRemote is not None:
        parsedArgs.name = "net.splitcells.network.worker"
        parsedArgs.executeViaSshAt = parsedArgs.bootstrapRemote
        parsedArgs.command = "cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap"
        parsedArgs.pullNetworkLog = True
        exit(0)
    if parsedArgs.testRemote is not None:
        parsedArgs.name = "net.splitcells.network.worker"
        parsedArgs.executeViaSshAt = parsedArgs.testRemote
        parsedArgs.command = "cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap && bin/repos.test"
        parsedArgs.pullNetworkLog = True
        exit(0)
    else:
        logging.error("Exactly one of the arguments --name, --test-remote or --bootstrap-remote has to be set, in order to execute this program.");
        exit(1)