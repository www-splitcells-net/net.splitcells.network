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

TODO Add test coverage and consider checking minimum coverage percentage.
TODO Use only applyTemplate for any String replacement.
TODO A lot less should be done in the executeRemotelyViaSsh.
     For instance, the systemd unit file should be done via executeLocally.
TODO Create test for all `worker.execute` flags.
TODO Create option to delete things like `.m2`, `repos` and `net.splitcells.shell.commands.managed`,
     so it can be made sure, that bootstrapping actually works.
TODO Use string's substitute method, instead of string's replace.
TODO Use as few string templates as possible.
     In an ideal case a global one is enough, as templating is much more simple than
TODO Only mount the program's state folder and not its sub folders one by one,
     in order to simplify state folder adjustments and the Python code.
TODO Support deployment to Docker and Kubernetes.
TODO Support executing as systemd service. Create a user service for that.
TODO Currently, there is not distinction between the name of the thing being executed and its namespace.
     This will probably create problems in the future, where you have multiple containers with the same namespace,
     but with different commands.
TODO IDEA Currently, everything is stored in `~/.local/state/${programName}/*`.
     If more strict file isolation is required, in order to prevent file accidents,
     a namespace could be used, that is implemented as a hidden parent folder for the execution folder:
     `~/.local/state/.$namespace/${programName}/*`.
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
VOLUME /root/.local/state/${programName}/.local/
VOLUME /root/bin/
VOLUME /root/.local/state/${programName}/Documents/
VOLUME /root/.ssh/
VOLUME /root/.m2/
VOLUME /root/.local/state/${programName}/repos/
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
COPY ${NAME_FOR_EXECUTION}/deployable-jars/* /root/opt/${NAME_FOR_EXECUTION}/jars/
WORKDIR /root/opt/${NAME_FOR_EXECUTION}/
ENTRYPOINT ["/opt/java/openjdk/bin/java"]
CMD ["-XX:ErrorFile=/root/.local/state/${NAME_FOR_EXECUTION}/.local/dumps/hs_err_pid_%p.log", "-cp", "./jars/*", "$CLASS_FOR_EXECUTION"]
"""

# `--log-level=warn` is podman's default.
# Logging is used, in order to better understand build runtime performance.
PREPARE_EXECUTION_TEMPLATE = """
set -e
set -x
# Prepare file system.
mkdir -p ~/.local/state/${programName}/.m2/
mkdir -p ~/.local/state/${programName}/.ssh/
mkdir -p ~/.local/state/${programName}/.local/dumps/
mkdir -p ~/.local/state/${programName}/Documents/
mkdir -p ~/.local/state/${programName}/repos/
mkdir -p ~/.local/state/${programName}/bin/
mkdir -p ~/.local/state/${programName}/config/
mkdir -p ~/.local/state/${programName}/logs/
mkdir -p ./target/
test -f target/program-${programName} && chmod +x target/program-${programName} # This file does not exist, when '--executable-path' is not set.
cd ~/.local/state/${programName}/repos/public/net.splitcells.network
podman build -f "target/Dockerfile-${executionName}" \\
    --tag "localhost/${executionName}"  \\
    --arch string \\
    ${additionalArguments}\\
    --log-level=warn
"""

PREPARE_EXECUTION_WITHOUT_BUILD_TEMPLATE = """
set -e
set -x
# Prepare file system.
mkdir -p ~/.local/state/${programName}/.m2/
mkdir -p ~/.local/state/${programName}/.ssh/
mkdir -p ~/.local/state/${programName}/.local/dumps/
mkdir -p ~/.local/state/${programName}/Documents/
mkdir -p ~/.local/state/${programName}/repos/
mkdir -p ~/.local/state/${programName}/bin/
mkdir -p ~/.local/state/${programName}/config/
mkdir -p ~/.local/state/${programName}/logs/
mkdir -p ./target/
test -f target/program-${programName} && chmod +x target/program-${programName} # This file does not exist, when '--executable-path' is not set.
"""

# allow_host_loopback is required, so that the software in the container can connect to the host.
PODMAN_COMMAND_TEMPLATE = """podman run --name "${executionName}" \\
  --network slirp4netns:allow_host_loopback=true \\
  ${additionalArguments}\\
  --rm \\
  -v ~/.local/state/${programName}/Documents:/root/.local/state/${programName}/Documents \\
  -v ~/.local/state/${programName}/.ssh:/root/.ssh \\
  -v ~/.local/state/${programName}/.m2:/root/.m2 \\
  -v ~/.local/state/${programName}/.local:/root/.local/state/${programName}/.local \\
  -v ~/.local/state/${programName}/repos:/root/.local/state/${programName}/repos \\
  -v ~/.local/state/${programName}/config:/root/.local/state/${programName}/config \\
  -v ~/.local/state/${programName}/logs:/root/.local/state/${programName}/logs \\
  -v ~/.local/state/${programName}/bin:/root/bin \\
  ${podmanParameters}\\
  "localhost/${executionName}"
"""

EXECUTE_VIA_PODMAN_TEMPLATE = """
set -x
""" + PODMAN_COMMAND_TEMPLATE

PUBLISH_VIA_PODMAN_TEMPLATE = """
podman tag ${programName}:latest codeberg.org/splitcells-net/${programName}:latest
podman push codeberg.org/splitcells-net/${programName}:latest
"""

PROGRAMS_DESCRIPTION = """Executes a given program as isolated as possible with all program files being persisted at `~/.local/state/${programName}/` and user input being located at `~/Documents`.
Executions with different names have different persisted file locations and are therefore isolated more clearly, whereas executions with the same name are assumed to share data.
This is the CLI interface to the Splitcells Network Worker.
Exactly one of arguments --program-name, --test-remote or --bootstrap-remote has to be set,
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
if ssh -q ${execute_via_ssh_at} "sh -c '[ -d ~/.local/state/${programName}/repos/public/net.splitcells.network.log ]'"
then
  cd ../net.splitcells.network.log
  git config remote.${execute_via_ssh_at}.url >&- || git remote add ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/${programName}/repos/public/net.splitcells.network.log
  git remote set-url ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/${programName}/repos/public/net.splitcells.network.log
  git remote set-url --push ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/${programName}/repos/public/net.splitcells.network.log
  git pull ${execute_via_ssh_at} master
  cd ../net.splitcells.network
fi"""

DEFAULT_CLOSING_PULL_NETWORK_LOG_SCRIPT = """# Closing Execution via Network Log Pull
cd ../net.splitcells.network.log
git config remote.${execute_via_ssh_at}.url >&- || git remote add ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/${programName}/repos/public/net.splitcells.network.log
git remote set-url ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/${programName}/repos/public/net.splitcells.network.log
git remote set-url --push ${execute_via_ssh_at} ${execute_via_ssh_at}:/home/${username}/.local/state/${programName}/repos/public/net.splitcells.network.log
git pull ${execute_via_ssh_at} master
"""

# The `net.splitcells.network && git pull` etc. ensures, that changes to bin/worker.execute etc. are available as fast as possible.
EXECUTE_MAIN_TASK_REMOTELY = """# Execute Main Task Remotely
ssh ${execute_via_ssh_at} /bin/sh << EOF
  set -e
  if [ ! -d ~/.local/state/${programName}/repos/public/net.splitcells.network ]; then
    mkdir -p ~/.local/state/${programName}/repos/public/
    cd ~/.local/state/${programName}/repos/public/
    git clone https://codeberg.org/splitcells-net/net.splitcells.network.git
  fi
  if [ ! -d ~/.local/state/${programName}/repos/public/net.splitcells.network.hub ]; then
    mkdir -p ~/.local/state/${programName}/repos/public/
    cd ~/.local/state/${programName}/repos/public/
    git clone https://codeberg.org/splitcells-net/net.splitcells.network.hub.git
  fi
  cd ~/.local/state/${programName}/repos/public/net.splitcells.network && git pull
  cd ~/.local/state/${programName}/repos/public/net.splitcells.network.hub && git pull
  cd ~/.local/state/${programName}/repos/public/net.splitcells.network
  ${bin_worker_execute} \\\n${remoteNetworkerArguments}
EOF"""

# The type simple is used, so that restarting the service is not an endlessly blocking operation.
SET_UP_SYSTEMD_SERVICE = """# Set up Systemd service
mkdir -p ${daemonFolder}
cat > ${daemonFile} <<SERVICE_EOL
[Unit]
Description=Execute ${executionName}

[Service]
Type=simple
StandardOutput=journal
ExecStart=""" + PODMAN_COMMAND_TEMPLATE + """
[Install]
WantedBy=default.target
SERVICE_EOL
systemctl --user daemon-reload
systemctl --user enable ${programName}.daemon
systemctl --user restart ${programName}.daemon
"""

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
    additionalArguments = ""
    def execute(self, configParser, config):
        self.configParser = configParser
        self.config = config
        if self.was_executed:
            raise Exception("A WorkerExecution instance cannot be executed twice.")
        self.was_executed = True
        self.bin_worker_execute = "bin/worker.execute.py"
        if config.execute_via_ssh_at is None:
            self.executeLocally()
        else:
            self.executeRemotelyViaSsh()
        if PODMAN_FLAGS_CONFIG_FILE.is_file():
            self.additionalArguments = (PODMAN_FLAGS_CONFIG_FILE.read_text() + '\\').replace('\n', '')
        else:
            self.additionalArguments = ''
            self.local_execution_script = self.local_execution_script.replace('${additionalArguments} \\', '\\')
    def filterArgumentsForRemoteScript(self, parsedVars, key):
        if key in ['execute_via_ssh_at', 'pull_network_log']:
            return False
        return parsedVars[key] is not None and self.configParser.get_default(key) != parsedVars[key]
    def executeRemotelyViaSsh(self):
        """This command should have no actual execution commands,
        but instead should only synchronize with the remote and generate appropriate `bin/worker.execute.py` commands,
        in order to delegate the actual execution commands.
        This makes `worker.execute.py` simplier."""
        self.username = self.config.execute_via_ssh_at.split("@")[0]
        preparingNetworkLogPullScript = None;
        closingpull_network_logScript = None;
        if self.config.pull_network_log:
            preparingNetworkLogPullScript = DEFAULT_NETWORK_PULL_SCRIPT
        else:
            preparingNetworkLogPullScript = ""
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
            execute_via_ssh_at = self.config.execute_via_ssh_at
            , username = self.username
            , name = self.config.program_name
            , remoteNetworkerArguments = self.remote_networker_arguments
            , daemonFolder = self.daemonFolder
            , daemonName = self.daemonName
            , daemonFile = self.daemonFile
            , bin_worker_execute = self.bin_worker_execute
            , programName = self.config.program_name
            , podmanParameters = self.additional_podman_args
            , executionName = self.config.execution_name
            , additionalArguments = self.additionalArguments)
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
        if not os.path.exists('./target'):
            os.mkdir('./target')
        # TODO Consoldiate Dockerfile template extensions, as every case can be solved via a dedicated shell script, that is the entrypoint of the Dockerfile.
        required_argument_count = 0
        self.docker_file = DOCKERFILE_SERVICE_TEMPLATE
        if self.config.command is not None:
            required_argument_count += 1
            self.docker_file += "ENTRYPOINT " + self.config.command + "\n"
        if self.config.executable_path is not None:
            required_argument_count += 1
            self.program_name = "program-" + self.config.program_name
            self.local_executable = ("#!/usr/bin/env sh\n"
                    + "export NET_SPLITCELLS_NETWORK_WORKER_NAME=" + self.config.program_name + "\n"
                    + "mkdir -p ~/.local/state/" + self.config.program_name + "/repos/public/net.splitcells.network\n"
                    + "cd ~/.local/state/" + self.config.program_name + "/repos/public/net.splitcells.network\n"
                    + Path(self.config.executable_path).read_text())
            if not self.config.dry_run:
                with open("./target/" + self.program_name, 'w') as executable_file:
                    executable_file.write(self.local_executable)
            self.docker_file += "ADD ./" + self.program_name + " /root/program\n"
            self.docker_file += 'ENTRYPOINT /root/program'
        if self.config.class_for_execution is not None:
            required_argument_count += 1
            self.docker_file += self.applyTemplate(JAVA_CLASS_EXECUTION_TEMPLATE)
            self.docker_file += self.docker_file.replace('$CLASS_FOR_EXECUTION', self.config.class_for_execution)
            if not self.config.dry_run:
                self.deployable_jars = Path(Path.home().joinpath('.local/state/' + self.config.program_name + '/repos/public/net.splitcells.network/target/' + self.config.program_name + '/deployable-jars/'))
                self.deployable_jars.mkdir(parents=True, exist_ok=True)
                shutil.copytree(str(Path.home().joinpath('.local/state/' + self.config.program_name + "/repos/public/" + self.config.source_repo + "/target/deployable-jars/"))
                    , str(self.deployable_jars)
                    , dirs_exist_ok=True)
        if required_argument_count == 0:
            raise Exception("Either `--command`, `--executable-path` or `--class-for-execution` needs to be set:" + str(self.config))
        if required_argument_count > 1:
            raise Exception("Exactly one of `--command`, `--executable-path` or `--class-for-execution` needs to be set, but " + required_argument_count + " were actually set.")
        if self.config.port_publishing is not None:
            for index, portMapping in enumerate(self.config.port_publishing.split(',')):
                if index != 0:
                    self.additional_podman_args += " "
                self.additional_podman_args += '--publish ' + portMapping
            self.additional_podman_args += " "
        if self.config.use_playwright:
            self.docker_file = self.docker_file.replace('$ContainerSetupCommand', 'RUN cd /root/opt/${NAME_FOR_EXECUTION}/ && mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"\n')
        else:
            self.docker_file = self.docker_file.replace('$ContainerSetupCommand', '\n')
        self.docker_file = self.docker_file.replace('${NAME_FOR_EXECUTION}', self.config.program_name)
        self.docker_file = self.docker_file.replace('${programName}', self.config.program_name)
        if not self.config.dry_run:
            file = 'target/Dockerfile-' + self.config.execution_name
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
        elif self.config.is_daemon:
            self.daemonFolder = "~/.config/systemd/user";
            self.daemonFile = self.daemonFolder + "/" + self.config.execution_name + ".service";
            self.local_execution_script += self.applyTemplate("\n" + SET_UP_SYSTEMD_SERVICE.replace("~/", "%h/"))
        else:
            self.local_execution_script += EXECUTE_VIA_PODMAN_TEMPLATE
        if self.config.command is not None:
            # TODO This does not seem to be valid or used anymore.
            self.local_execution_script = self.local_execution_script.replace('"$executionCommand"', "'" + self.config.command.replace("'", "'\\''") + "'")
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
            self.local_execution_script = self.local_execution_script.replace("-v ~/.local/state/${programName}/Documents:/root/Documents \\", "-v ~/Documents:/root/Documents \\")
        self.local_execution_script = self.local_execution_script.replace('${programName}', self.config.program_name)
        self.local_execution_script = self.applyTemplate(self.local_execution_script);
        # Execute program.
        if self.config.dry_run:
            logging.error("Generating script: " + self.local_execution_script)
            return
        if self.config.verbose:
            logging.info("Executing script: " + self.local_execution_script)
        return_code = subprocess.call(self.local_execution_script, shell='False') # The systems default shell is not used, because Fish and Bash are not compatible to each other in the slightest.
        if return_code != 0:
            logging.error("Could not execute given command.")
        exit(return_code)
def str2bool(arg):
    # The stringification of the truth boolean is `True` in Python 3 and therefore this capitalization is supported as well.
    return arg == 'true' or arg == 'True'
def parse_worker_execution_arguments(arguments):
    # If not remove the comment, and the hyphenless flag names. For each argument flag, there is an alternative name without hyphens ('-'), so these can easily be printed out and reused for this program. See `execute_via_ssh_at`.
    parser = argparse.ArgumentParser(description=PROGRAMS_DESCRIPTION)
    parser.add_argument('--program-name', dest='program_name', required=False, help="This is the name of the program for which something is being executed. This name is used to share data between executions of the program.")
    parser.add_argument('--execution-name', dest='execution_name', required=False, help="This is the name of the execution, which is executed for a program. Every call needs this argument. It is used to identify i.e. the container, that executes the given commands.")
    parser.add_argument('--bootstrap', dest='bootstrap', required=False, type=str2bool, default=False, help="If set to true, the source code of the Splitcells Network project is set up at this current computer.")
    parser.add_argument('--bootstrap-remote', dest='bootstrap_remote', required=False, help="This is the ssh address for bootstrapping in the form of `username@host`. If this is set, other parameters are set automatically as well, in order to bootstrap the Network repos on a remote server in a standardized way.")
    parser.add_argument('--test-remote', dest='test_remote', required=False, help="This is the ssh address for testing in the form of `username@host`. If this is set, other parameters are set automatically as well, in order to test the Network repos on a remote server in a standardized way.")
    parser.add_argument('--build-remote', dest='build_remote', required=False, help="This is the ssh address for building the Splitcells Network project in the form of `username@host`. If this is set, other parameters are set automatically as well, in order to build the Network repos on a remote server in a standardized way.")
    parser.add_argument('--pull-network-log', dest='pull_network_log', required=False, type=str2bool, default=True, help="If set to true, the repo `net.splitcells.network.log` will be pulled from the remote.")
    parser.add_argument('--command', required=False, dest='command', help=CLI_FLAG_COMMAND_HELP)
    parser.add_argument('--executable-path', '--executable_path', dest='executable_path', help="Executes the given executable file. Only set this option or --command.")
    parser.add_argument('--class-for-execution', '--class_for_execution', dest='class_for_execution', help="This Java class is executed. If this flag is given, --source-repo also has to be set.")
    parser.add_argument('--source-repo', dest='source_repo', help="Determines from which repo, the build jars are copied. If this flag is given, --class-for-execution also has to be set.")
    # TODO --use-host-documents is probably not needed anymore, as there is not a concrete use case for this at the moment.
    parser.add_argument('--use-host-documents', '--use_host_documents', dest='use_host_documents', required=False, type=str2bool, default=False, help="Determines whether to mount `~/Documents` or not. This should be avoided, in order to avoid file dependencies to the host system, which makes the execution more portable.")
    parser.add_argument('--publish-execution-image', '--publish_execution_image', dest='publish_execution_image', required=False, type=str2bool, default=False, help="If set to true, the given command is not executed, but a container image is published instead.")
    parser.add_argument('--verbose', dest='verbose', required=False, type=str2bool, default=False, help="If set to true, the output is verbose.")
    parser.add_argument('--only-build-image', '--only_build_image', dest='only_build_image', required=False, type=str2bool, default=False, help="If set to true, the created image is not executed.")
    parser.add_argument('--only-execute-image', '--only_execute_image', dest='only_execute_image', required=False, type=str2bool, default=False, help="If set to true, the previously created image is executed without building it.")
    parser.add_argument('--cpu-architecture', '--cpu_architecture', dest='cpu_architecture', help="Set the cpu architecture for the execution.")
    parser.add_argument('--dry-run', dest='dry_run', required=False, type=str2bool, default=False, help="If true, commands are only prepared and no commands are executed.")
    parser.add_argument('--is-daemon', '--is_daemon', dest='is_daemon', required=False, type=str2bool, default=False, help="If this is true, the process is executed as systemd user service in the background.")
    parser.add_argument('--use-playwright', '--use_playwright', dest='use_playwright', required=False, type=str2bool, default=False, help="If true, playwright is installed for the execution.")
    parser.add_argument('--auto-configure-cpu-architecture-explicitly', '--auto_configure_cpuArch_explicitly', dest='auto_configure_cpuArch_explicitly', required=False, type=str2bool, default=True, help=CLI_FLAG_AUTO_CPU_ARCH_HELP)
    parser.add_argument('--port-publishing', '--port_publishing', dest='port_publishing', help="This is a comma separated list of `host-port:container-port`, that describes the port forwarding on the host.")
    parser.add_argument('--execute-via-ssh-at', '--execute_via_ssh_at', dest='execute_via_ssh_at', help="Execute the given command at an remote server via SSH. The format is `[user]@[address/network name]`.")
    parsedArgs = parser.parse_args(arguments)
    workerExecution = WorkerExecution()
    if parsedArgs.source_repo is not None:
        if parsedArgs.class_for_execution is None:
            raise Exception("--source-repo is set, but --class-for-execution is not set.")
    if parsedArgs.class_for_execution is not None:
        if parsedArgs.source_repo is None:
            raise Exception("--class-for-execution is set, but --source-repo is not set.")
    if parsedArgs.program_name is None:
            parsedArgs.program_name = parsedArgs.execution_name
    if parsedArgs.program_name is None:
        parsedArgs.program_name = "net.splitcells.network.worker"
    if parsedArgs.command is not None:
        pass
    elif parsedArgs.bootstrap_remote is not None:
        parsedArgs.execute_via_ssh_at = parsedArgs.bootstrap_remote
        parsedArgs.command = "export NET_SPLITCELLS_NETWORK_WORKER_NAME=" + parsedArgs.program_name + " && cd ~/.local/state/" + parsedArgs.program_name + "/repos/public/net.splitcells.network && bin/worker.bootstrap"
        parsedArgs.bootstrap_remote = None
        if parsedArgs.execution_name is None:
            parsedArgs.execution_name = parsedArgs.program_name + '.boostrap'
    elif parsedArgs.test_remote is not None:
        parsedArgs.execute_via_ssh_at = parsedArgs.test_remote
        parsedArgs.command = "cd ~/.local/state/" + parsedArgs.program_name + "/repos/public/net.splitcells.network && bin/worker.bootstrap && bin/repos.test"
        parsedArgs.test_remote = None
        if parsedArgs.execution_name is None:
            parsedArgs.execution_name = parsedArgs.program_name + '.test.remote'
    elif parsedArgs.build_remote is not None:
        parsedArgs.execute_via_ssh_at = parsedArgs.build_remote
        parsedArgs.command = ("cd ~/.local/state/" + parsedArgs.program_name + "/repos/public/net.splitcells.network && bin/worker.bootstrap && bin/repos.build")
        parsedArgs.build_remote = None
        if parsedArgs.execution_name is None:
            parsedArgs.execution_name = parsedArgs.program_name + '.build.remote'
    elif parsedArgs.executable_path is not None:
        pass
    elif parsedArgs.class_for_execution is not None:
        pass
    else:
        raise Exception("Exactly one of the arguments --program-name, --executable-path, --test-remote, --class-for-execution or --bootstrap-remote has to be set, in order to execute this program.");
    if parsedArgs.execution_name is None:
        parsedArgs.execution_name = parsedArgs.program_name
    if parsedArgs.program_name is None and parsedArgs.execution_name is None:
        raise Exception("Either the --program-name or the --execution-name has to be given.")
    if parsedArgs.is_daemon and parsedArgs.execute_via_ssh_at is not None:
        parsedArgs.execution_name = parsedArgs.execution_name + ".daemon"
    workerExecution.execute(parser, parsedArgs)
    return workerExecution
class TestWorkerExecution(unittest.TestCase):
    maxDiff = None
    def test_only_build_image(self):
        test_subject = parse_worker_execution_arguments(['--program-name=net.splitcells.martins.avots.distro', '--executable-path=bin/worker.bootstrap', '--dry-run=true', '--only-build-image=true'])
        self.assertEqual(test_subject.local_execution_script, """
set -e

# Prepare file system.
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.m2/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.ssh/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.local/dumps/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/Documents/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/repos/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/bin/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/config/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/logs/
mkdir -p ./target/
test -f target/program-net.splitcells.martins.avots.distro && chmod +x target/program-net.splitcells.martins.avots.distro # This file does not exist, when '--executable-path' is not set.
cd ~/.local/state/net.splitcells.martins.avots.distro/repos/public/net.splitcells.network
podman build -f "target/Dockerfile-net.splitcells.martins.avots.distro" \\
    --tag "localhost/net.splitcells.martins.avots.distro"  \\
    --arch x86_64 \\
    \\
    --log-level=warn
""")
    def test_only_execute_image(self):
        test_subject = parse_worker_execution_arguments(['--program-name=net.splitcells.martins.avots.distro', '--executable-path=bin/worker.bootstrap', '--dry-run=true', '--only-execute-image=true'])
        self.assertEqual(test_subject.local_execution_script, """
set -e

# Prepare file system.
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.m2/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.ssh/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.local/dumps/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/Documents/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/repos/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/bin/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/config/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/logs/
mkdir -p ./target/
test -f target/program-net.splitcells.martins.avots.distro && chmod +x target/program-net.splitcells.martins.avots.distro # This file does not exist, when '--executable-path' is not set.


podman run --name "net.splitcells.martins.avots.distro" \\
  --network slirp4netns:allow_host_loopback=true \\
  \\
  --rm \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/Documents:/root/.local/state/net.splitcells.martins.avots.distro/Documents \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/.ssh:/root/.ssh \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/.m2:/root/.m2 \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/.local:/root/.local/state/net.splitcells.martins.avots.distro/.local \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/repos:/root/.local/state/net.splitcells.martins.avots.distro/repos \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/config:/root/.local/state/net.splitcells.martins.avots.distro/config \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/logs:/root/.local/state/net.splitcells.martins.avots.distro/logs \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/bin:/root/bin \\
  \\
  "localhost/net.splitcells.martins.avots.distro"
""")
    def test_bootstrap(self):
        test_subject = parse_worker_execution_arguments(['--program-name=net.splitcells.martins.avots.distro', '--executable-path=bin/worker.bootstrap', '--dry-run=true'])
        self.assertEqual(test_subject.local_execution_script, """
set -e

# Prepare file system.
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.m2/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.ssh/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/.local/dumps/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/Documents/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/repos/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/bin/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/config/
mkdir -p ~/.local/state/net.splitcells.martins.avots.distro/logs/
mkdir -p ./target/
test -f target/program-net.splitcells.martins.avots.distro && chmod +x target/program-net.splitcells.martins.avots.distro # This file does not exist, when '--executable-path' is not set.
cd ~/.local/state/net.splitcells.martins.avots.distro/repos/public/net.splitcells.network
podman build -f "target/Dockerfile-net.splitcells.martins.avots.distro" \\
    --tag "localhost/net.splitcells.martins.avots.distro"  \\
    --arch x86_64 \\
    \\
    --log-level=warn


podman run --name "net.splitcells.martins.avots.distro" \\
  --network slirp4netns:allow_host_loopback=true \\
  \\
  --rm \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/Documents:/root/.local/state/net.splitcells.martins.avots.distro/Documents \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/.ssh:/root/.ssh \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/.m2:/root/.m2 \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/.local:/root/.local/state/net.splitcells.martins.avots.distro/.local \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/repos:/root/.local/state/net.splitcells.martins.avots.distro/repos \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/config:/root/.local/state/net.splitcells.martins.avots.distro/config \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/logs:/root/.local/state/net.splitcells.martins.avots.distro/logs \\
  -v ~/.local/state/net.splitcells.martins.avots.distro/bin:/root/bin \\
  \\
  "localhost/net.splitcells.martins.avots.distro"
""")
    def test_bootstrap_remote(self):
        test_subject = parse_worker_execution_arguments(['--bootstrap-remote=user@address', '--dry-run=true'])
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
  if [ ! -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.hub ]; then
    mkdir -p ~/.local/state/net.splitcells.network.worker/repos/public/
    cd ~/.local/state/net.splitcells.network.worker/repos/public/
    git clone https://codeberg.org/splitcells-net/net.splitcells.network.hub.git
  fi
  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && git pull
  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.hub && git pull
  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network
  bin/worker.execute.py \\
    --command='export NET_SPLITCELLS_NETWORK_WORKER_NAME=net.splitcells.network.worker && cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap'\\
    --dry-run='true'\\
    --execution-name='net.splitcells.network.worker.boostrap'\\
    --program-name='net.splitcells.network.worker'

EOF

# Closing Execution via Network Log Pull
cd ../net.splitcells.network.log
git config remote.user@address.url >&- || git remote add user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url --push user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git pull user@address master
""")
    def test_bootstrap_remote_via_daemon(self):
        test_subject = parse_worker_execution_arguments(['--bootstrap-remote=user@address', '--is-daemon=true', '--dry-run=true'])
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
  if [ ! -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.hub ]; then
    mkdir -p ~/.local/state/net.splitcells.network.worker/repos/public/
    cd ~/.local/state/net.splitcells.network.worker/repos/public/
    git clone https://codeberg.org/splitcells-net/net.splitcells.network.hub.git
  fi
  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && git pull
  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.hub && git pull
  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network
  bin/worker.execute.py \\
    --command='export NET_SPLITCELLS_NETWORK_WORKER_NAME=net.splitcells.network.worker && cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap'\\
    --dry-run='true'\\
    --execution-name='net.splitcells.network.worker.boostrap.daemon'\\
    --is-daemon='true'\\
    --program-name='net.splitcells.network.worker'

EOF

# Closing Execution via Network Log Pull
cd ../net.splitcells.network.log
git config remote.user@address.url >&- || git remote add user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git remote set-url --push user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
git pull user@address master
""")
    def test_bootstrap_via_daemon(self):
        test_subject = parse_worker_execution_arguments(["--command='export NET_SPLITCELLS_NETWORK_WORKER_NAME=net.splitcells.network.worker && cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap'"
                                                            , '--dry-run=true'
                                                            , "--execution-name=net.splitcells.network.worker.boostrap.daemon"
                                                            , "--port-publishing=8080:8080"
                                                            , '--is-daemon=true'#
                                                            , "--program-name=net.splitcells.network.worker"])
        self.assertEqual(test_subject.local_execution_script, """
set -e

# Prepare file system.
mkdir -p ~/.local/state/net.splitcells.network.worker/.m2/
mkdir -p ~/.local/state/net.splitcells.network.worker/.ssh/
mkdir -p ~/.local/state/net.splitcells.network.worker/.local/dumps/
mkdir -p ~/.local/state/net.splitcells.network.worker/Documents/
mkdir -p ~/.local/state/net.splitcells.network.worker/repos/
mkdir -p ~/.local/state/net.splitcells.network.worker/bin/
mkdir -p ~/.local/state/net.splitcells.network.worker/config/
mkdir -p ~/.local/state/net.splitcells.network.worker/logs/
mkdir -p ./target/
test -f target/program-net.splitcells.network.worker && chmod +x target/program-net.splitcells.network.worker # This file does not exist, when '--executable-path' is not set.
cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network
podman build -f "target/Dockerfile-net.splitcells.network.worker.boostrap.daemon" \\
    --tag "localhost/net.splitcells.network.worker.boostrap.daemon"  \\
    --arch x86_64 \\
    \\
    --log-level=warn

# Set up Systemd service
mkdir -p ~/.config/systemd/user
cat > ~/.config/systemd/user/net.splitcells.network.worker.boostrap.daemon.service <<SERVICE_EOL
[Unit]
Description=Execute net.splitcells.network.worker.boostrap.daemon

[Service]
Type=simple
StandardOutput=journal
ExecStart=podman run --name "net.splitcells.network.worker.boostrap.daemon" \\
  --network slirp4netns:allow_host_loopback=true \\
  \\
  --rm \\
  -v %h/.local/state/net.splitcells.network.worker/Documents:/root/.local/state/net.splitcells.network.worker/Documents \\
  -v %h/.local/state/net.splitcells.network.worker/.ssh:/root/.ssh \\
  -v %h/.local/state/net.splitcells.network.worker/.m2:/root/.m2 \\
  -v %h/.local/state/net.splitcells.network.worker/.local:/root/.local/state/net.splitcells.network.worker/.local \\
  -v %h/.local/state/net.splitcells.network.worker/repos:/root/.local/state/net.splitcells.network.worker/repos \\
  -v %h/.local/state/net.splitcells.network.worker/config:/root/.local/state/net.splitcells.network.worker/config \\
  -v %h/.local/state/net.splitcells.network.worker/logs:/root/.local/state/net.splitcells.network.worker/logs \\
  -v %h/.local/state/net.splitcells.network.worker/bin:/root/bin \\
  --publish 8080:8080 \\
  "localhost/net.splitcells.network.worker.boostrap.daemon"

[Install]
WantedBy=default.target
SERVICE_EOL
systemctl --user daemon-reload
systemctl --user enable net.splitcells.network.worker.daemon
systemctl --user restart net.splitcells.network.worker.daemon
""")
if __name__ == '__main__':
    # As there is no build process for Python unit tests are executed every time, to make sure, that the script works correctly.
    # During this test info logging is disabled, which is disabled by default in Python.
    test_result = unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestWorkerExecution))
    logging.getLogger().setLevel(logging.INFO)
    if not test_result.wasSuccessful():
        raise Exception("The self test was not successful: " + str(unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestWorkerExecution))))
    parse_worker_execution_arguments(sys.argv[1:])
