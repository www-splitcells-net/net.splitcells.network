#!/usr/bin/env python3

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import subprocess
import sys
import argparse
import json
import logging
from os import environ
from pathlib import Path

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--source-file', dest='sourceFile', required=True)
	parser.add_argument('--target-address', dest='targetAddress', required=True)
	parsedArgs = parser.parse_args()
	commandToExecute = 'scp ' + sourceFile + ' ' targetAddress
	logging.debug("Executing: " + commandToExecute)
	subprocess.call(commandToExecute, shell='True')
	returnCode = subprocess.call(subRepoScript, shell='True')
	if returnCode != 0:
		logging.error('Error uploading file with return code ' + str(returnCode) + ' from scp.')
		return False