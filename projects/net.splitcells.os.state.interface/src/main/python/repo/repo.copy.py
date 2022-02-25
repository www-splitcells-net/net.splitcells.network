#!/usr/bin/env python3
"""
This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0, which is available at
http://www.eclipse.org/legal/epl-2.0, or the MIT License,
which is available at https://spdx.org/licenses/MIT.html.

TODO TODOC Is this better than "repo.exists || repo.clone.into.current '<URL of remote repo>'"
and if so, why?
Also, add a general version of the answer to the general development guidelines.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import argparse
import logging
from os import environ
import subprocess
if __name__ == '__main__':
    if environ.get('log_level') == 'debug':
        logging.basicConfig(level=logging.DEBUG)
    parser = argparse.ArgumentParser()
    parser.add_argument('--remote-repo', dest='remoteRepo', required=True)
    parser.add_argument('--omit-if-exists', dest='omitIfExists', type=bool, default=False)
    parser.add_argument('--target-folder', dest='targetFolder', required=True)
    parsedArgs = parser.parse_args()
    # TODO Do not create shell script, but call directly.
    # TODO Log errors to error stream.
    if parsedArgs.omitIfExists  and parsedArgs.targetFolder == '.' and subprocess.call('repo.is.instance', shell='True') == 0:
        logging.debug('The repo already exists, so nothing needs to be done.')
        exit(0)
    returnCode = None
    if parsedArgs.targetFolder == '.':
        synchronizationScript = 'repo.clone.into.current ' + parsedArgs.remoteRepo
        logging.debug('Executing: ' + synchronizationScript)
        returnCode = subprocess.call(synchronizationScript, shell='True')
    else:
        logging.error('TODO The option "target-folder" currently only supports the value ".", which copies the repo into the current folder.')
        exit(1)
    if returnCode != 0:
        exit(1)