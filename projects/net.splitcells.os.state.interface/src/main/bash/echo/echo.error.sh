#!/usr/bin/env bash
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

# TODO Other commands do not filter echoed errors correctly anymore.

echo.line.current.clear
>&2 echo "error" $@

if [ "$echo_error_notifies_user" == 'true' ]
then
  # Send a notification to the current user, if the environment is set accordingly.
  # Thereby, it is more easily to know, if some optional feature was not executed during a long command.
  # For instance, when a command synchronizes a local repo with all other personal computers,
  # a notification for each not available computer is helpful.
  # In this case it can be normal, that some computers were not available during each synchronization,
  # and therefore the execution as a whole does not have to be aborted or marked as a failure.
  notify.error "$@"
fi