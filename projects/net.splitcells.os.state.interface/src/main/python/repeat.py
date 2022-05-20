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

import sys

if __name__ == '__main__':
	if not len(sys.argv) == 4 or not sys.argv[2] == 'times':
		print('error', file=sys.stderr) # TODO Create better error message.
		sys.exit(1)
	print(sys.argv[1] * int(sys.argv[3]))
	sys.exit(0)