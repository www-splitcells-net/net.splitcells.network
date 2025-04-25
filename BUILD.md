----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Building The Splitcells Network Project
The following describes, how all projects of the Splitcells Network Project can be build locally.
In order to build this project Java 21 ([Eclipse Temurin](https://adoptium.net/) is preferred), Maven, Git and Bash is required. 
# Cloning The Repositories
Only the [core repository](https://codeberg.org/splitcells-net/net.splitcells.network)
has to be manually cloned, in order to build the software.
Clone this repo into one directory by the following terminal command:
`git clone https://codeberg.org/splitcells-net/net.splitcells.network.git`
Execute `bin/workers.repos.pull` in the `net.splitcells.network` repo,
in order to download all other repositories.
# Building the actual code
Execute `bin/repos.build` of the `net.splitcells.network` repo and you are done.
# Windows specifics
When installing Git on Windows,
the setup provides some configuration options.
It is recommended to use the (preferred) `Checkout as-is, commit Unix-style` option or
at least the `Checkout Windows-style, commit Unix-style` option,
in order to prevent line ending problems.
Enable long paths in git via the `git config --global core.longpaths true` command in Git bash.

Make sure, that the [maximum supported path length](https://learn.microsoft.com/en-us/windows/win32/fileio/maximum-file-path-limitation?tabs=registry)
in your Windows installation is extended.
Otherwise, git clones may have problems.