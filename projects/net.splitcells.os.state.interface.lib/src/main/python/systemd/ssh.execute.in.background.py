#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import argparse
import logging
import subprocess

from os import environ

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level = logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--host', required = True, help = 'user@host')
	parser.add_argument('--task-name', required = True , dest = 'taskName')
	parser.add_argument('--command', required = True)
	parsedArgs = parser.parse_args()
	# '-t' prevents error, when a command like sudo is executed.
	commandToExecute = "ssh -t"\
	 	+ " " + parsedArgs.host\
	 	+ " systemd-run --unit=" + parsedArgs.taskName\
	 	+ " " + parsedArgs.command
	logging.debug("Executing: " + commandToExecute)
	subprocess.call(commandToExecute, shell='True')
