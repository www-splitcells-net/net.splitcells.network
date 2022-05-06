#!/usr/bin/env python3
"""
This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0, which is available at
http://www.eclipse.org/legal/epl-2.0, or the MIT License,
which is available at https://spdx.org/licenses/MIT.html.

Objective

Provide a way to create a collection of all file system based repositories of a user, which can be worked on as one.
This is especially useful for decentralized backups or if many projects needs to be organized/used.

Solution

Support processing a tree of repositories (meta repo) and therefore allows working on all repos as one
(i.e. in order to backup everything).

The following tree structure is recommended for the meta repo.
The tree should only have 3 levels of root folders, that are processed by this.
The first level consists of one folder and is the root of the meta repo.

The second level splits the repositories into organisational units like private and public repositories.
A minimal number of second level repositories is recommended in order to ease administration.
If there is no need for such organization the first and second level may be omitted.

The third level contains the roots of all repos containing the actual data.
There should be no repository roots in higher levels, except if it is managed by the backend (i.e. git submodules).
Only third level repositories should be assumed to be fully publicly portable,
because a flat meta repo structure is easiest to support by hosting platforms (i.e. Github, Gitlab, sourcehut etc).

The first and second level repositories are only used in order to organize third level repositories
by the user hosting the first and second level repository.
They are portable, but generally it is harder to migrate them to an other platform.

It is encouraged to use globally unique names for each repo in order to be able to minimize the number of second
level repositories.
Java package name convention is a good start for that.

Objective

The necessary meta info should be stored as simple and portable as possible.
In best case scenario only the relative paths of the sub repos and its possible remote servers has to be stored.

TODO Instead of "./.net.splitcells.os.state.interface.repo/subs.json" use the
     more simple "./.net.splitcells.os.state.interface.repo/subs.txt",
     which needs to be defined yet.
     The reason for this migration is the fact,
     that the JSON format is too complex yet
     and unnecessary hard to process.
TODO Use only python for recursion and not shell, in order simplify command.
TODO Improve documentation.
TODO Remove duplicate code.
TODO Support moving child repositories semi-automatically.
TODO Create compatibility tooling for alternative like meta: https://github.com/mateodelnorte/meta
     This would create metadata files for the meta tool,
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import subprocess
import sys
import argparse
import json
import logging
from os import (environ, getcwd)
from pathlib import Path


def execute(relativePath, host, command):
	logging.debug('relativePath: ' + relativePath)
	command = command.replace('$subRepo', '')
	logging.debug('	command: ' + command)
	returnCode = subprocess.call(command, shell='True')
	if returnCode != 0:
		logging.error('	Error processing repository with return code ' + str(returnCode) + '.')
		return False
	return True
def process(relativePath, host, command, commandForMissing, commandForUnknown, commandForCurrent, commandForChildren):
	if not execute(relativePath, host, command):
		return False
	subListPath=Path('./.net.splitcells.os.state.interface.repo/subs.json')
	if subListPath.is_file():
		# TODO Check if "!/.net.splitcells.os.state.interface.repo/" is present in gitignore. If not pr
		with open(subListPath, 'r') as subListFile:
			repoList=json.load(subListFile)
			for currentSubDir in Path('.').iterdir():
				if not currentSubDir.name.startswith('.') and currentSubDir.is_dir():
					subName = currentSubDir.name
					if not currentSubDir.name in repoList['subs']:
						unknownSubRepoScript = 'set -e; cd ' + subName + ' ; repo.process' + " --command='" + commandForUnknown + "' --host=" + host + ' --relative-path=' + relativePath
						unknownSubRepoScript = unknownSubRepoScript.replace('$subRepo', relativePath + '/' + subName + '/$subRepo')
						logging.debug('unknownSubRepoScript: ' + unknownSubRepoScript)
						returnCode = subprocess.call(unknownSubRepoScript, shell='True')
						if returnCode != 0:
							logging.error('Error processing unknown sub repository with return code ' + str(returnCode) + '.')
							return False
			for subName in repoList['subs'].keys():
				subRepoPath=Path('./' + subName)
				if not subRepoPath.is_dir():
					# TODO Echo to stderr.
					print('Folder of subrepository "' + str(subRepoPath) + '" is missing.')
					missingSubRepoScript = 'set -e; mkdir -p ' + subName + '; ' + 'cd ' + subName + ' ; repo.process' + " --command='" + commandForMissing + "' --host=" + host + ' --relative-path=' + relativePath
					missingSubRepoScript += " --command-for-missing='" + commandForMissing + "'"
					missingSubRepoScript += " --command-for-unknown='" + commandForUnknown + "'"
					missingSubRepoScript = missingSubRepoScript.replace('$subRepo', relativePath + '/' + subName + '/$subRepo')
					logging.debug('missingSubRepoScript: ' + missingSubRepoScript)
					returnCode = subprocess.call(missingSubRepoScript, shell='True')
					if returnCode != 0:
						logging.error('Error processing missing sub repository with return code ' + str(returnCode) + " at '" + str(getcwd()) + '/' + subName + "'.")
						return False
					continue
				r='repo.process'
				# TODO Do not rely on Bash specific command syntax.
				currentCommand = ''
				if command is not None:
					currentCommand = command
				elif commandForCurrent is not None:
					currentCommand = commandForCurrent
				else:
					logging.error("No commands present. Please specify argument '--command=[...]' or '--command-for-current=[...]'.")
					return False
				subRepoScript = 'set -e; mkdir -p ' + subName + '; ' + 'cd ' + subName + ' ; ' + r + " --command='" + currentCommand + "' --host=" + host + ' --relative-path=' + relativePath
				subRepoScript += " --command-for-missing='" + commandForMissing + "'"
				subRepoScript += " --command-for-unknown='" + commandForUnknown + "'"
				if commandForCurrent is not None:
					subRepoScript += " --command-for-current='" + commandForCurrent + "'"
				if commandForChildren is not None:
					subRepoScript += " --command-for-children='" + commandForChildren + "'"
				subRepoScript = subRepoScript.replace('$subRepo', relativePath + '/' + subName + '/$subRepo')
				logging.debug('subRepoScript: ' + subRepoScript)
				returnCode = subprocess.call(subRepoScript, shell='True')
				if returnCode != 0:
					logging.error('Error processing sub repository with return code ' + str(returnCode) + " at '" + str(getcwd()) + '/' + subName + "'.")
					return False
	return True
if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser(description="Setting the environment variable 'log_level' to 'debug', enables debug level logging.")
	parser.add_argument('--relative-path', dest='relativePath', default='./')
	parser.add_argument('--host', dest='host', default="''")
	parser.add_argument('--command', dest='command', required=True)
	parser.add_argument('--command-for-missing', dest='commandForMissing', default="exit 1")
	parser.add_argument('--command-for-unknown', dest='commandForUnknown', default="exit 1")
	parser.add_argument('--command-for-current', dest='commandForCurrent', required=False) # TODO What is the purpose of this?
	parser.add_argument('--command-for-children', dest='commandForChildren', required=False)
	parsedArgs = parser.parse_args()
	if not process(parsedArgs.relativePath, parsedArgs.host, parsedArgs.command, parsedArgs.commandForMissing, parsedArgs.commandForUnknown, parsedArgs.commandForCurrent, parsedArgs.commandForChildren):
		exit(1)