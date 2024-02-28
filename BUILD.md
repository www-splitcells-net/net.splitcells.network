----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Building The Splitcells Network Project
# Cloning the repositories
2 repositories are needed in order to build the software:
* `git@github.com:www-splitcells-net/net.splitcells.network.git`
* `git@github.com:www-splitcells-net/net.splitcells.network.bom.git`

Clone these into the same directory:
* `git clone git@github.com:www-splitcells-net/net.splitcells.network.git`
* `git clone git@github.com:www-splitcells-net/net.splitcells.network.bom.git`
# Building the actual code
Java 21 ([Eclipse Temurin](https://adoptium.net/) is preferred), Maven, Python 3, Git and Bash is required in
order to build this project.
Execute `./bin/build`, that's it. ✨
The Java projects can be build via `mvn clean install` at the root folder.
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