# Splitcells™ Network Worker

> The Splitcells™ Network Worker provides commands for working on the repos and servers of the project cluster.

The commands' functionality can be provided by multiple backends:
* The simplest backend is based on CLI tools, provided by the operation system.
  This is used in order to bootstrap the build and may not provide all functionality.
  In general, this backend focuses on integrating external software into this project.
* More complex backends are used in order to work on these repos with portable code (i.e. written in Java) or
  to provide additional functionality.
  In general, this backend is used, when the program, build via the boostrapping backend,
  uses the repos as storage.