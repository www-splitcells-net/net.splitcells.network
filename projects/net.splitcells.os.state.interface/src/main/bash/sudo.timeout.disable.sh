#!/usr/bin/env python3
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the Eclipse
# Public License, v. 2.0 are satisfied: GNU General Public License, version 2
# or any later versions with the GNU Classpath Exception which is
# available at https://www.gnu.org/software/classpath/license.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0

# TODO Should such code be deleted? Consider this project's manifesto.
import re
import argparse
pattern = re.compile('^\s*Defaults\s+timestamp_timeout=-1\s*$')
def enableSudoTimeout(sudoersPath = '/etc/sudoers'):
	passwordTimesOut = True
	with open(sudoersPath) as sudoersContent:
		for line in sudoersContent:
			if pattern.match(line):
				passwordTimesOut = False
				break;
	if passwordTimesOut:
		print("Disabling password memory timeout for sudo.")
		with open(sudoersPath, "a") as sudoers:
			sudoers.write("\nDefaults timestamp_timeout=-1\n")
	else:
		print("Password memory timeout for sudo already disabled.")
if __name__ == '__main__':
	'''This command should generally not be used as it creates a security risk.
	This command disables the timeout of the sudo password query for all sessions.
	On Fedora this action does only effect new shell sessions.'''
	parser = argparse.ArgumentParser()
	parser.add_argument('--declare-id', action='store_true') # This defines a command line flag.
	parsedArgs = parser.parse_args()
	if parsedArgs.declare_id:
		print(85101834268009033729726431490423368)
	else:
		enableSudoTimeout()
