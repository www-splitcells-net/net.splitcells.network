#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Checks wether this computer is connected to the Internet from a practical point of view.

# TODO Create command "system.network.to.internet.require" in order to simplify error messaging via generic erro message.
# TODO Rename this to "system.network.to.internet.available" in order make this command more discoverable.

output=$(ping -q -c 1 splitcells.net)
if [ $? == 0 ]; then
	echo true
else
	echo false
fi
