#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

TODO IDEA Provide option to install commands to a custom folder,
     so that the installation does not have to change the users dotfiles.
     This way the installation is compatible to more environments and therefore more portable.
TODO Create tests.
TODO PERFORMANCE compile to C via https://cython.org/.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"
import argparse
from pathlib import Path
from pathlib import PosixPath
import shutil
from os import environ
import logging
def currentFolder():
	return Path.cwd()
class Command:
	def __init__(self, commandPath):
		commandPosixPath = PosixPath(commandPath)
		self.name = commandPosixPath.name
		self.sourceFolder = commandPosixPath.parent
		self.managedCommandNaming = "commandName = '" + self.name + "'"
		if 'NET_SPLITCELLS_SHELL_PATH' in environ:
			self.targetFolder = PosixPath(environ['NET_SPLITCELLS_SHELL_PATH'])
		else:
			self.targetFolder = Path.home()
		self.targetFolder = self.targetFolder.joinpath('bin', 'net.splitcells.shell.commands.managed')
	def install(self):
		executionCounter = 0
		targetFileName = self.name
		if (self.name.endswith('.sh')):
			targetFileName = self.name[:-3]
		if (self.name.endswith('.py')):
			targetFileName = self.name[:-3]
		targetFile = self.targetFolder.joinpath(targetFileName) # TODO Create test, where a command is installed and check if suffix processing works correctly.
		if targetFile.exists():
			while True:
				targetFile = self.targetFolder.joinpath(targetFileName + '.' + str(executionCounter))
				executionCounter+=1
				if not targetFile.exists():
					break
		shutil.copy(self.sourceFolder.joinpath(self.name), targetFile)
		if 'current_echo_level' in environ:
			if int(environ['current_echo_level']) >= 5:
				print(self.name + ' installed.')
if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(format='%(message)s', level=logging.DEBUG)
	else:
	    logging.basicConfig(format='%(message)s', level=logging.INFO)
	parser = argparse.ArgumentParser(description='Installs command to ~/bin and integrates commands already present.'
	    + 'Use the environment variable `NET_SPLITCELLS_SHELL_PATH` in order to change the target install directory.' ) # TODO If command already present, check if it is a manager. If this is not the case, throw an error.
	parser.add_argument('commandToInstall', nargs='?', type=str, help='The name of the command that is installed.')
	parsedArgs = parser.parse_args()
	Command(parsedArgs.commandToInstall).install()
