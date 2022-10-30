#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

It is preferred,
that an implementation of this command should be side effect free.
Meaning, that only `from-project` and `to-project` should be modified.

If one wants to create a rendering specific to one target project type,
its best to copy this command and use a different command name,
with `project.render.as.` as the prefix.

The implementation of `project.render.as.net.splitcells.website` shows,
that a full implementation is not always needed.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import argparse
import subprocess

if __name__ == '__main__':
	argsParser = argparse.ArgumentParser(description='Renders a project to a different project.')
	argsParser.add_argument('--from-project'
		, nargs = '?'
		, type = str
		, help = 'This is the path of project, that is used as a source.'
		, required = True)
	argsParser.add_argument('--to-project'
		, nargs = '?'
		, type = str
		, help = 'This is the path of project, where the results are stored.'
		, required = True)
	parsedArgs = argsParser.parse_args()
	argsDic = vars(parsedArgs)
	subprocess.call('command.managed.execute conjunction project.render'
		+ ' --from-project=' + argsDic.get('from_project')
		+ ' --to-project=' + argsDic.get('to_project')
		, shell='True')