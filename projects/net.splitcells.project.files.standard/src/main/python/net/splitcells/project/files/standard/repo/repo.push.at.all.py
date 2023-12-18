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
	parser = argparse.ArgumentParser(description="""Pushes repo to all hosts.
The command `./bin/net.splitcells.osi.repos.hosts` contains the list of all repo's hosts.""")
	parsedArgs = parser.parse_args()
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level = logging.DEBUG)
	hostListPath = Path('./bin/net.splitcells.osi.repos.hosts')
	if hostListPath.is_file():
		hosts = subprocess.run([hostListPath], stdout=subprocess.PIPE)
		hostFile = hosts.stdout.decode('utf-8').split("\n")
		currentDirectory = getcwd()
		currentDirectoryName = path.basename(currentDirectory)
		for i in range(0, int(len(hostFile)/2)):
			hostName = hostFile[i*2]
			hostUrl = hostFile[i*2 + 1]
			commandToExecute = 'repo.push.at --remote-repo-name=' + hostName + " --remote-repo-URL='" + hostUrl.replace('$1', currentDirectoryName + "$peerRepo'")
			logging.debug('Executing: ' + commandToExecute)
			returnCode = subprocess.call(commandToExecute, shell='True')
			if returnCode != 0:
				logging.error('Error pushing the repository `' + currentDirectory + '` to `' + hostName + '` with return code ' + str(returnCode) + '.')
				exit(1)