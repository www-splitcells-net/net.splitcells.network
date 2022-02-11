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
	parser = argparse.ArgumentParser(description="Authorizes ssh keys for the user, so that user can use it for ssh login via pub key. This command does not configure the ssh service.")
	parser.add_argument('--ssh-file', dest='sshPubKeyFile', required=True) # TODO Support '~'.
	parsedArgs = parser.parse_args()
	keysToImport = ""
	authorizedKeys = ""
	with open(Path.home().joinpath(".ssh", "authorized_keys"), 'r') as authorizedKeyFile:
		authorizedKeys = authorizedKeyFile.read()
	with open(parsedArgs.sshPubKeyFile, 'r') as sshPubKeyFile:
		for line in sshPubKeyFile.readlines():
			if line not in authorizedKeys:
				logging.debug('Importing public ssh key for user: ' + line)
				keysToImport += line + "\n"
	with open(Path.home().joinpath(".ssh", "authorized_keys"), "a") as authorizedKeysFile:
		authorizedKeysFile.write(keysToImport)