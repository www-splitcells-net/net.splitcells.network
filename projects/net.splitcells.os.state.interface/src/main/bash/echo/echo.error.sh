#!/usr/bin/env sh
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
>&2 echo "error" "$@"

if [ -z "$echo_error_notifies_user" ]
then
  # It is desired, that errors are notified to the user by default,
  # as it is too easy to overlook errors in large shell programs.
  # The idea behind the default is, to inform the users that there are errors,
  # in case this error happens in a long running script with a lot of output.
  # This teaches the user, that such errors exist in principal.
  # After that, the user can disable the error, but the user at least knows,
  # that there is such a thing.
  #
  # This is especially helpful, when a user sets up a new computer.
  # If this would be the other way around, it would be more likely,
  # that the user forgets to set the $echo_error_notifies_user variable than the other way around.
  #
  # Of course, such a strategy only works, when error messages are not spammed to the user.
  #
  # TODO IDEA Maybe there is a better tactic for this, in order to inform the user, that there are errors,
  # because this tactic cannot handle error message spamming in a reasonable matter.
  # On the other hand, this tactic might work perfectly fine,
  # if the notification service of the operation system can handle error message spam without
  # overwhelming the user.
  notify.error "$@"
else
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
fi
