#!/usr/bin/env python3
"""
This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0, which is available at
http://www.eclipse.org/legal/epl-2.0, or the MIT License,
which is available at https://spdx.org/licenses/MIT.html.

Deploys a build of this repository to a server,
retrieves execution data and integrates these into the network log.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2022"
__license__ = "EPL-2.0 OR MIT"

import argparse
import subprocess
import logging

if __name__ == '__main__':
	argsParser = argparse.ArgumentParser(
		description='Deploys the build process to a different server.')
	argsParser.add_argument('--target-server', dest='targetServer', type = str, required = True)
	argsParser.add_argument('--user', type = str, required = True)
	parsedArgs = argsParser.parse_args()
	buildScript = """
		git push {0}:/home/{1}/Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network master:net-splitcells-martins-avots-connection
		ssh -t {0} systemd-run --uid={1} --unit=build --working-directory='/home/{1}/Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network' 'sh -c "git merge net-splitcells-martins-avots-connection && ./bin/test.via.network.worker"'
		cd ../net.splitcells.network.log/
		git pull {0}:/home/{1}/Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network.log master
		""".format(parsedArgs.targetServer, parsedArgs.user)
	logging.debug('Executing: ' + buildScript)
	returnCode = subprocess.call(buildScript, shell='True')
	if returnCode != 0:
		exit(1)