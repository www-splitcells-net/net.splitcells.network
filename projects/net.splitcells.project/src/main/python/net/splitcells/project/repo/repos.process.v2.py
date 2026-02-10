#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

import argparse
import logging
import unittest
import sys

def repoProcess(args):
    parser = argparse.ArgumentParser(formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument('--relative-path', dest='relativePath', default='./', help="This is path of the currently processed repo.")
    parser.add_argument('--host', dest='host', required=False)
    parser.add_argument('--command', dest='command', required=True)
    parser.add_argument('--command-for-missing', dest='commandForMissing', required=False)
    parser.add_argument('--command-for-unknown', dest='commandForUnknown', default='exit 1')
    parser.add_argument('--command-for-current', dest='commandForCurrent', required=False) # TODO What is the purpose of this?
    parser.add_argument('--command-for-children', dest='commandForChildren', required=False)
    parser.add_argument('--ignore-peer-repos', dest='ignorePeerRepos', required=False, default='false')
    parsedArgs = parser.parse_args()
class TestRepoProcess(unittest.TestCase):
    def test(self):
        print("Test")
if __name__ == '__main__':
    # As there is no build process for Python unit tests are executed every time, to make sure, that the script works correctly.
    # During this test info logging is disabled, which is disabled by default in Python.
    test_result = unittest.TextTestRunner().run(unittest.TestLoader().loadTestsFromTestCase(TestRepoProcess))
    logging.getLogger().setLevel(logging.INFO)
    if not test_result.wasSuccessful():
        raise Exception("The self test was not successful: " + str(test_result))
    repoProcess(sys.argv[1:])