#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT
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
