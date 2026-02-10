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
    subRepo = ''
    peerRepo = ''
    targetPath = None
    dryRun = False
    def execute(self, args):
        self.targetPath = Path(args.path)
        self.commandForCurrent = args.commandForCurrent
        self.dryRun = args.dryRun
        self.verbose = args.verbose
        self.executeRepo()
    def copy(self):
        copy = ReposProcess()
        copy.targetPath = self.targetPath
        copy.commandForCurrent = self.commandForCurrent
        copy.dryRun = self.dryRun
        copy.verbose = self.verbose
        return copy
    def executeRepo(self):
        if (str(self.targetPath) != './'):
            self.executionScript += 'cd \"' + str(self.targetPath) + '\"\n'
        self.executionScript += self.applyTemplate(self.commandForCurrent) + '\n\n'
        childrenFile = self.targetPath.joinpath('./bin/net.splitcells.repos.children')
        if childrenFile.is_file():
            childQuery = subprocess.run([childrenFile], stdout=subprocess.PIPE)
            for child in childQuery.stdout.decode('utf-8').split("\n"):
                if not child == "" and not child.isspace():
                    self.subRepo = child
                    if self.targetPath.joinpath(child).is_dir():
                        self.executionScript += self.applyTemplate("cd ./${subRepo}\n" + self.commandForCurrent + "\n")
                    self.executionScript += "\n"
                    self.subRepo = ""
        peerFile = self.targetPath.joinpath('./bin/net.splitcells.shell.repos.peers')
        if peerFile.is_file():
            peerQuery = subprocess.run([peerFile], stdout=subprocess.PIPE)
            for peer in peerQuery.stdout.decode('utf-8').split("\n"):
                if not peer == "" and not peer.isspace():
                    self.peerRepo = peer
                    peerFile = self.targetPath.joinpath("../" + peer).resolve()
                    if peerFile.is_dir():
                        self.executionScript += self.applyTemplate("cd " + str(peerFile) + "\n" + self.commandForCurrent + "\n")
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
             subRepo = self.subRepo
            ,peerRepo = self.peerRepo)
def reposProcess(args):
    parser = argparse.ArgumentParser(description="Processes a group of repos.")
    parser.add_argument('--path', dest='path', default='./', help="This is path of the to be processed meta repo.")
    parser.add_argument('--host', dest='host', required=False)
    parser.add_argument('--command-for-missing', dest='commandForMissing', required=False)
    parser.add_argument('--command-for-unknown', dest='commandForUnknown', default='exit 1')
    parser.add_argument('--command-for-current', dest='commandForCurrent', required=True)
    parser.add_argument('--ignore-peer-repos', dest='ignorePeerRepos', required=False, default='false')
    parser.add_argument('--dry-run', dest='dryRun', required=False, type=str2bool, default=False, help="If true, commands are only prepared and no commands are executed.")
    parser.add_argument('--verbose', dest='verbose', required=False, type=str2bool, default=False, help="If set to true, the output is verbose.")
    process = ReposProcess()
    process.execute(parser.parse_args(args))
    return process
class TestReposProcess(unittest.TestCase):
    def testPath(self):
        with TemporaryDirectory() as tmpDirStr:
            tmpDir = Path(tmpDirStr)
            testResult = reposProcess(["--dry-run=true", "--path=" + tmpDirStr, '--command-for-current=echo'])
            self.assertEqual(testResult.executionScript, """cd "${tmpDirStr}"
echo

""".replace("${tmpDirStr}", tmpDirStr))
    def testRepo(self):
        with TemporaryDirectory() as tmpDirStr:
            tmpDir = Path(tmpDirStr)
            makedirs(tmpDir.joinpath('test-repo/bin'))
            makedirs(tmpDir.joinpath('test-repo/sub-1'))
            makedirs(tmpDir.joinpath('test-repo/sub-2/bin'))
            makedirs(tmpDir.joinpath('test-repo/sub-2/sub-3'))
            makedirs(tmpDir.joinpath('peer-repo'))
            with open(tmpDir.joinpath('test-repo/bin/net.splitcells.repos.children'), 'w') as testRepo:
                testRepo.write("""#!/usr/bin/env sh
                    echo sub-1
                    echo sub-2
                    """)
            with open(tmpDir.joinpath('test-repo/bin/net.splitcells.shell.repos.peers'), 'w') as peerRepo:
                            peerRepo.write("""#!/usr/bin/env sh
                                echo peer-repo
                                """)
            subprocess.call("chmod +x " + str(tmpDir.joinpath('test-repo/bin/net.splitcells.repos.children')), shell='True')
            subprocess.call("chmod +x " + str(tmpDir.joinpath('test-repo/bin/net.splitcells.shell.repos.peers')), shell='True')
            testResult = reposProcess(["--dry-run=true", "--path=" + str(tmpDir.joinpath('test-repo')), '--command-for-current=echo child:${subRepo},peer:${peerRepo}'])
            self.assertEqual(testResult.executionScript, """cd "${tmpDirStr}/test-repo"
echo child:,peer:

cd ./sub-1
echo child:sub-1,peer:

cd ./sub-2
echo child:sub-2,peer:

cd ${tmpDirStr}/peer-repo
echo child:,peer:peer-repo

""".replace("${tmpDirStr}", tmpDirStr))
if __name__ == '__main__':
    # As there is no build process for Python unit tests are executed every time, to make sure, that the script works correctly.
    # During this test info logging is disabled, which is disabled by default in Python.
    test_result = unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestReposProcess))
    logging.getLogger().setLevel(logging.INFO)
    if not test_result.wasSuccessful():
        raise Exception("The self test was not successful: " + str(test_result))
    reposProcess(sys.argv[1:])