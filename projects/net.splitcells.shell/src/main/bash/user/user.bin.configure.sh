#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

set -e
this.requires user.bin.configure.0 && command.managed.execute conjunction user.bin.configure
command.repositories.install
command.managed.install.project.commands
  chmod +x "$(command.managed.bin)/"*
if user.bin.configure.uses.test.dependencies
then
  test.dependencies
  echo Dependency tests executed.
else
  echo "Dependencies are not validated during this installation."
fi
