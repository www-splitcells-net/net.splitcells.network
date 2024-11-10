#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import argparse
import logging
import subprocess

from os import environ

if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level = logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--remote-repo'
		, dest = 'remoteRepo'
		, required = True
		, help = 'This is the URL of the remote repository, from which missing repos are cloned.')
	# First the repair is executed by ignoring peer repos,
	# as these often cause problems,
	# when peer or sub repos are deleted.
	commandToExecute = "repos.process"\
		+ " --command 'exit'"\
		+ " --command-for-missing 'command.managed.execute disjunction repos.clone.into.current "\
		+ parser.parse_args().remoteRepo + "/$subRepo'"
	logging.debug("Executing: " + commandToExecute)
	subprocess.call(commandToExecute, shell='True')
	commandToExecute = "repos.process"\
		+ " --command 'exit'"\
		+ " --ignore-peer-repos 'false'"\
		+ " --command-for-missing 'command.managed.execute disjunction repos.clone.into.current "\
		+ parser.parse_args().remoteRepo + "/$subRepo'"
	logging.debug("Executing: " + commandToExecute)
	subprocess.call(commandToExecute, shell='True')