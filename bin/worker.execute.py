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
by writing to any variable or attribute only once except for the configuration.
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
TODO IDEA Currently, everything is stored in `${HOME}/.local/state/$(executionName)/*`.
     If more strict file isolation is required, in order to prevent file accidents,
     a namespace could be used, that is implemented as a hidden parent folder for the execution folder:
     `${HOME}/.local/state/.$namespace/$(executionName)/*`.
     See `repo.process` for inspiration.
     Of course, different users could be used instead.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["Contributors To The `net.splitcells.*` Projects"]
__copyright__ = "Copyright 2024"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import argparse
from datetime import datetime
import logging
import os
import platform
import random
import shutil
import subprocess
import sys
import unittest

from string import Template
from pathlib import Path

PODMAN_FLAGS_CONFIG_FILE = Path.home().joinpath(".config/net.splitcells.network.worker/execute.podman.flags")

TEMPORARY_FILE_PREFIX = "temporary-"
"""This file name prefix is used, to make it easy to delete just temporary services of an user."""

DOCKERFILE_SERVICE_TEMPLATE = """
FROM docker.io/eclipse-temurin:21-jdk-noble
RUN apt-get clean
RUN apt-get update # This fixes install errors. It is unknown why this is the case.
RUN apt-get install --yes maven git python3 pip
ADD net.splitcells.network.worker.pom.xml /root/opt/${NAME_FOR_EXECUTION}/pom.xml
# RUN pip install --break-system-packages playwright
# RUN playwright install --with-deps firefox # Install all OS dependencies, that are required for Playwright. Otherwise, Playwright cannot be used in Java.
# RUN cd /root/opt/${NAME_FOR_EXECUTION}/ && mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"
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
COPY deployable-jars/* /root/opt/${NAME_FOR_EXECUTION}/jars/
WORKDIR /root/opt/${NAME_FOR_EXECUTION}/
ENTRYPOINT ["/opt/java/openjdk/bin/java"]
CMD ["-XX:ErrorFile=/root/.local/state/${NAME_FOR_EXECUTION}/.local/dumps/hs_err_pid_%p.log", "-cp", "./jars/*", "$CLASS_FOR_EXECUTION"]
"""

PREPARE_EXECUTION_TEMPLATE = """
set -e
set -x
executionName="$(executionName)"
executionCommand="$executionCommand"
# Prepare file system.
mkdir -p ${HOME}/.local/state/$(executionName)/.m2/
mkdir -p ${HOME}/.local/state/$(executionName)/.ssh/
mkdir -p ${HOME}/.local/state/$(executionName)/.local/dumps
mkdir -p ${HOME}/.local/state/$(executionName)/Documents/
mkdir -p ./target/
test -f target/program-$(executionName) && chmod +x target/program-$(executionName) # This file does not exist, when '--executable-path' is not set.
podman build -f "target/Dockerfile-$(executionName)" \
    --tag "localhost/$(executionName)"  \
    --arch string \
    ${additionalArguments} \
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
mkdir -p ${HOME}/.local/state/$(executionName)/.m2/
mkdir -p ${HOME}/.local/state/$(executionName)/.ssh/
mkdir -p ${HOME}/.local/state/$(executionName)/.local/
mkdir -p ${HOME}/.local/state/$(executionName)/Documents/
mkdir -p ./target/
test -f target/program-$(executionName) && chmod +x target/program-$(executionName) # This file does not exist, when '--executable-path' is not set.
"""

EXECUTE_VIA_PODMAN_TEMPLATE = """
set -x
podman run --name "$(executionName)" \
  --network slirp4netns:allow_host_loopback=true \
  ${additionalArguments} \
  --rm \
  -v ${HOME}/.local/state/$(executionName)/Documents:/root/Documents \
  -v ${HOME}/.local/state/$(executionName)/.ssh:/root/.ssh \
  -v ${HOME}/.local/state/$(executionName)/.m2:/root/.m2 \
  -v ${HOME}/.local/state/$(executionName)/.local:/root/.local/state/$(executionName)/.local \
  "$podmanParameters" \
  "localhost/$(executionName)"
  #
  # allow_host_loopback is required, so that the software in the container can connect to the host.
"""

