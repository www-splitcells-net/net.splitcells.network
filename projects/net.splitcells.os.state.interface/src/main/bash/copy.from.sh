#!/bin/sh
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT
if [ "$2" != "to" ]; then
  echo "The second argument is invalid: $2"
  echo 'It should be "to"'
  exit 1
fi
# TODO Check if 3 arguments are present.
cp $1 $3