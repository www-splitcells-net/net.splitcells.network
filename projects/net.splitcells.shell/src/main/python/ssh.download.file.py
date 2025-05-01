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
from os import environ

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--remote-file-address', dest='remoteFileAddress', required=True, help="The format is `username@remote.host:remote/file/path`. The path is relative to the user's home folder.")
	parser.add_argument('--target-file', dest='targetFile', required=True)
	parsedArgs = parser.parse_args()
	commandToExecute = 'scp -r ' + parsedArgs.remoteFileAddress + ' ' + parsedArgs.targetFile
	logging.debug("Executing: " + commandToExecute)
	returnCode = subprocess.call(commandToExecute, shell='True')
	if returnCode != 0:
		logging.error('Error downloading file with return code ' + str(returnCode) + ' from scp.')
		exit(1)
