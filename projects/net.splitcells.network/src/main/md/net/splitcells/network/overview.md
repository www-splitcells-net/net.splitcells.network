----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Project Structure Overview
## Project Relations
This <a href="https://github.com/mermaid-js/mermaid-cli">Mermaid overview graph</a> shows
the simplified relations between the projects.
An arrow from project A to project B means,
that either A provides something to B or that A interacts with B.
Keep in mind, that project A providing project B with things,
does not necessarily mean,
that A is a dependency of B.
For instance, `repos.process` manages git repos,
but the git repos themselves do not require `repos.process`.

The general idea behind this overview is,
that the more downward the viewer looks,
the more and more the projects represent end products,
instead of foundational modules.

<div align="center">
    <code class="mermaid">
graph TD
    developer --> Network
    Osi --> repos.process
    repos.process --> git
    Network --> Osi
    Network --> Blog
    Dem --> Worker
    Blog --> splitcells.net
    Network --> Pom
    Osi --> Osi.lib
    Pom --> Dem
    Gel --> Sep
    Worker --> Webserver
    Sep --> Cin
    Webserver --> splitcells.net
    Webserver --> Gel
    Webserver --> logs
    splitcells.net --> user
    </code>
</div>
<script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>

## Project And Repo Cluster Structure
This project is meant to be part of a cluster, with a certain filesystem structure in mind.
The cluster's filesystem consists of a folder containing repositories with minimal repo nesting:
```
Project Cluster
├── net.splitcells.network
│   └── projects
│       ├── net.splitcells.dem
│       ├── net.splitcells.gel
│       ├── net.splitcells.shell
│       ├── net.splitcells.system
│       └── ...
├── net.splitcells.network.log
├── net.splitcells.os.state.interface.lib.gpl.v2
├── net.splitcells.os.state.interface.lib.gpl.v2
└── ...
```
> This image illustrates the networks structure by showing relevant parts of the filesystem.

* [net.splitcells.network](http://splitcells.net):
  This repository integrates all projects, repositories and hosting services, that are part of the primary network.
    * [dem](./projects/net.splitcells.dem/README.md): Provides a standardized fundament for Java projects.
    * [gel](./projects/net.splitcells.gel/README.md): This framework delivers optimization capabilities.
    * [os.state.interface](./projects/net.splitcells.os.state.interface/README.md):
      The projects helps the user to organize and execute commands in the terminal via dependency injection.
    * [system](./projects/net.splitcells.system/README.md):
      Manages all integrated subprojects of the network.
      In particular, it can be used to use all integrated projects as a dependency.

Related projects are located in repositories, which are at the same folder as the
[net.splitcells.network](http://splitcells.net) project and are sometimes called peer projects/repos
(see [net.splitcells.osi.repos.peers files](https://codeberg.org/splitcells-net/net.splitcells.network.hub/src/branch/main/bin/net.splitcells.osi.repos.peers)).
These projects are not inside this repository and are managed more independently.
They may be managed by users with [OS state interface](./projects/net.splitcells.os.state.interface/README.md).
It is recommended to not nest repositories.
Examples are:

* [net.splitcells.network.log](https://codeberg.org/splitcells-net/net.splitcells.network.log) contains data like benchmark results.
* [net.splitcells.shell.lib.gpl.v2](https://codeberg.org/splitcells-net/net.splitcells.shell.lib.gpl.v2) is a command repository,
  that can be used independently or can be registered to an installation
  of `net.splitcells.shell`.