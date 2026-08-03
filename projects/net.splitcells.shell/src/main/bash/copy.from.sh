#!/bin/sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
if [ "$2" != "to" ]; then
  echo "The second argument is invalid: $2"
  echo 'It should be "to"'
  exit 1
fi
# TODO Check if 3 arguments are present.
cp $1 $3