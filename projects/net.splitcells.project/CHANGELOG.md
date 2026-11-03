----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
* The changelog format can be found [here](./src/main/md/net/splitcells/network/guidelines/changelog.md).
## [Unreleased]
### Major Changes
* **2024-02-12 \#213** Disallow using `./.net.splitcells.os.state.interface.repo/subs.json`
  for storing the child repo names.
  Use `./bin/net.splitcells.osi.repos.children` instead.
  Both files have the same format.
  This simplifies the file structure.
### Minor Changes
* **2026-10-31 \#32** Add `repo.remote.set2` and `repos.remote.set2`,
  in order to set a git remote with a given name and URL for the current repo or for all repos respectively.
  `repos.remote.set2` supports `${childRepo}` in the remote URL like `repos.process`.
  The existing implementation did not allow to set a custom remote name and therefore made it impossible to configure multiple remotes this way.