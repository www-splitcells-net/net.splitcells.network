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
import sys
import subprocess
print("Waiting until '" + sys.argv[1] + "' exits with error code 0.")
print("Only the first check is done automatically.")
returnCode = subprocess.call(sys.argv[1], shell='True')
while returnCode != 0:
    input("Press enter for next check.")
    returnCode = subprocess.call(sys.argv[1], shell='True')