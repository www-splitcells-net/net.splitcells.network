#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT
#
# Minimize the number of dependencies as the context of this program is not known and is used for bootstrapping.
# TODO Remove this command in order to minimize source code size.
cd $1
mkdir -p ~/bin
	cp ./* ~/bin/
	chmod -R +x ~/bin/*
echo Commands at $1 installed.
