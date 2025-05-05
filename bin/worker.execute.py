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

class WorkerExecution:
    was_executed = False
    remote_execution_script = ""
    local_execution_script = ""
    docker_file = ""
    docker_file_path = ""
    program_name = ""