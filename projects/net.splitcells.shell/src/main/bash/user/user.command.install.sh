#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
#
# Minimize the number of dependencies as the context of this program is not known and is used for bootstrapping.
cp $1 "$(command.managed.bin)/"
chmod -R +x "$(command.managed.bin)/"*
echo Command $1 installed.
