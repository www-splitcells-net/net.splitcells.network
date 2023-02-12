#!/usr/bin/env bash
# Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the Eclipse
# Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
# which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
#
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
