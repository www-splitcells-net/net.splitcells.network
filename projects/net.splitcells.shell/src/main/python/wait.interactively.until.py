#!/usr/bin/env python3
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
import sys
import subprocess
print("Waiting until '" + sys.argv[1] + "' exits with error code 0.")
print("Only the first check is done automatically.")
returnCode = subprocess.call(sys.argv[1], shell='True')
while returnCode != 0:
    input("Press enter for next check.")
    returnCode = subprocess.call(sys.argv[1], shell='True')