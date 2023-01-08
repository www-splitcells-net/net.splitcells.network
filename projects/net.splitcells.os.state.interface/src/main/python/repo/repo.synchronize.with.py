#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

'repo.repair' is executed before 'repo.is.clean', because the other way around fails, if some sub repos are missing locally.
'repo.repair' is executed before 'repo.remote.set', because the other way around can fail,
if a local repo is already delete on remote.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import argparse
import logging
from os import environ
import subprocess
if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--remote-host', dest='remoteHost')
	parser.add_argument('--remote-repo', dest='remoteRepo')
	parsedArgs = parser.parse_args()
	# TODO Do not create shell script, but call directly.
	# TODO Log errors to error stream.
	synchronizationScript = """system.network.peer.ssh.reachable {0} \\
	&& repo.repair --remote-repo={1} \\
	&& repo.remote.set {1} \\
	&& repo.is.clean \\
	&& repo.pull \\
	|| echo.error Could not synchronize with {0}.""".format(parsedArgs.remoteHost, parsedArgs.remoteRepo)
	logging.debug('Executing: ' + synchronizationScript)
	returnCode = subprocess.call(synchronizationScript, shell='True')
	if returnCode != 0:
		exit(1)