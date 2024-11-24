#!/usr/bin/env sh
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
set -e
this.requires user.bin.configure.0 && command.managed.execute conjunction user.bin.configure
command.repositories.install
command.managed.install.project.commands
  chmod +x ~/bin/net.splitcells.os.state.interface.commands.managed/*
if user.bin.configure.uses.test.dependencies
then
  test.dependencies
  echo Dependency tests executed.
else
  echo "Dependencies are not validated during this installation."
fi
