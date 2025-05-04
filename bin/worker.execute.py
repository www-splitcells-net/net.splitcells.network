#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

TODO Create test for all `worker.execute` flags.
TODO Create option to delete things like `.m2`, `repos` and `net.splitcells.shell.commands.managed`,
     so it can be made sure, that bootstrapping actually works.
TODO Use string's substitute method, instead of string's replace.
TODO Support deployment to Docker and Kubernetes.
TODO Support executing as systemd service. Create a user service for that.
TODO Currently, there is not distinction between the name of the thing being executed and its namespace.
     This will probably create problems in the future, where you have multiple containers with the same namespace,
     but with different commands.
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
