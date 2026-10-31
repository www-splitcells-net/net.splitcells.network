#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import subprocess
import argparse
import logging
from os import (environ, getcwd, path)
from pathlib import Path

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--remote-repo', dest='remoteRepo')
	parser.add_argument('--remote-name', dest='remoteName')
	parsedArgs = parser.parse_args()
	script = """
	    set -e
	    set -x
	    repo.is.git
	    git remote get-url {remoteName} && git remote set-url --push {remoteName} {remoteRepo} || git remote add {remoteName} {remoteRepo}
	""".format(remoteRepo = parsedArgs.remoteRepo, remoteName = parsedArgs.remoteName)
	logging.debug("Executing: " + script)
	returnCode = subprocess.call(script, shell='True')
	if returnCode != 0:
		exit(1)