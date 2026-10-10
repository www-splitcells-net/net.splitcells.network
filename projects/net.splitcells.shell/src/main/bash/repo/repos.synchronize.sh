#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Pulls and pushes to already configured mirrors. Use repos.synchronize.with to do this and to configure it.
# This command assumes, that the repo has no uncommited changes.
# TODO Clone missing sub repositories via "repo.repair".
repos.pull && repos.push
