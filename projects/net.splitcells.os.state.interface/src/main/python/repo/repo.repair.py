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
import sys

from os import environ

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level = logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--remote-repo'
		, dest = 'remoteRepo'
		, required = True
		, help = 'This is the URL of the remote repository, from which missing repos are cloned.')
	commandToExecute = "repo.process"\
		+ " --command 'exit'"\
		+ " --command-for-missing 'command.managed.execute disjunction repo.clone.into.current"\
		+ parser.parse_args().remoteRepo + "/$subRepo'"
	logging.debug("Executing: " + commandToExecute)
	subprocess.call(commandToExecute, shell='True')