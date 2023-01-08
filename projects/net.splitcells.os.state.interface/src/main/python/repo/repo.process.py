#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

TODO Document that `repo.synchronize` etc. only work on default branch by default.
     This is done in order to avoid complex synchronization in case of deleted branches by default.
TODO Ensure that during repo cloning the default branch for origin is set.
TODO Make it possible, to store the default branch in a file in the repo.
     Check this file, if present, and throw an error if the current branch does not match default branch.
TODO Repo process should have an optional flag in order to only process the current repo.
     This would than be used in order to execute complex commands on the current repo first and than on its sub repos.
     For example the command "repo.process 'repo.repair [...] && repo.remote.set [...] && repo.pull [...]'",
     would than work more reliable, when the remote server deletes sub repos at arbitrary times.
TODO Ignore peer repos with explicit flag and via environment (net_splitcells_os_state_interface_repo_process_repo_peer_disabled).
     Ignoring peers is required in order to synchronize meta repos more reliably automatically.


TODO Instead of "./.net.splitcells.os.state.interface.repo/subs.json" use the
     more simple "./.net.splitcells.os.state.interface.repo/subs.txt",
     which needs to be defined yet.
     The reason for this migration is the fact,
     that the JSON format is too complex yet
     and unnecessary hard to process.
TODO Use only python for recursion and not shell, in order simplify command.
TODO Improve documentation and make it friendly to new users.
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
import argparse
import json
import logging
import re
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
	if not execute(relativePath, host.replace('$peerRepo', ''), command):
		return False
	peerListPath = Path('./bin/net.splitcells.osi.repos.peers')
	if peerListPath.is_file() and environ.get('net_splitcells_os_state_interface_repo_process_repo_peer_disabled') == '1':
		peers = subprocess.run([peerListPath], stdout=subprocess.PIPE)
		for peerRepo in peers.stdout.decode('utf-8').split("\n"):
			if peerRepo.strip() != "":
				subRepoPath = Path('../' + peerRepo)
				returnCode = processSub(relativePath + '/../' + peerRepo
										, host
										, re.sub('/[a-z\.]*/../', '/', command.replace('$peerRepo', '/../' + peerRepo))
										, commandForMissing
										, commandForUnknown
										, commandForCurrent
										, commandForChildren
										, peerRepo
										, subRepoPath)
				if not returnCode:
					return returnCode
	command = command.replace('$peerRepo', '')
	subListPath=Path('./.net.splitcells.os.state.interface.repo/subs.json')
	if subListPath.is_file():
		# TODO Check if "!/.net.splitcells.os.state.interface.repo/" is present in gitignore. If not pr
		with open(subListPath, 'r') as subListFile:
			repoList=json.load(subListFile)
			for currentSubDir in Path('.').iterdir():
				if not currentSubDir.name.startswith('.') and currentSubDir.is_dir():
					subName = currentSubDir.name
					if not subName in repoList['subs']:
						unknownSubRepoScript = 'set -e; cd ' + subName + ' ; repo.process' + " --command='" + commandForUnknown + "' --host=" + host + ' --relative-path=' + relativePath + '/' + subName
						unknownSubRepoScript = unknownSubRepoScript.replace('$subRepo', relativePath + '/' + subName + '/$subRepo')
						logging.debug('unknownSubRepoScript: ' + unknownSubRepoScript)
						returnCode = subprocess.call(unknownSubRepoScript, shell='True')
						if returnCode != 0:
							logging.error('Error processing unknown sub repository with return code ' + str(returnCode) + '.')
							return False
			for subName in repoList['subs'].keys():
				subRepoPath=Path('./' + subName)
				returnCode = processSub(relativePath
										, host
										, command
										, commandForMissing
										, commandForUnknown
										, commandForCurrent
										, commandForChildren
										, subName
										, subRepoPath)
				if not returnCode:
					return returnCode
	return True
def processSub(relativePath, host, command, commandForMissing, commandForUnknown, commandForCurrent, commandForChildren, subName, subRepoPath):
	if not subRepoPath.is_dir():
		logging.error('Folder of sub repository "' + str(subRepoPath) + '" is missing.')
		missingSubRepoScript = 'set -e; mkdir -p ' + str(subRepoPath) + '; ' + 'cd ' + str(subRepoPath) + ' ; repo.process' + " --command='" + commandForMissing + "' --host=" + host + ' --relative-path=' + relativePath
		missingSubRepoScript += " --command-for-missing='" + commandForMissing + "'"
		missingSubRepoScript += " --command-for-unknown='" + commandForUnknown + "'"
		missingSubRepoScript = missingSubRepoScript.replace('$subRepo', str(subRepoPath) + '/$subRepo')
		logging.debug('missingSubRepoScript: ' + missingSubRepoScript)
		returnCode = subprocess.call(missingSubRepoScript, shell='True')
		if returnCode != 0:
			logging.error('Error processing missing sub repository with return code ' + str(returnCode) + " at '" + str(getcwd()) + '/' + subName + "'.")
			return False
		return True
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
	subRepoScript = 'set -e; mkdir -p ' + str(subRepoPath) + '; ' + 'cd ' + str(subRepoPath) + ' ; ' + r + " --command='" + currentCommand + "' --host=" + host + ' --relative-path=' + relativePath
	subRepoScript += " --command-for-missing='" + commandForMissing + "'"
	subRepoScript += " --command-for-unknown='" + commandForUnknown + "'"
	if commandForCurrent is not None:
		subRepoScript += " --command-for-current='" + commandForCurrent + "'"
	if commandForChildren is not None:
		subRepoScript += " --command-for-children='" + commandForChildren + "'"
	subRepoScript = subRepoScript.replace('$subRepo', str(subRepoPath) + '/$subRepo')
	logging.debug('subRepoScript: ' + subRepoScript)
	returnCode = subprocess.call(subRepoScript, shell='True')
	if returnCode != 0:
		logging.error('Error processing sub repository with return code ' + str(returnCode) + " at '" + str(getcwd()) + '/' + subName + "'.")
		return False
	return True
if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser(description="""
Generates and executes a set of commands over a set of repositories.

Setting the environment variable 'log_level' to 'debug', enables debug level logging.

All command arguments support the variables $subRepo and $peerRepo.
`$subRepo` is replaced with the relative path from the root --relative-path to the next child repo folder, that will be processed next.
`$peerRepo` is replaced with the relative path from the root --relative-path to the next peer repo folder, that will be processed next.
These variables make it easy to create commands, that pull or push a tree of repos to a remote server by constructing the appropriate URLs via these variables.
The variable `$subRepo` is used mainly to push a tree of git repos to another server.
This is often useful, when the target SSH server is hosted by oneself and supports complex repo structures.
The variable `$peerRepo` is used mainly to push a single folder of a git repo.
This is often useful, when the target server is a public server, where a user often can only manage a single folder of git repos. 
""")
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