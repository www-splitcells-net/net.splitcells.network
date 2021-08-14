#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Creates a mirror of a source repository.
# The difference between a repository clone and a mirror is, that a mirror is not intended to work independently.
# A mirror just copies an other repository.
# The source repositories URL is stored in the mirror and may be changed to different URL.
# The source repository may also be changed completely.
# The main purpose of a mirror is to save storage compared to clone.
# Note that it is not guaranteed that storage is used more efficient by an mirror than a clone.
# This depends on the implementation.

repo.process --command 'command.managed.execute disjunction repo.mirror '$@'/$subRepo'
