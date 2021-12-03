#!/usr/bin/env python3
"""
This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0, which is available at
http://www.eclipse.org/legal/epl-2.0, or the MIT License,
which is available at https://spdx.org/licenses/MIT.html.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

# TODO https://stackoverflow.com/questions/1523427/what-is-the-common-header-format-of-python-files
# TODO calculate return code
import subprocess
import sys
import argparse
import logging
from os import environ
from pathlib import Path
from shutil import which
# TODO FIXME If file not present in path this script should continue.
# TODO listener scripts
def executeCommand(command):
	"""Returns whether the next commands needs to be executed."""
	# TODO Check if command exsists.
	# TODO Create option to enable trace logging via environment.
	logging.debug('Executing \"' + ' '.join(command) + '\".')
	returnCode = subprocess.call(command) # This call raises an error if the command is not found.
	return returnCode == 0
def managedCommandExecution(type, command): # RENAME
	"""If the system does not change significantly and this method is called with the same arguments, the same commands should be executed.
	Keep in mind that the commands that will be executed next in a given stage, are largely determined by the return value of the previously executed commands' return values.
	Otherwise, more resources are used than necessary (i.e. system.update commands that require restarts).
	
	Each executed command should take a finite amount of time, as otherwise managed commands may not be executed correctly.
	(i.e. "system.update": if one update command waits for new updates, it is also blocking other update commands.
	It is preferred that each executed command takes up a small amount of time.
	
	TODO Currently the echo of executed commands are not filtered.
	This was attempted as this seems to be a useful in order to remove unnecessary information.
	If this feature is attempted again this should be develop in an own branch.
	Also this feature definitely requires testing.
	Another challenge is the output of color etc..
	Last time it was attempted colors were not present.
	See following commits of the last attempt:
	* 766e6fe1b11c2ba301a763471592d345c0bf4176 Filter output of managed command execution:
	* e1bb175e57fa4671a2e0a3a5a4ad1a9928d25bbd Echo output of managed conjunction execution not as error.
	"""
	executionCounter = 0
	managedCommand = commandAt(command, executionCounter)
	executedSuccessfully = False
	while commandExists(managedCommand[0]):
		executedSuccessfully = executeCommand(managedCommand)
		if type == 'disjunction' and executedSuccessfully:
			executedSuccessfully = True
			break
		if type == 'conjunction' and not executedSuccessfully:
			executedSuccessfully = False
			break
		if type == 'conjunction' and executedSuccessfully:
			executedSuccessfully = True
		executionCounter+=1
		managedCommand = commandAt(command, executionCounter)
	if not executedSuccessfully:
		logging.error('Could not execute \"' + ' '.join(command) + '\".')
	return executedSuccessfully
def commandAt(command, number):
	rVal = command.copy()
	rVal[0] = rVal[0] + '.' + str(number)
	return rVal
def commandExists(commandName):
	"""Check whether `name` is on PATH."""
	return which(commandName) is not None
if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(format='%(message)s', level=logging.DEBUG)
	else:
		logging.basicConfig(format='%(message)s')
	wasExecutedSuccessfully = managedCommandExecution(sys.argv[1], sys.argv[2:])
		# Managed commands are sometimes mixed with other commands, that do not follow the conventions of this project.
		# The last console line is cleared, in case the last echo was filtered and is still displayed.
	if wasExecutedSuccessfully:
		sys.exit(0)
	else:
		sys.exit(1)