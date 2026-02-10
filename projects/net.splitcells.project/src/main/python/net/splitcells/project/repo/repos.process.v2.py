#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["Contributors To The `net.splitcells.*` Projects"]
__copyright__ = "Copyright 2024"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import argparse
import logging
import unittest
import sys

def str2bool(arg):
    # The stringification of the truth boolean is `True` in Python 3 and therefore this capitalization is supported as well.
    return arg == 'true' or arg == 'True'
class RepoProcess:
    executionScript = ""
    def execute(self, args):
        if args.dry_run:
            logging.info("Generated script: \n" + self.executionScript)
        else:
            if args.verbose:
                logging.info("Executing script: \n" + self.executionScript)
            subprocess.call(self.executionScript, shell='True')
def repoProcess(args):
    parser = argparse.ArgumentParser(description="Processes a group of repos.")
    parser.add_argument('--relative-path', dest='relativePath', default='./', help="This is path of the currently processed repo.")
    parser.add_argument('--host', dest='host', required=False)
    parser.add_argument('--command', dest='command', required=False)
    parser.add_argument('--command-for-missing', dest='commandForMissing', required=False)
    parser.add_argument('--command-for-unknown', dest='commandForUnknown', default='exit 1')
    parser.add_argument('--command-for-current', dest='commandForCurrent', required=False) # TODO What is the purpose of this?
    parser.add_argument('--command-for-children', dest='commandForChildren', required=False)
    parser.add_argument('--ignore-peer-repos', dest='ignorePeerRepos', required=False, default='false')
    parser.add_argument('--dry-run', dest='dry_run', required=False, type=str2bool, default=False, help="If true, commands are only prepared and no commands are executed.")
    parser.add_argument('--verbose', dest='verbose', required=False, type=str2bool, default=False, help="If set to true, the output is verbose.")
    RepoProcess().execute(parser.parse_args(args))
class TestRepoProcess(unittest.TestCase):
    def testHelp(self):
        repoProcess(["--dry-run=true"])
if __name__ == '__main__':
    # As there is no build process for Python unit tests are executed every time, to make sure, that the script works correctly.
    # During this test info logging is disabled, which is disabled by default in Python.
    test_result = unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestRepoProcess))
    logging.getLogger().setLevel(logging.INFO)
    if not test_result.wasSuccessful():
        raise Exception("The self test was not successful: " + str(test_result))
    repoProcess(sys.argv[1:])