PUBLISH_VIA_PODMAN_TEMPLATE = """
podman tag $(executionName):latest codeberg.org/splitcells-net/$(executionName):latest
podman push codeberg.org/splitcells-net/$(executionName):latest
"""

PROGRAMS_DESCRIPTION = """Executes a given program as isolated as possible with all program files being persisted at `${HOME}/.local/state/${executionName}/` and user input being located at `${HOME}/Documents`.
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

DEFAULT_NETWORK_PULL_SCRIPT = """# Preparing Execution via Network Log Pull
if ssh -q ${execute_via_ssh_at} "sh -c '[ -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log ]'"
then
  cd ../net.splitcells.network.log
  git config remote.${execute_via_ssh_at}.url >&- || git remote add ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git remote set-url ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git remote set-url --push ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git pull ${execute_via_ssh_at} master
  cd ../net.splitcells.network
fi"""

DEFAULT_CLOSING_PULL_NETWORK_LOG_SCRIPT = """# Closing Execution via Network Log Pull
cd ../net.splitcells.network.log
git config remote.${execute_via_ssh_at}.url >&- || git remote add ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url --push ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git pull ${execute_via_ssh_at} master
"""

EXECUTE_MAIN_TASK_REMOTELY = """# Execute Main Task Remotely
ssh ${execute_via_ssh_at} /bin/sh << EOF
  set -e
  if [ ! -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network ]; then
    mkdir -p ~/.local/state/net.splitcells.network.worker/repos/public/
    cd ~/.local/state/net.splitcells.network.worker/repos/public/
    git clone https://codeberg.org/splitcells-net/net.splitcells.network.git
  fi
  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network
  ${bin_worker_execute} \\\n${remoteNetworkerArguments}
EOF"""

SET_UP_SYSTEMD_SERVICE_REMOTELY = """# Set up Systemd service remotely
ssh ${execute_via_ssh_at} /bin/sh << EOF
  set -e
  mkdir -p ${daemonFolder}
  cat > ${daemonFile} <<SERVICE_EOL
[Unit]
Description=Execute ${daemonName}

