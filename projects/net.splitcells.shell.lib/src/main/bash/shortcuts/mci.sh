#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
if command -v mvnd 2>&1 >/dev/null
then
    mvnd clean install $@
    exit
fi

mvn clean install $@
