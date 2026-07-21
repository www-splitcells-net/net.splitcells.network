#!/usr/bin/env python3
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# TODO Should such code be deleted? Consider this project's manifesto.
import re
import argparse
pattern = re.compile('\s*Defaults\s+timestamp_timeout=-1\s*\n')
def enableSudoTimeout(sudoersPath = '/etc/sudoers'):
	passwordTimesOut = True
	sudoersUpdate=None
	with open(sudoersPath, 'r') as sudoersFile:
		sudoersUpdate=pattern.sub('', sudoersFile.read())
	with open(sudoersPath, "wt") as sudoersFile:
		sudoersFile.write(sudoersUpdate)
if __name__ == '__main__':
	'''This command should generally not be used as it creates a security risk.
	This command enables the timeout of the sudo password query for all sessions.
	On Fedora this action does only effect new shell sessions.'''
	parser = argparse.ArgumentParser()
	parser.add_argument('--declare-id', action='store_true') # This defines a command line flag.
	parsedArgs = parser.parse_args()
	if parsedArgs.declare_id:
		print(85101834268009033729726431490423368)
	else:
		enableSudoTimeout()
