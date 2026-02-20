#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

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
TODO Make it possible to use relative target path as arg and inside the generated scripts.
     This way portable scripts for other users and computers could be generated.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["Contributors To The `net.splitcells.*` Projects"]
__copyright__ = "Copyright 2024"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import argparse
import datetime
import logging
import unittest
from string import Template
import subprocess
import sys
import textwrap
from tempfile import TemporaryDirectory
from os import (environ, makedirs)
from pathlib import Path

def str2bool(arg):
    # The stringification of the truth boolean is `True` in Python 3 and therefore this capitalization is supported as well.
    return arg == 'true' or arg == 'True'
class ReposProcess:
    executionScript = ""
    childRepo = ''
    peerRepo = ''
    targetPath = None
    dryRun = False
    isRoot = True
    ignorePeerRepos = False
    processInParallel = False
    def execute(self, args):
        # TODO Currently, given target paths are converted to be absolute, as peer repos are otherwise not processed correctly.
        self.targetPath = Path(args.path).absolute()
        self.command = args.command
        self.dryRun = args.dryRun
        self.verbose = args.verbose
        self.commandForMissing = args.commandForMissing
        self.commandForUnknown = args.commandForUnknown
        self.ignorePeerRepos = args.ignorePeerRepos
        self.processInParallel = args.processInParallel
        if self.processInParallel and 'codeberg' in args.command + self.commandForMissing + self.commandForUnknown:
            logging.error('Disabling --process-in-parallel, as Codeberg does not support parallel SSH connections. If this is attempted, the Codeberg server starts rejecting SSH connections.')
            self.processInParallel = False
        self.executeRepo()
    def childRepoProcess(self):
        childProcess = ReposProcess()
        childProcess.isRoot = False
        childProcess.targetPath = self.targetPath
        childProcess.command = self.command
        childProcess.commandForMissing = self.commandForMissing
        childProcess.commandForUnknown = self.commandForUnknown
        childProcess.dryRun = self.dryRun
        childProcess.verbose = self.verbose
        childProcess.childRepo = self.childRepo
        childProcess.peerRepo = self.peerRepo
        childProcess.ignorePeerRepos = self.ignorePeerRepos
        childProcess.processInParallel = self.processInParallel
        return childProcess
    def executeRepo(self):
        if self.isRoot:
            self.executionScript += "set -e\n\n"
        if self.targetPath.is_dir():
            if (str(self.targetPath) != './'):
                self.executionScript += 'cd \"' + str(self.targetPath) + '\"\n'
            self.executionScript += self.getCommandForScript(self.command) + '\n'
        else:
            self.executionScript += '# Processing missing "' + str(self.targetPath) + '"\n'
            self.executionScript += 'cd \"' + str(self.targetPath.parent) + '\"\n'
            self.executionScript += self.getCommandForScript(self.commandForMissing) + '\n'
        childrenFile = self.targetPath.joinpath('./bin/net.splitcells.repos.children')
        if childrenFile.is_file():
            childQuery = subprocess.run([childrenFile], stdout=subprocess.PIPE)
            if childQuery.returncode != 0:
                exit(childQuery.returncode)
            children = childQuery.stdout.decode('utf-8').split("\n")
            for child in children:
                if not child == "" and not child.isspace():
                    childFile = self.targetPath.joinpath(child)
                    childProcess = self.childRepoProcess()
                    childProcess.childRepo = child
                    childProcess.targetPath = childFile
                    childProcess.command = childProcess.applyTemplate(self.command)
                    childProcess.executeRepo()
                    self.executionScript += childProcess.executionScript
            for targetSubDir in self.targetPath.iterdir():
                if targetSubDir.is_dir() and not targetSubDir.name.startswith('.') and targetSubDir.name != 'bin':
                    if not targetSubDir.name in children:
                        self.executionScript += '# Processing unknown repo "' + str(targetSubDir) + '"\n'
                        self.executionScript += self.getCommandForScript(self.commandForUnknown) + '\n'
        self.processPeerRepos()
        if self.processInParallel and self.isRoot:
            self.executionScript += 'wait\n'
        if self.dryRun:
            if self.isRoot:
                logging.info("Generated script: \n" + self.executionScript)
        elif self.isRoot:
            if self.verbose:
                logging.info("Executing script: \n" + self.executionScript)
            logging.debug("Executing script: \n" + self.executionScript)
            executionSuccess = subprocess.run(self.executionScript, shell='True')
            if executionSuccess.returncode != 0:
                exit(executionSuccess.returncode)
    def getCommandForScript(self, command):
        scriptLine = self.applyTemplate(command)
        if self.processInParallel:
            return scriptLine + " &\n"
        return scriptLine + '\n'
    def processPeerRepos(self):
        peerFile = self.targetPath.joinpath('./bin/net.splitcells.shell.repos.peers')
        if self.ignorePeerRepos:
            if peerFile.is_file():
                logging.info("Ignoring peer repos.")
            return
        if peerFile.is_file():
            peerQuery = subprocess.run([peerFile], stdout=subprocess.PIPE)
            if peerQuery.returncode != 0:
                exit(peerQuery.returncode)
            for peer in peerQuery.stdout.decode('utf-8').split("\n"):
                if not peer == "" and not peer.isspace():
                    peerFile = self.targetPath.joinpath("../" + peer).resolve()
                    peerProcess = self.childRepoProcess()
                    peerProcess.peerRepo = peer
                    peerProcess.targetPath = peerFile
                    peerProcess.command = peerProcess.applyTemplate(self.command)
                    peerProcess.executeRepo()
                    self.executionScript += peerProcess.executionScript
    def applyTemplate(self, string):
        return Template(string).safe_substitute(
             subRepo = self.childRepo
            ,childRepo = self.childRepo
            ,peerRepo = self.peerRepo
            ,currentRepo = self.targetPath.name)
