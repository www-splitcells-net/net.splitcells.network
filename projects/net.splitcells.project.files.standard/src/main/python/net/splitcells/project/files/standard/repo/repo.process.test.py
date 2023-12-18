#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2022"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

import logging
import subprocess
import sys
from threading import Thread

is_running = True
def store_output(stream, output):
    with stream:
        for line in iter(stream.readline, b''):
            if is_running:
                line_str = line.decode(sys.stdout.encoding)
                output.append(line_str)
            else:
                return
if __name__ == '__main__':
    process = subprocess.Popen("repo.process --command='echo $subRepo' --command-for-missing='exit'",
                               shell = True,
                               bufsize = 100000,
                               stdout = subprocess.PIPE,
                               stderr = subprocess.PIPE)
    output = list()
    output_thread = Thread(target=store_output, args=[process.stdout, output])
    error_thread = Thread(target=store_output, args=[process.stderr, output])
    output_thread.start()
    error_thread.start()
    exit_code = process.wait()
    is_running = False
    output_thread.join()
    error_thread.join()
    expected_results = ["\n" # The first entry is empty, because the first repo processed is the current repo.
        ,"../net.splitcells.cin.log/\n"
        ,"../net.splitcells.network.community.via.javadoc/\n"
        ,"../net.splitcells.network.community.git-bug/\n"
        ,"../net.splitcells.network.log/\n"
        ,"../net.splitcells.network.media/\n"
        ,"../net.splitcells.network.repos/\n"
        ,"../net.splitcells.os.state.interface.lib.gpl.2/\n"
        ,"../net.splitcells.os.state.interface.lib.gpl.3/\n"
        ,"../net.splitcells.network.m2/\n"
                        ]
    output_is_valid = True
    for i in range(1, len(expected_results)):
        # `contains` is needed in case the peer repos are not present.
        element_is_valid = expected_results[i] in output[i]
        if not element_is_valid:
            logging.error('Invalid repo: ' + expected_results[i] + ", " + output[i])
        output_is_valid = output_is_valid and element_is_valid
    if not output_is_valid:
        raise Exception('output is not valid: ' + str(output))
    sys.exit(exit_code)