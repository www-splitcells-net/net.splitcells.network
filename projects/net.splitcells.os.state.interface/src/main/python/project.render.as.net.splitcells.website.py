#!/usr/bin/env python3
"""
This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0, which is available at
http://www.eclipse.org/legal/epl-2.0, or the MIT License,
which is available at https://spdx.org/licenses/MIT.html.

See `project.render.py`.
This command is used in order to render a website based on the format of
`net.splitcells.website.server`.

One could just './bin/render.as.net.splitcells.website.to' itself,
but this command explicitly and testable formalizes this standard.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import argparse
import subprocess

if __name__ == '__main__':
	argsParser = argparse.ArgumentParser(description='Renders a project to a different project.')
	argsParser.add_argument('--to-project'
		, nargs='?'
		, type=str
		, help='This is the path of project, where the results are stored.'
		, required=True)
	parsedArgs = argsParser.parse_args()
	argsDic = vars(parsedArgs)
	subprocess.call('./bin/render.as.net.splitcells.website.to ' + argsDic.get('to_project')
		, shell='True')