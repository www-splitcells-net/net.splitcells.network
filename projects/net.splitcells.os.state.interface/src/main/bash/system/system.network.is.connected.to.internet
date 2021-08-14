#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Checks wether this computer is connected to the Internet from a practical point of view.

# TODO Create command "system.network.to.internet.require" in order to simplify error messaging via generic erro message.
# TODO Rename this to "system.network.to.internet.available" in order make this command more discoverable.

output=$(ping -q -c 1 splitcells.net)
if [ $? == 0 ]; then
	echo true
else
	echo false
fi
