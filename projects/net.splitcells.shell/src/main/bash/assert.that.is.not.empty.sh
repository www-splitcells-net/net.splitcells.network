#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# TODO Remove echo if variable is present. As it creates noise that is unnecessary in most cases.
if [ -n "$1" ];
 then echo "var is set to '$1'";
 else echo "var is unset"; exit 1;
fi