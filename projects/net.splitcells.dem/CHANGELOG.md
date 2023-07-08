----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
## [Unreleased]
### Major Changes
* **2023-06-27**:
  1. Rename `ListI#list()` into `ListI#_list()`,
    in order to simplify autocompletion for import statements in IDEs
    by avoiding name duplication with `Lists#list()`.
  2. Migrate `net.splitcells.dem.utils.ComonFunctions#removeAny` to
     `net.splitcells.dem.data.set.Set.removeAny()`.
### Minor Changes
* **2022-11-27 \#183**: Create `net.splitcells.dem.testing.TestExtensively` in order to execute any type of test.
* **2022-10-16 \#170**: Define the concept of a `ConnectingConstructor`.
