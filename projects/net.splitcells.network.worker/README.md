----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Splitcells™ Network Worker
> The Splitcells™ Network Worker provides commands for working on the repos and servers of the project cluster.

The commands' functionality can be provided by multiple backends:
* The simplest backend is based on CLI tools, provided by the operation system.
  This is used in order to bootstrap the build and test on the local or remote computers.
  It may not provide all functionality.
  In general, this backend focuses on integrating external software into this project.
  This is provided by `bin/worker.*` files at this repo.
* More complex backends are used in order to work on these repos with portable code (i.e. written in Java) or
  to provide additional functionality like an UI.
  In general, this backend is used, when the program, build via the boostrapping backend,
  uses the repos as storage.