#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

this.requires user.bin.configure.0 && command.managed.execute conjunction user.bin.configure
command.repositories.install
command.managed.install.project.commands
  chmod +x ~/bin/net.splitcells.os.state.interface.commands.managed/*
user.bin.configure.uses.dependencies.test && dependencies.test || echo "Dependencies are not validated during this installation."