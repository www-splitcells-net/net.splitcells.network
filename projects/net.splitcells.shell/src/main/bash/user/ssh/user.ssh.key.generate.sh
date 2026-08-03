#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

set -e
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