[Service]
Type=oneshot
StandardOutput=journal
ExecStart=/usr/bin/date
SERVICE_EOL
EOF"""

class WorkerExecution:
    was_executed = False
    remote_execution_script_template = ""
    remote_execution_script = ""
    local_execution_script = ""
    docker_file = ""
    docker_file_path = ""
    program_name = ""
    username = None
    config = None
    remote_networker_arguments = ""
    daemonFolder = ""
    daemonName = ""
    daemonFile = ""
    configParser = None
    additional_podman_args = ""
    bin_worker_execute = None
    def execute(self, configParser, config):
        self.configParser = configParser
        self.config = config
        if self.was_executed:
            raise Exception("A WorkerExecution instance cannot be executed twice.")
        self.was_executed = True
        if self.config.backwards_compatible:
            self.bin_worker_execute = "bin/worker.execute"
        else:
            self.bin_worker_execute = "bin/worker.execute.py"
        if config.execute_via_ssh_at is None:
            self.executeLocally()
        else:
            self.executeRemotelyViaSsh()
    def filterArgumentsForRemoteScript(self, parsedVars, key):
        if self.config.backwards_compatible:
            if key in ['pull_network_log', 'backwards_compatible']:
                return False
        return parsedVars[key] is not None \
            and self.configParser.get_default(key) != parsedVars[key] \
            and key != 'execute_via_ssh_at'
    def executeRemotelyViaSsh(self):
        self.username = self.config.execute_via_ssh_at.split("@")[0]
        preparingNetworkLogPullScript = None;
        closingpull_network_logScript = None;
        if self.config.pull_network_log:
            preparingNetworkLogPullScript = DEFAULT_NETWORK_PULL_SCRIPT
        else:
            preparingNetworkLogPullScript = ""
        if self.config.is_daemon:
            if self.config.daemon_name is None:
                self.daemonName = TEMPORARY_FILE_PREFIX + self.config.name + "-" + datetime.now().strftime('%Y-%m-%d-%H-%M-%S') + "-" + str(random.randint(1, 999_999_999))
            else:
                self.daemonName = self.config.daemon_name
            self.daemonFolder = "~/.config/systemd/user";
            self.daemonFile = self.daemonFolder + "/" + self.daemonName;
            self.remote_execution_script_template = self.applyTemplate(SET_UP_SYSTEMD_SERVICE_REMOTELY)
        else: # Else is not a daemon.
        # TODO The method for generating the remote script is an hack.
            parsedVars = vars(self.config)
            parsedKeys = list(filter(lambda key: self.filterArgumentsForRemoteScript(parsedVars, key), sorted(list(parsedVars.keys()))))
            for i, key in enumerate(parsedKeys):
                self.remote_networker_arguments + "\n"
                value_string = ""
                if type(parsedVars[key]) == bool:
                    value_string = str(parsedVars[key]).lower()
                else:
                    value_string = str(parsedVars[key]).replace("\'", "\\\'").replace("\"", "\\\"").replace("\n", "")
                self.remote_networker_arguments += "    --" + key.replace("_", "-") + "='" + value_string + "'"
                if i != len(parsedKeys) - 1:
                    self.remote_networker_arguments += "\\\n"
                else:
                    self.remote_networker_arguments += "\n"
            self.remote_execution_script_template = self.applyTemplate(EXECUTE_MAIN_TASK_REMOTELY)
        if self.config.pull_network_log:
            closingPullNetworkLogScript = DEFAULT_CLOSING_PULL_NETWORK_LOG_SCRIPT
        else:
            closingPullNetworkLogScript = ""
        self.remote_execution_script = self.applyTemplate(self.formatDocument(self.formatSection(preparingNetworkLogPullScript)
            + self.formatSection(self.remote_execution_script_template)
            + self.formatSection(closingPullNetworkLogScript)))
        if self.config.dry_run:
            logging.info("Generated script: \n" + self.remote_execution_script)
        else:
            if self.config.verbose:
                logging.info("Executing script: \n" + self.remote_execution_script)
            subprocess.call(self.remote_execution_script, shell='True')
        return
    def applyTemplate(self, string):
        return Template(string).safe_substitute(
            execute_via_ssh_at = self.config.execute_via_ssh_at,
            username = self.username,
            name = self.config.name,
            remoteNetworkerArguments = self.remote_networker_arguments,
            daemonFolder = self.daemonFolder,
            daemonName = self.daemonName,
            daemonFile = self.daemonFile,
            bin_worker_execute = self.bin_worker_execute)
    def formatDocument(self, arg):
        """Ensure, that the document ends with a single new line symbol."""
        if arg.endswith("\n\n"):
            return arg[:len(arg) - 1]
        return arg
    def formatSection(self, arg):
        """Ensure, that the section ends with an empty line, if any section is present."""
        if arg.strip() == "":
            return ""
        if arg.endswith("\n"):
            return arg + "\n"
        return arg + "\n\n"
    def executeLocally(self):
        """ TODO Use [${...}] based variable substitution instead of complex string replacements. """
        if self.config.is_daemon:
            raise Exception("Running a service as a daemon is not implemented yet.")
        if not os.path.exists('./target'):
            os.mkdir('./target')
        # TODO Consoldiate Dockerfile template extensions, as every case can be solved via a dedicated shell script, that is the entrypoint of the Dockerfile.
        required_argument_count = 0
        self.docker_file = DOCKERFILE_SERVICE_TEMPLATE
        if self.config.command is not None:
            ++required_argument_count
            self.docker_file += "ENTRYPOINT " + self.config.command + "\n"
        if self.config.executable_path is not None:
            ++required_argument_count
            self.program_name = "program-" + self.config.name
            shutil.copyfile(parsedArgs.executable_path, "./target/" + self.program_name)
            self.docker_file += "ADD ./" + self.program_name + " /root/program\n"
            self.docker_file += 'ENTRYPOINT /root/program'
        if self.config.class_for_execution is not None:
            ++required_argument_count
            self.docker_file += JAVA_CLASS_EXECUTION_TEMPLATE
            self.docker_file += self.docker_file.replace('$CLASS_FOR_EXECUTION', self.config.class_for_execution)
        if required_argument_count == 0:
            raise Exception("Either `--command`, `--executable-path` or `--class-for-execution` needs to be set:" + str(self.config))
        if required_argument_count > 1:
            raise Exception("Exactly one of `--command`, `--executable-path` or `--class-for-execution` needs to be set, but " + required_argument_count + " were actually set.")
        if self.config.flat_folders:
            self.docker_file = self.docker_file.replace("VOLUME /root/.local/", "VOLUME /root/.local/state/${executionName}/.local/")
            self.docker_file = self.docker_file.replace("VOLUME /root/Documents/", "VOLUME /root/.local/state/${executionName}/Documents/")
            self.docker_file = self.docker_file.replace("VOLUME /root/repos/", "VOLUME /root/.local/state/${executionName}/repos/")
            # .ssh and .m2 does not have to be replaced, as these are used for environment configuration of tools inside the container.
        if parsedArgs.use_playwright:
            self.docker_file = self.docker_file.replace('$ContainerSetupCommand', 'RUN cd /root/opt/${NAME_FOR_EXECUTION}/ && mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"\n')
        else:
            self.docker_file = self.docker_file.replace('$ContainerSetupCommand', '\n')
        self.docker_file = self.docker_file.replace('${NAME_FOR_EXECUTION}', self.config.name)
        self.docker_file = self.docker_file.replace('${executionName}', self.config.name)
        file = 'target/Dockerfile-' + self.config.name
        if os.path.exists(file):
            os.remove(file)
        with open(file, 'w') as file_to_write:
            file_to_write.write(self.docker_file)
        with open('target/net.splitcells.network.worker.pom.xml', 'w') as pom_file_to_write:
            pom_file_to_write.write(CONTAINER_POM)
        if self.config.only_execute_image:
            self.local_execution_script = PREPARE_EXECUTION_WITHOUT_BUILD_TEMPLATE
        else:
            self.local_execution_script = PREPARE_EXECUTION_TEMPLATE
        if self.config.publish_execution_image:
            self.local_execution_script += PUBLISH_VIA_PODMAN_TEMPLATE
        elif self.config.only_build_image:
            pass # TODO This is not implemented yet.
        else:
            self.local_execution_script += EXECUTE_VIA_PODMAN_TEMPLATE
        if self.config.flat_folders:
            self.local_execution_script = self.local_execution_script.replace("-v ${HOME}/.local/state/${executionName}/Documents:/root/Documents ", "-v ${HOME}/.local/state/${executionName}/Documents:/root/.local/state/${executionName}/Documents ")
            self.local_execution_script = self.local_execution_script.replace("-v ${HOME}/.local/state/${executionName}/repos:/root/repos ", "-v ${HOME}/.local/state/${executionName}/repos:/root/.local/state/${executionName}/repos ")
            self.local_execution_script = self.local_execution_script.replace("-v ${HOME}/.local/state/${executionName}/.local:/root/.local ", "-v ${HOME}/.local/state/${executionName}/.local:/root/.local/state/${executionName}/.local ")
            # .ssh and .m2 does not have to be replaced, as these are used for environment configuration of tools inside the container.
        if self.config.command is not None:
            # TODO This does not seem to be valid or used anymore.
            self.local_execution_script = executionScript.replace('"$executionCommand"', "'" + self.config.command.replace("'", "'\\''") + "'")
        if self.config.port_publishing is not None:
            for portMapping in self.config.port_publishing.split(','):
                self.additional_podman_args += ' --publish ' + portMapping
        self.local_execution_script = self.local_execution_script.replace('"$podmanParameters"', self.additional_podman_args)
        if self.config.auto_configure_cpuArch_explicitly:
            self.local_execution_script = self.local_execution_script.replace('\n    --arch string \\\n', '\n    --arch ' + platform.uname().machine + ' \\\n')
        elif self.config.cpu_architecture is None:
            self.local_execution_script = self.local_execution_script.replace('\n    --arch string \\\n', '\n')
        else:
            self.local_execution_script = self.local_execution_script.replace('\n    --arch string \\\n', '\n    --arch ' + self.config.cpu_architecture + ' \\\n')
        if self.config.verbose:
            self.local_execution_script = self.local_execution_script.replace('--log-level=info', '--log-level=debug')
        else:
            self.local_execution_script = self.local_execution_script.replace("\nset -x\n", "\n\n")
        if self.config.use_host_documents:
            # TODO This replacement is done in a dirty way. Use a template variable instead.
            self.local_execution_script = self.local_execution_script.replace("-v ${HOME}/.local/state/${executionName}/Documents:/root/Documents \\", "-v ${HOME}/Documents:/root/Documents \\")
        if PODMAN_FLAGS_CONFIG_FILE.is_file():
            self.local_execution_script = self.local_execution_script.replace('${additionalArguments} \\', (PODMAN_FLAGS_CONFIG_FILE.read_text() + '\\').replace('\n', ''))
        else:
            self.local_execution_script = self.local_execution_script.replace('${additionalArguments} \\', '\\')
        self.local_execution_script = self.local_execution_script.replace('${executionName}', self.config.name)
        # Execute program.
        if parsedArgs.dry_run:
            logging.error("Generating script: " + self.local_execution_script);
            exit(0)
        if parsedArgs.verbose:
            logging.info("Executing script: " + self.local_execution_script);
        return_code = subprocess.call(self.local_execution_script, shell='False') # The systems default shell is not used, because Fish and Bash are not compatible to each other in the slightest.
        if return_code != 0:
            logging.error("Could not execute given command.");
        exit(return_code)
def str2bool(arg):
    # The stringification of the truth boolean is `True` in Python 3 and therefore this capitalization is supported as well.
    return arg == 'true' or arg == 'True'
def parse_worker_execution_arguments(arguments):
    # If not remove the comment, and the hyphenless flag names. For each argument flag, there is an alternative name without hyphens ('-'), so these can easily be printed out and reused for this program. See `execute_via_ssh_at`.
    parser = argparse.ArgumentParser(description=PROGRAMS_DESCRIPTION)
    parser.add_argument('--name', dest='name', required=False, help="This is the name of the task being executed.")
    parser.add_argument('--bootstrap-remote', dest='bootstrap_remote', required=False, help="This is the ssh address for bootstrapping in the form of `username@host`. If this is set, other parameters are set automatically as well, in order to bootstrap the Network repos on a remote server in a standardized way.")
    parser.add_argument('--test-remote', dest='test_remote', required=False, help="This is the ssh address for testing in the form of `username@host`. If this is set, other parameters are set automatically as well, in order to test the Network repos on a remote server in a standardized way.")
    parser.add_argument('--build-remote', dest='build_remote', required=False, help="This is the ssh address for building the Splitcells Network project in the form of `username@host`. If this is set, other parameters are set automatically as well, in order to build the Network repos on a remote server in a standardized way.")
    parser.add_argument('--pull-network-log', dest='pull_network_log', required=False, type=str2bool, default=True, help="If set to true, the repo `net.splitcells.network.log` will be pulled from the remote.")
    parser.add_argument('--command', required=False, dest='command', help=CLI_FLAG_COMMAND_HELP)
    parser.add_argument('--executable-path', '--executable_path', dest='executable_path', help="Executes the given executable file. Only set this option or --command.")
    parser.add_argument('--class-for-execution', '--class_for_execution', dest='class_for_execution', help="This Java class is executed.")
    # TODO --use-host-documents is probably not needed anymore, as there is not a concrete use case for this at the moment.
    parser.add_argument('--use-host-documents', '--use_host_documents', dest='use_host_documents', required=False, type=str2bool, default=False, help="Determines whether to mount `~/Documents` or not. This should be avoided, in order to avoid file dependencies to the host system, which makes the execution more portable.")
    parser.add_argument('--publish-execution-image', '--publish_execution_image', dest='publish_execution_image', required=False, type=str2bool, default=False, help="If set to true, the given command is not executed, but a container image is published instead.")
    parser.add_argument('--verbose', dest='verbose', required=False, type=str2bool, default=False, help="If set to true, the output is verbose.")
    parser.add_argument('--only-build-image', '--only_build_image', dest='only_build_image', required=False, type=str2bool, default=False, help="If set to true, the created image is not executed.")
    parser.add_argument('--only-execute-image', '--only_execute_image', dest='only_execute_image', required=False, type=str2bool, default=False, help="If set to true, the previously created image is executed without building it.")
    parser.add_argument('--cpu-architecture', '--cpu_architecture', dest='cpu_architecture', help="Set the cpu architecture for the execution.")
    parser.add_argument('--dry-run', dest='dry_run', required=False, type=str2bool, default=False, help="If true, commands are only prepared and no commands are executed.")
    parser.add_argument('--is-daemon', '--is_daemon', dest='is_daemon', required=False, type=str2bool, default=False, help="If this is true, the process is executed as systemd user service in the background.")
    parser.add_argument('--daemon-name', dest='daemon_name', required=False, help="This is the name of the systemd user service name. This flag is mainly used for unit tests or to ensure, that exactly one daemon instance exists at any given time.")
    parser.add_argument('--use-playwright', '--use_playwright', dest='use_playwright', required=False, type=str2bool, default=False, help="If true, playwright is installed for the execution.")
    parser.add_argument('--auto-configure-cpu-architecture-explicitly', '--auto_configure_cpuArch_explicitly', dest='auto_configure_cpuArch_explicitly', required=False, type=str2bool, default=True, help=CLI_FLAG_AUTO_CPU_ARCH_HELP)
    parser.add_argument('--port-publishing', '--port_publishing', dest='port_publishing', help="This is a comma separated list of `host-port:container-port`, that describes the port forwarding on the host.")
    parser.add_argument('--execute-via-ssh-at', '--execute_via_ssh_at', dest='execute_via_ssh_at', help="Execute the given command at an remote server via SSH. The format is `[user]@[address/network name]`.")
    parser.add_argument('--flat-folders', '--flat_folders', dest='flat_folders', required=False, type=str2bool, default=False, help="If this is set to true, the `~/.local/state/${executionName}` is not mapped to `~/.local/state/${executionName}/.local/state/${executionName}` via containers.")
    parser.add_argument('--backwards-compatible', dest='backwards_compatible', required=False, type=str2bool, default=True, help="If set to true, the the remote script is compatible to the previous implementation.")
    parsedArgs = parser.parse_args(arguments)
    workerExecution = WorkerExecution()
    if parsedArgs.command is not None:
        if parsedArgs.name is None:
            parsedArgs.name = "net.splitcells.network.worker"
    elif parsedArgs.bootstrap_remote is not None:
        if parsedArgs.name is None:
            parsedArgs.name = "net.splitcells.network.worker"
        parsedArgs.execute_via_ssh_at = parsedArgs.bootstrap_remote
        parsedArgs.command = "cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap"
        parsedArgs.bootstrap_remote = None
    elif parsedArgs.test_remote is not None:
        if parsedArgs.name is None:
            parsedArgs.name = "net.splitcells.network.worker"
        parsedArgs.execute_via_ssh_at = parsedArgs.test_remote
        parsedArgs.command = "cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap && bin/repos.test"
        parsedArgs.test_remote = None
    elif parsedArgs.build_remote is not None:
        if parsedArgs.name is None:
            parsedArgs.name = "net.splitcells.network.worker"
        parsedArgs.execute_via_ssh_at = parsedArgs.build_remote
        parsedArgs.command = "cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap && bin/repos.build;" \
            + 'cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.martins.avots.distro && ../net.splitcells.network/bin/worker.execute --name="net.splitcells.martins.avots.distro" --class-for-execution="net.splitcells.martins.avots.distro.LiveDistro" --only-build-image=true --use-playwright=true'
        parsedArgs.build_remote = None
    else:
        raise Exception("Exactly one of the arguments --name, --test-remote or --bootstrap-remote has to be set, in order to execute this program.");
    workerExecution.execute(parser, parsedArgs)
    return workerExecution
class TestWorkerExecution(unittest.TestCase):
    maxDiff = None
    def test_bootstrap_remote(self):
        test_subject = parse_worker_execution_arguments(['--bootstrap-remote=user@address', '--dry-run=true', '--backwards-compatible=True'])
        self.assertEqual(test_subject.remote_execution_script, """# Preparing Execution via Network Log Pull
