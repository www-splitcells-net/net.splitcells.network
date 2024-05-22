----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Splitcells™ Network Worker
> The Splitcells™ Network Worker provides commands for working on the repos and servers of the project cluster
> while defining in a portable and configurable way which resources are used for input and output.

The input and output resources are most of the time the storage systems and operation systems involved.
Storage systems are mostly configured in order to define what data is used.
OS configuration is mostly involved into managing the runtime,
which is useful for developers in order to replicate remote servers locally.

The Network Worker focuses more on project workflows,
whereas the OS State Interface project focuses more on integrating local software into a given project.

The command are provided by the `bin/worker.*` files at the top of this repo.
These commands are intended to be executed at the respective root folder of this repo.

The commands' functionality can be provided by multiple backends:
* The simplest backend is based on CLI tools, provided by the operation system.
  This is used in order to bootstrap the build and test on the local or remote computers.
  It may not provide all functionality.
  In general, this backend focuses on integrating external software into this project.
* More complex backends are used in order to work on these repos with portable code (i.e. written in Java) or
  to provide additional functionality like an UI.
  In general, this backend is used, when the program, build via the boostrapping backend,
  uses the repos as storage.