def reposProcess(args):
    parser = argparse.ArgumentParser(description="Processes a group of repos.")
    parser.add_argument('--path', dest='path', default='./', help="This is path of the to be processed meta repo.")
    parser.add_argument('--host', dest='host', required=False)
    parser.add_argument('--command', dest='command', required=True, help="This command is executed for all present repositories.")
    parser.add_argument('--command-for-missing', dest='commandForMissing', default='exit 1')
    parser.add_argument('--command-for-unknown', dest='commandForUnknown', default='exit 1')
    parser.add_argument('--ignore-peer-repos', dest='ignorePeerRepos', type=str2bool, required=False, default='false')
    parser.add_argument('--dry-run', dest='dryRun', required=False, type=str2bool, default=False, help="If true, commands are only prepared and no commands are executed.")
    parser.add_argument('--verbose', dest='verbose', required=False, type=str2bool, default=False, help="If set to true, the output is verbose.")
    parser.add_argument('--process-in-parallel', dest='processInParallel', type=str2bool, required=False, default='false')
    process = ReposProcess()
    process.execute(parser.parse_args(args))
    return process
class TestReposProcess(unittest.TestCase):
    maxDiff = None
    def testPath(self):
        with TemporaryDirectory() as tmpDirStr:
            tmpDir = Path(tmpDirStr)
            testResult = reposProcess(["--dry-run=true", "--path=" + tmpDirStr, '--command=echo'])
            self.assertEqual(testResult.executionScript, """set -e

cd "${tmpDirStr}"
echo

""".replace("${tmpDirStr}", tmpDirStr))
    def testRepo(self):
        with TemporaryDirectory() as tmpDirStr:
            tmpDir = Path(tmpDirStr)
            makedirs(tmpDir.joinpath('test-repo/bin'))
            makedirs(tmpDir.joinpath('test-repo/sub-1/bin'))
            makedirs(tmpDir.joinpath('test-repo/sub-2/bin'))
            makedirs(tmpDir.joinpath('test-repo/sub-2/sub-3'))
            makedirs(tmpDir.joinpath('test-repo/none-sub-peer'))
            makedirs(tmpDir.joinpath('peer-repo'))
            with open(tmpDir.joinpath('test-repo/bin/net.splitcells.repos.children'), 'w') as testRepo:
                testRepo.write("""#!/usr/bin/env sh
                    echo sub-1
                    echo sub-2
                    echo missing-sub
                    """)
            subprocess.call("chmod +x " + str(tmpDir.joinpath('test-repo/bin/net.splitcells.repos.children')), shell='True')
            with open(tmpDir.joinpath('test-repo/bin/net.splitcells.shell.repos.peers'), 'w') as peerRepo:
                peerRepo.write("""#!/usr/bin/env sh
                    echo peer-repo
                    echo missing-peer
                    """)
            subprocess.call("chmod +x " + str(tmpDir.joinpath('test-repo/bin/net.splitcells.shell.repos.peers')), shell='True')
            with open(tmpDir.joinpath('test-repo/sub-1/bin/net.splitcells.shell.repos.peers'), 'w') as peerRepo:
                peerRepo.write("""#!/usr/bin/env sh
                    echo none-sub-peer
                    """)
            subprocess.call("chmod +x " + str(tmpDir.joinpath('test-repo/sub-1/bin/net.splitcells.shell.repos.peers')), shell='True')
            testResult = reposProcess(["--dry-run=true"
                , "--path=" + str(tmpDir.joinpath('test-repo'))
                , '--command=echo child:${childRepo} & ${subRepo},peer:${peerRepo}'])
            self.assertEqual(testResult.executionScript, textwrap.dedent("""\
                set -e
                
                cd "${tmpDirStr}/test-repo"
                echo child: & ,peer:
                
                cd "${tmpDirStr}/test-repo/sub-1"
                echo child:sub-1 & sub-1,peer:
                
                cd "${tmpDirStr}/test-repo/none-sub-peer"
                echo child:sub-1 & sub-1,peer:
                
                cd "${tmpDirStr}/test-repo/sub-2"
                echo child:sub-2 & sub-2,peer:
                
                # Processing missing "${tmpDirStr}/test-repo/missing-sub"
                cd "${tmpDirStr}/test-repo"
                exit 1
                
                # Processing unknown repo "${tmpDirStr}/test-repo/none-sub-peer"
                exit 1
                
                cd "${tmpDirStr}/peer-repo"
                echo child: & ,peer:peer-repo
                
                # Processing missing "${tmpDirStr}/missing-peer"
                cd "${tmpDirStr}"
                exit 1
                
                """).replace("${tmpDirStr}", tmpDirStr))
            testResultForParallelism = reposProcess(["--dry-run=true"
                , "--path=" + str(tmpDir.joinpath('test-repo'))
                , '--command=echo child:${childRepo} & ${subRepo},peer:${peerRepo}'
                , '--process-in-parallel=true'])
            self.assertEqual(testResultForParallelism.executionScript, textwrap.dedent("""\
                set -e
                
                cd "${tmpDirStr}/test-repo"
                echo child: & ,peer: &
                
                cd "${tmpDirStr}/test-repo/sub-1"
                echo child:sub-1 & sub-1,peer: &
                
                cd "${tmpDirStr}/test-repo/none-sub-peer"
                echo child:sub-1 & sub-1,peer: &
                
                cd "${tmpDirStr}/test-repo/sub-2"
                echo child:sub-2 & sub-2,peer: &
                
                # Processing missing "${tmpDirStr}/test-repo/missing-sub"
                cd "${tmpDirStr}/test-repo"
                exit 1 &
                
                # Processing unknown repo "${tmpDirStr}/test-repo/none-sub-peer"
                exit 1 &
                
                cd "${tmpDirStr}/peer-repo"
                echo child: & ,peer:peer-repo &
                
                # Processing missing "${tmpDirStr}/missing-peer"
                cd "${tmpDirStr}"
                exit 1 &
                
                wait
                """).replace("${tmpDirStr}", tmpDirStr))
            testResultWithoutPeers = reposProcess(["--dry-run=true"
                , "--path=" + str(tmpDir.joinpath('test-repo'))
                , '--command=echo child:${childRepo} & ${subRepo},peer:${peerRepo}'
                , '--ignore-peer-repos=true'])
            self.assertEqual(testResultWithoutPeers.executionScript, textwrap.dedent("""\
                set -e
                
                cd "${tmpDirStr}/test-repo"
                echo child: & ,peer:
                
                cd "${tmpDirStr}/test-repo/sub-1"
                echo child:sub-1 & sub-1,peer:
                
                cd "${tmpDirStr}/test-repo/sub-2"
                echo child:sub-2 & sub-2,peer:
                
                # Processing missing "${tmpDirStr}/test-repo/missing-sub"
                cd "${tmpDirStr}/test-repo"
                exit 1
                
                # Processing unknown repo "${tmpDirStr}/test-repo/none-sub-peer"
                exit 1
                
                """).replace("${tmpDirStr}", tmpDirStr))
if __name__ == '__main__':
    if datetime.datetime.strptime("2026.03.12", "%Y.%m.%d") < datetime.datetime.now():
        print("repo.process.v1 is deprecated and the date of removal is reached. Please, delete repo.process.v1.py from the source code and remove v1 integration from v2.")
        exit(1)
    if environ.get('repo_process_v1') == '1':
        print('Using ' + str(['repos.process.v1'] + sys.argv[1:]))
        exit(subprocess.call(['repos.process.v1'] + sys.argv[1:]))
    # TODO Remove this, when the old repo process is deleted.
    if environ.get('repo_process_v2_parallel') == '1':
        sys.argv = sys.argv + ['--process-in-parallel=true']
    else:
        sys.argv = sys.argv
    # As there is no build process for Python unit tests are executed every time, to make sure, that the script works correctly.
    # During this test info logging is disabled, which is disabled by default in Python.
    test_result = unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestReposProcess))
    logging.getLogger().setLevel(logging.INFO)
    if not test_result.wasSuccessful():
        raise Exception("The self test was not successful: " + str(test_result))
    reposProcess(sys.argv[1:])
