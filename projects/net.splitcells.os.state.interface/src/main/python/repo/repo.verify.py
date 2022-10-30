#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

# Repo verify throws an error, if the current folder is not the root folder of a repository.

import subprocess
from pathlib import Path
import json
import sys
print(Path('.').resolve())
returnCode = subprocess.call('command.managed.execute conjunction repo.verify', shell='True')
if returnCode != 0:
	sys.exit(1)

subListPath=Path('./.net.splitcells.os.state.interface.repo/subs.json')
if subListPath.is_file():
	with open(subListPath, 'r') as subListFile:
		repoList=json.load(subListFile)
		for subName in repoList['subs'].keys():
			subRepoPath=Path('./' + subName)
			if not subRepoPath.is_dir():
				print('Folder of subrepository "' + str(subRepoPath) + '" is missing.')
				# TODO Echo to stderr.
				sys.exit(1)
		for currentSubDir in Path('.').iterdir():
			if not currentSubDir.name.startswith('.') and currentSubDir.is_dir():
				if not currentSubDir.name in repoList['subs']:
					print('Unknown subrepository "' + currentSubDir.name + '" is present.')
					sys.exit(1)
		for subName in repoList['subs'].keys():
			if Path('./' + subName).is_dir():
				returnCode = subprocess.call('cd ' + subName + '; repo.verify', shell='True')
				if returnCode != 0:
					sys.exit(1)
