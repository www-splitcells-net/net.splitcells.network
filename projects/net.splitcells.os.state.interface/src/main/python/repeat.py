#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import sys

if __name__ == '__main__':
	if not len(sys.argv) == 4 or not sys.argv[2] == 'times':
		print('error', file=sys.stderr) # TODO Create better error message.
		sys.exit(1)
	print(sys.argv[1] * int(sys.argv[3]))
	sys.exit(0)