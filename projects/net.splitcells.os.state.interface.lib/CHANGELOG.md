----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Changelog
## [Unreleased]
### Major Changes
* **2023-01-24** Delete `system.update` implementation for docker.
  This implementation did not make much sense,
  as it downloaded the newest version of each image being present.
  Thereby, no containers were being updated.
  This also steadily increased the number of local images and therefore storage usage,
  because the old ones were not deleted.