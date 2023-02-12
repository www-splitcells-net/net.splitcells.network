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
if [ -f "~/.ssh" ]
then
	chmod 700 ~/.ssh # Makes the folder only readable by owner, which is usally enough for ssh servers.
fi
if [ -f "~/.ssh/authorized_keys" ]
then
	chmod 644 ~/.ssh/authorized_keys
fi
if [ -f "~/.ssh/id_rsa" ]
then
	echo SSH keys already present. Nothing needs to be done.
else
  test -f ~/.ssh/id_rsa || ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -N '' # Does not override existing keys.
fi
