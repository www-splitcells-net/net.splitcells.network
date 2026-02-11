#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

TODO Repo process should only create one shell script, instead of doing everything interactively.
     This makes it easier to test, debug and understand things.
     See the project's Python guidelines.
TODO Repo process, repair or copy do only output debug message if a sub repo is unknown.
TODO Always use dedicated remotes, as its otherwise harder to work on such repos by hand.
TODO Use a config object instead of arguments, in order to simplify code.
TODO Use only python for recursion and not shell, in order simplify command.
TODO Document that `repo.synchronize` etc. only work on default branch by default.
     This is done in order to avoid complex synchronization in case of deleted branches by default.
     If branches are explicitly deleted somewhere, there needs to be a way to state, that this branch is deleted.
     Otherwise, the branch will be restored by the other repositories.
TODO Ensure that during repo cloning the default branch for origin is set.
TODO Make it possible, to store the default branch in a file in the repo.
     Check this file, if present, and throw an error if the current branch does not match default branch.
TODO Repo process should have an optional flag in order to only process the current repo.
     This would than be used in order to execute complex commands on the current repo first and than on its sub repos.
     For example the command "repos.process 'repo.repair [...] && repo.remote.set [...] && repo.pull [...]'",
     would than work more reliable, when the remote server deletes sub repos at arbitrary times.
TODO Instead of "./.net.splitcells.os.state.interface.repo/subs.json" use the
     more simple "./.net.splitcells.os.state.interface.repo/subs.txt",
     which needs to be defined yet.
     The reason for this migration is the fact,
     that the JSON format is too complex yet
     and unnecessary hard to process.
     Maybe use an executable like "./bin/repo.subs.sh" instead, in order to support as much as possible
     (i.e. this could be used in order to make repo process compatible to git submodules).
TODO Improve documentation and make it friendly to new users.
TODO Remove duplicate code.
TODO Support moving child repositories semi-automatically.
TODO Create compatibility tooling for alternative like meta: https://github.com/mateodelnorte/meta
     This would create metadata files for the meta tool,
TODO Make it possible to `repo.synchronize` etc. all branches of a repo.
     Create a new peer meta repo, that contains the names of all deleted branches.
     Beware that different branches can be created over time with the same name.
     The meta repo therefore needs to store some commit info as well and not just the name of the deleted branch.
     Only using fast forward pushes and merges seems to be a good method,
     that works great, if no conflicts are present.
     It may be enough, to fix conflicts always by hand, as these should not occur often.
     Some kind of options or flags could be used in the future,
     in order to resolve certain conflicts.
TODO Maybe repo process should support just being a wrapper around git submodules,
     that provides easy workflows.
TODO Handle deletion of sub repos by remote during repo synchronization.
TODO Support dry run for easier debugging or in order to generate shell scripts.
TODO Create a way to list a sub repos, in order to create a list of repos.
     This can be used, in order to create config files for other git management software.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import subprocess
