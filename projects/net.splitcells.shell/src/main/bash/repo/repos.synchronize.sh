#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Synchronize repository against all remotes.
# This command assumes, that the repo has no uncommited changes.
# TODO Clone missing sub repositories via "repo.clone.into.current".
repos.pull && repos.push.at.all
