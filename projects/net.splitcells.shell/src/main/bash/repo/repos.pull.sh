#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Pull information from all remotes.
repos.process --command 'command.managed.execute disjunction repo.pull' $@

exit # TODO
repos.process \
	--command 'exit' \
	--command-for-missing 'command.managed.execute disjunction repo.clone.into.current '$@'/$subRepo' \
	--command-for-current 'exit' \
	--command-for-children 'command.managed.execute disjunction repo.clone.into.current '$@'/$subRepo'