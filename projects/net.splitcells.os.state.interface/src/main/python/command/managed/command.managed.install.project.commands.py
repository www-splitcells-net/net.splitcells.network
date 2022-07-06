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

import argparse
from pathlib import Path
from pathlib import PosixPath
from os import environ
import logging
def currentFolder():
	return Path.cwd()
class Command:
	def __init__(self
			, projectPath
			, targetFolder = Path.home().joinpath('bin', 'net.splitcells.os.state.interface.commands.managed')
			):
		projectPosixPath = PosixPath(projectPath)
		self.projectName = projectPosixPath.name
		self.binFolder = projectPosixPath.joinpath('bin')
		self.targetFolder = targetFolder
	def install(self):
		logging.info("Installing project repository '" + self.projectName + "'.")
		for projectCommand in self.binFolder.rglob("*"):
			globalProjectCommandName = self.projectName + '.' + projectCommand.name
			targetCommandFile = self.targetFolder.joinpath(globalProjectCommandName)
			if targetCommandFile.exists():
				raise Exception('Cannot install ' + projectCommand.name + ' of project ' + self.projectName + ' to ' + str(targetCommandFile) + '.')
			targetCommandFile.write_text("#!/usr/bin/env sh\n"
			    + 'cd ' + str(projectCommand.resolve().parent.parent) + " \n"
			    + './bin/' + projectCommand.name + " $@\n")
if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(format='%(message)s', level=logging.DEBUG)
	else:
		logging.basicConfig(format='%(message)s', level=logging.INFO)
	parser = argparse.ArgumentParser(description='Installs projects command to ~/bin/net.splitcells.os.state.interface.commands.managed.')
	parser.add_argument('--project', nargs='?', type=str, help='Path to the project to be installed.')
	parsedArgs = parser.parse_args()
	if parsedArgs.project is None:
	    # A different file is used for the list of repos to be installed in this command and
	    # 'command.repositories.install'. The reason for this is, that a project repo cannot be installed
	    # like and command repo in OSSI, because not every file in the src folder of project repo can be used
	    # as an independent command.
		repoConfig = PosixPath('~/.config/net.splitcells.os.state.interface/project.repositories').expanduser()
		if repoConfig.exists():
			with open(repoConfig,'r') as projectRepositories:
				for projectRepository in projectRepositories.read().splitlines():
				    repoPrefix = 'repo='
				    Command(projectRepository).install()
		else:
			logging.debug('Nothing was installed, because no project repositories are defined.')
	else:
		Command(parsedArgs.project).install()
	