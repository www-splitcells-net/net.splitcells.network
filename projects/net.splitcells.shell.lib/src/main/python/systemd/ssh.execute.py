#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

* TODO Return exit code of shell command execution.
* TODO Add flags in order to execute task in background and remove `ssh.execute.in.background`
  command in order to avoid duplicate code.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import argparse
import logging
import subprocess

from os import environ

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level = logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--host', required = True, help = 'user@host')
	parser.add_argument('--command', required = True, type = str)
	parsedArgs = parser.parse_args()
	# '-t' prevents error, when a command like sudo is executed.
	commandToExecute = "ssh -t"\
	 	+ " " + parsedArgs.host\
	 	+ " '" + parsedArgs.command + "'"
	logging.debug("Executing: " + commandToExecute)
	subprocess.call(commandToExecute, shell='True')
