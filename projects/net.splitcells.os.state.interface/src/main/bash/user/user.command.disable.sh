#!/usr/bin/env sh
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

mkdir -p ~/bin/net.splitcells.os.state.interface.commands.disabled
echo "#!/usr/bin/env sh" >> ~/bin/net.splitcells.os.state.interface.commands.disabled/$1
	echo "echo $1 is disabled." >> ~/bin/net.splitcells.os.state.interface.commands.disabled/$1
	echo "exit 1" >> ~/bin/net.splitcells.os.state.interface.commands.disabled/$1
chmod +x ~/bin/net.splitcells.os.state.interface.commands.disabled/$1
