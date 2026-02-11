#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

TODO Support subRepo optionally and rename it to childRepo.

"""

__author__ = "Mārtiņš Avots"
__authors__ = ["Contributors To The `net.splitcells.*` Projects"]
__copyright__ = "Copyright 2024"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import argparse
import logging
import unittest
from string import Template
import subprocess
import sys
from tempfile import TemporaryDirectory
from os import makedirs
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
    def execute(self, args):
        self.targetPath = Path(args.path)
        self.command = args.command
        self.dryRun = args.dryRun
        self.verbose = args.verbose
        self.commandForMissing = args.commandForMissing
        self.commandForUnknown = args.commandForUnknown
        self.ignorePeerRepos = args.ignorePeerRepos
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
        return childProcess
    def executeRepo(self):
        if self.isRoot:
            self.executionScript += "set -e\n\n"
        if self.targetPath.is_dir():
            if (str(self.targetPath) != './'):
                self.executionScript += 'cd \"' + str(self.targetPath) + '\"\n'
            self.executionScript += self.applyTemplate(self.command) + '\n\n'
        else:
            self.executionScript += '# Processing missing "' + str(self.targetPath) + '"\n'
            self.executionScript += 'cd \"' + str(self.targetPath.parent) + '\"\n'
            self.executionScript += self.applyTemplate(self.commandForMissing) + '\n\n'
        childrenFile = self.targetPath.joinpath('./bin/net.splitcells.repos.children')
        if childrenFile.is_file():
            childQuery = subprocess.run([childrenFile], stdout=subprocess.PIPE)
            children = childQuery.stdout.decode('utf-8').split("\n")
            for child in children:
                if not child == "" and not child.isspace():
                    self.childRepo = child
                    childFile = self.targetPath.joinpath(child)
                    childProcess = self.childRepoProcess()
                    childProcess.childRepo = ''
                    childProcess.targetPath = childFile
                    childProcess.command = self.applyTemplate(self.command)
                    childProcess.executeRepo()
                    self.executionScript += childProcess.executionScript
                    if not self.executionScript.endswith("\n\n"):
                        self.executionScript += "\n"
                    self.childRepo = ""
            for targetSubDir in self.targetPath.iterdir():
                if targetSubDir.is_dir() and not targetSubDir.name.startswith('.') and targetSubDir.name != 'bin':
                    if not targetSubDir.name in children:
                        self.executionScript += '# Processing unknown repo "' + str(targetSubDir) + '"\n'
                        self.executionScript += self.commandForUnknown + '\n\n'
        self.processPeerRepos()
    def processPeerRepos(self):
        peerFile = self.targetPath.joinpath('./bin/net.splitcells.shell.repos.peers')
        if self.ignorePeerRepos:
            if peerFile.is_file():
                logging.info("Ignoring peer repos.")
            return
        if peerFile.is_file():
            peerQuery = subprocess.run([peerFile], stdout=subprocess.PIPE)
            for peer in peerQuery.stdout.decode('utf-8').split("\n"):
                if not peer == "" and not peer.isspace():
                    self.peerRepo = peer
                    peerFile = self.targetPath.joinpath("../" + peer).resolve()
                    peerProcess = self.childRepoProcess()
                    peerProcess.peerRepo = ''
                    peerProcess.targetPath = peerFile
                    peerProcess.command = self.applyTemplate(self.command)
                    peerProcess.executeRepo()
                    self.executionScript += peerProcess.executionScript
                    if not self.executionScript.endswith("\n\n"):
                        self.executionScript += "\n"
                    self.peerRepo = ""
        if self.dryRun:
            logging.info("Generated script: \n" + self.executionScript)
        else:
            if self.verbose:
                logging.info("Executing script: \n" + self.executionScript)
            subprocess.call(self.executionScript, shell='True')
    def applyTemplate(self, string):
        return Template(string).safe_substitute(
             subRepo = self.childRepo
            ,childRepo = self.childRepo
            ,peerRepo = self.peerRepo)
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
            self.assertEqual(testResult.executionScript, """set -e

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

""".replace("${tmpDirStr}", tmpDirStr))
            testResultWithoutPeers = reposProcess(["--dry-run=true"
                , "--path=" + str(tmpDir.joinpath('test-repo'))
                , '--command=echo child:${childRepo} & ${subRepo},peer:${peerRepo}'
                , '--ignore-peer-repos=true'])
            self.assertEqual(testResultWithoutPeers.executionScript, """set -e

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

""".replace("${tmpDirStr}", tmpDirStr))
if __name__ == '__main__':
    # As there is no build process for Python unit tests are executed every time, to make sure, that the script works correctly.
    # During this test info logging is disabled, which is disabled by default in Python.
    test_result = unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestReposProcess))
    logging.getLogger().setLevel(logging.INFO)
    if not test_result.wasSuccessful():
        raise Exception("The self test was not successful: " + str(test_result))
    reposProcess(sys.argv[1:])
