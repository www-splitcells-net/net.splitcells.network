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
import logging
import subprocess

from os import environ

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level = logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--remote-repo-name'
						, dest = 'remoteRepoName'
						, required = True
						, help = 'This is the local name of the remote repository,to which data is pushed.')
	parser.add_argument('--remote-repo-URL'
						, dest = 'remoteRepoUrl'
						, required = True
						, help = 'This is the URL of the remote repository,to which data is pushed.')
	parsedArgs = parser.parse_args()
	commandToExecute = "repo.process --command 'command.managed.execute disjunction repo.push.at" \
					   + " --remote-repo-name=''" + parsedArgs.remoteRepoName + "''" \
					   + " --remote-repo-URL=''" + parsedArgs.remoteRepoUrl + "'"
	logging.debug("Executing: " + commandToExecute)
	subprocess.call(commandToExecute, shell='True')