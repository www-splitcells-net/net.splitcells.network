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