if ssh -q user@address "sh -c '[ -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log ]'"
then
  cd ../net.splitcells.network.log
  git config remote.user@address.url >&- || git remote add user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git remote set-url user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git remote set-url --push user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git pull user@address master
  cd ../net.splitcells.network
fi

# Execute Main Task Remotely
ssh user@address /bin/sh << EOF
  set -e
  if [ ! -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network ]; then
    mkdir -p ~/.local/state/net.splitcells.network.worker/repos/public/
    cd ~/.local/state/net.splitcells.network.worker/repos/public/
    git clone https://codeberg.org/splitcells-net/net.splitcells.network.git
  fi
  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network
  bin/worker.execute \\
    --command='cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap'\\
    --dry-run='true'\\
    --name='net.splitcells.network.worker'

EOF

# Closing Execution via Network Log Pull
cd ../net.splitcells.network.log
git config remote.user@address.url >&- || git remote add user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url --push user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git pull user@address master
""")
    def test_bootstrap_remote_via_daemon(self):
        test_subject = parse_worker_execution_arguments(['--bootstrap-remote=user@address', '--is-daemon=true', '--daemon-name=daemon-name', '--dry-run=true', '--backwards-compatible=True'])
        self.assertEqual(test_subject.remote_execution_script, """# Preparing Execution via Network Log Pull
if ssh -q user@address "sh -c '[ -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log ]'"
then
  cd ../net.splitcells.network.log
  git config remote.user@address.url >&- || git remote add user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git remote set-url user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git remote set-url --push user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
  git pull user@address master
  cd ../net.splitcells.network
fi

# Set up Systemd service remotely
ssh user@address /bin/sh << EOF
  set -e
  mkdir -p ~/.config/systemd/user
  cat > ~/.config/systemd/user/daemon-name <<SERVICE_EOL
[Unit]
Description=Execute daemon-name

[Service]
Type=oneshot
StandardOutput=journal
ExecStart=/usr/bin/date
SERVICE_EOL
EOF

# Closing Execution via Network Log Pull
cd ../net.splitcells.network.log
git config remote.user@address.url >&- || git remote add user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url --push user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git pull user@address master
""")
if __name__ == '__main__':
    # As there is no build process for Python unit tests are executed every time, to make sure, that the script works correctly.
    test_result = unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestWorkerExecution))
    if not test_result.wasSuccessful():
        logging.basicConfig(level=logging.INFO)
        raise Exception("The self test was not successful: " + str(unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestWorkerExecution))))
    parse_worker_execution_arguments(sys.argv[1:])