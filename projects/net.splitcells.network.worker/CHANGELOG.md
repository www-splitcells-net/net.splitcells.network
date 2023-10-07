----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
The changelog format can be found [here](../../src/main/md/net/splitcells/network/guidelines/changelog.md).

## [Unreleased]
### Major Changes
* **2022-10-21**: **\#162** Move project command `bin/execute` and `bin/execute.extensive.tester` to the `net.splitcells.system` project,
     in order to ensure, that all tests are executed.
### Minor Changes
* **2022-03-20**: **\#162** Create network worker project (`projects/net.splitcells.network.worker`).
### Patches
* **2023-10-07: \#257 Recover network log data and fix data loss bug:**
    The migration from Java's Filesystem API to the Network's own file system API lead to a bug,
    where the CSV files containing the runtime of the tests were overwritten by the Network worker.
    Thereby, the CSV files only contained one runtime entry.
    Fortunately, the network log git repo containing the CSVs could be reset and
    thereby the runtime performance data could be restored as well.