import argparse
import json
import logging
import re
import sys
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
def process(relativePath, host, command, commandForMissing, commandForUnknown, commandForCurrent, commandForChildren, ignorePeerRepos):
	if not execute(relativePath, host.replace('$peerRepo', ''), command):
		return False
	if ignorePeerRepos == 'false':
		peerListPath = Path('./bin/net.splitcells.shell.repos.peers')
		if peerListPath.is_file() and environ.get('net_splitcells_os_state_interface_repo_process_repo_peer_disabled') != '1':
			peers = subprocess.run([peerListPath], stdout=subprocess.PIPE)
			for peerRepo in peers.stdout.decode('utf-8').split("\n"):
				if peerRepo.strip() != "":
					subRepoPath = Path('../' + peerRepo)
					returnCode = processSub(relativePath + '/../' + peerRepo
											, host
											, re.sub(r'/[a-z\.]*/../', '/', command.replace('$peerRepo', '/../' + peerRepo))
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
		raise Exception('`./.net.splitcells.os.state.interface.repo/subs.json` is present, but deprecated and unsupported.')
	subListPath=Path('./.net.splitcells.os.state.interface.repo/sub-repo-names')
	if subListPath.is_file():
		raise Exception('`./.net.splitcells.os.state.interface.repo/sub-repo-names` is present, but deprecated and unsupported.')
	subListPath=Path('./bin/net.splitcells.repos.children')
	if subListPath.is_file():
		subListQuery = subprocess.run([subListPath], stdout=subprocess.PIPE)
		subRepos = []
		for subRepo in subListQuery.stdout.decode('utf-8').split("\n"):
			if not subRepo == "" and not subRepo.isspace():
				subRepos.append(subRepo)
		for currentSubDir in Path('.').iterdir():
			# Hidden files aka dotfiles and the bin folder are ignored.
			if currentSubDir.is_dir() and not currentSubDir.name.startswith('.') and not currentSubDir.name.startswith('bin'):
				subName = currentSubDir.name
				if not subName in subRepos:
					unknownSubRepoScript = 'set -e; cd ' + subName + ' ; repos.process' + " --command='" + commandForUnknown + "' --host=" + host + ' --relative-path=' + relativePath + '/' + subName
					unknownSubRepoScript = unknownSubRepoScript.replace('$subRepo', relativePath + '/' + subName + '/$subRepo')
					logging.debug('unknownSubRepoScript: ' + unknownSubRepoScript)
					returnCode = subprocess.call(unknownSubRepoScript, shell='True')
					if returnCode != 0:
						logging.error('Error processing unknown sub repository with return code ' + str(returnCode) + '.')
						return False
		for subName in subRepos:
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
		if commandForMissing is None:
			logging.error('Folder of sub repository "' + str(subRepoPath) + '" is missing.')
			return False
		missingSubRepoScript = 'set -e; mkdir -p ' + str(subRepoPath) + '; ' + 'cd ' + str(subRepoPath) + ' ; repos.process' + " --command='" + commandForMissing + "' --host=" + host + ' --relative-path=' + relativePath
		missingSubRepoScript += " --command-for-missing='" + commandForMissing + "'"
		missingSubRepoScript += " --command-for-unknown='" + commandForUnknown + "'"
		missingSubRepoScript = missingSubRepoScript.replace('$subRepo', str(subRepoPath) + '/$subRepo')
		logging.debug('missingSubRepoScript: ' + missingSubRepoScript)
		returnCode = subprocess.call(missingSubRepoScript, shell='True')
		if returnCode != 0:
			logging.error('Error processing missing sub repository with return code ' + str(returnCode) + " at '" + str(getcwd()) + '/' + subName + "'.")
			return False
		return True
	r='repos.process'
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
	if commandForMissing is not None:
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
	if environ.get('repo_process_v2') == '1':
		print('Using ' + str(['repos.process.v2'] + sys.argv[1:]))
		exit(subprocess.call(['repos.process.v2'] + sys.argv[1:]))
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser(formatter_class=argparse.RawTextHelpFormatter)
	parser.add_argument('--relative-path', dest='relativePath', default='./', help="This is path of the currently processed repo.")
	parser.add_argument('--host', dest='host', default="''")
	parser.add_argument('--command', dest='command', required=True)
	parser.add_argument('--command-for-missing', dest='commandForMissing', required=False)
	parser.add_argument('--command-for-unknown', dest='commandForUnknown', default='exit 1')
	parser.add_argument('--command-for-current', dest='commandForCurrent', required=False) # TODO What is the purpose of this?
	parser.add_argument('--command-for-children', dest='commandForChildren', required=False)
	parser.add_argument('--ignore-peer-repos', dest='ignorePeerRepos', required=False, default='false')
	parsedArgs = parser.parse_args()
	if not process(parsedArgs.relativePath, parsedArgs.host, parsedArgs.command, parsedArgs.commandForMissing, parsedArgs.commandForUnknown, parsedArgs.commandForCurrent, parsedArgs.commandForChildren, parsedArgs.ignorePeerRepos):
		exit(1)