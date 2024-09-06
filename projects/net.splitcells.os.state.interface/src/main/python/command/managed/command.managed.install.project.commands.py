#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

TODO Avoid listing sub projects at `~/.config/net.splitcells.os.state.interface/project.repositories.`
     Use a special file in the root project instead.
     This would ease migrating the names of such sub projects.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

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
            projectCommandContent = "#!/usr/bin/env sh\n" \
                    + 'cd ' \
                    + str(projectCommand.resolve().parent.parent) \
                    + " \n" \
                    + './bin/' + projectCommand.name + " $@\n"
            self._installProjectCommand(projectCommand.name, projectCommandContent)
        repoGui = self.binFolder.joinpath('repo.gui')
    def _installProjectCommand(self, name, content):
        globalProjectCommandName = self.projectName + '.' + name
        targetCommandFile = self.targetFolder.joinpath(globalProjectCommandName)
        if targetCommandFile.exists():
            executionCounter = 0
            while True:
                globalProjectCommandName = self.projectName + '.' + name + '.' + str(executionCounter)
                targetCommandFile = self.targetFolder.joinpath(globalProjectCommandName)
                executionCounter += 1
                if not targetCommandFile.exists():
                    break
        targetCommandFile.write_text(content)
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
    