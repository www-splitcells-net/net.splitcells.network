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

import argparse
import subprocess

if __name__ == '__main__':
	argsParser = argparse.ArgumentParser(description='Renders a project to a different project. This can be used to build a website.')
	argsParser.add_argument('--from-project'
		, nargs='?'
		, type=str
		, help='This is the path of project, that is used as a source.'
		, required=True)
	argsParser.add_argument('--to-project'
		, nargs='?'
		, type=str
		, help='This is the path of project, where the results are stored.'
		, required=True)
	parsedArgs = argsParser.parse_args()
	argsDic = vars(parsedArgs)
	subprocess.call('command.managed.execute conjunction website.server.render'
		+ ' --from-project=' + argsDic.get('from-project')
		+ ' --to-project=' + argsDic.get('to-project')
		, shell='True')