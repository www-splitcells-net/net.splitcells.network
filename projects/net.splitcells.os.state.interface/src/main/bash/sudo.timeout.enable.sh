#!/usr/bin/env python3
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

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
