#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import subprocess
import argparse
import logging
from os import environ

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--source-file', dest='sourceFile', required=True)
	parser.add_argument('--target-address', dest='targetAddress', required=True)
	parsedArgs = parser.parse_args()
	commandToExecute = 'scp ' + sourceFile + ' ' + targetAddress
	logging.debug("Executing: " + commandToExecute)
	subprocess.call(commandToExecute, shell='True')
	returnCode = subprocess.call(subRepoScript, shell='True')
	if returnCode != 0:
		logging.error('Error uploading file with return code ' + str(returnCode) + ' from scp.')
		exit(1)