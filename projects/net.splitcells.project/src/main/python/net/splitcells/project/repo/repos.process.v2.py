#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

import argparse

if __name__ == '__main__':
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