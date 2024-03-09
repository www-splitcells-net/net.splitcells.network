# Splitcells™ Network Worker

> The Splitcells™ Network Worker provides commands for working on the repos and servers of the project cluster.

**TODO** This repo's content probably should be moved to the main repo,
as one of the main repos goals is to provide an interface for multiple backend.
This split just causes more work.

The commands' functionality can be provided by multiple backends:
* The simplest backend is based on CLI tools, provided by the operation system.
  This is used in order to bootstrap the build and may not provide all functionality.
  In general, this backend focuses on integrating external software into this project.
* More complex backends are used in order to work on these repos with portable code (i.e. written in Java) or
  to provide additional functionality.
  In general, this backend is used, when the program, build via the boostrapping backend,
  uses the repos as storage.