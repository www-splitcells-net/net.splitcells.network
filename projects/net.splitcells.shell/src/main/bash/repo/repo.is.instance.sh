#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Checks if the current folder is the root folder of an version control repository.

command.managed.execute disjunction repo.is.instance
