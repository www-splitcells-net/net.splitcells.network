# net.splitcells.network

<img src="http://splitcells.net/net/splitcells/martins/avots/website/images/license.standard/net.splitcells.network.logo.jpg" width="50%" alt="Logo"/>

> Together we are more than the sum of our cells.

#### **[Introduction](#introduction)** | **[Where to start?](#where-to-start)** | **[Project Status](#project-status)** | **[Network's Structure](#networks-structure)** | **[Contacts](#contacts)** | **[Website](http://splitcells.net/net/splitcells/network/README.html)**

## Introduction

We provide an open source ecosystem centered around optimization and operations research.

The main project is the [Generic Allocator](projects/net.splitcells.gel.doc/src/main/md/net/splitcells/gel/index.md).
It is a Java framework providing modeling, analytic and solving capabilities regarding optimization problems.
A detailed introduction and documentation can be found [here](projects/net.splitcells.gel.doc/src/main/md/net/splitcells/gel/index.md).

An overview of the projects can be found [here](#networks-structure).
Some of them are not strictly related to optimization and can be used in other contexts as well.
[OS State Interface](projects/net.splitcells.os.state.interface/README.md) is the main example of such.

## Where to start?

🚀 [Model and optimize problems.](projects/net.splitcells.gel.doc/src/main/md/net/splitcells/gel/index.md)

🔬 Analyze and organize your operations and prepare schedules.

🤝 Collaborate large decision-making networks.

🗞️️ Get an insight into our thoughts via our [blog](https://splitcells-net.srht.site/) and programming progress via our [changelog](./CHANGELOG.md).

🔭 Research optimization.

📚 Get [structured documentation](projects/net.splitcells.gel.doc/README.md).

✍ Contribute to projects.

💰 Support [contributors](https://www.patreon.com/splitcells_net).

📣 Spread the word!

## Project Status

[![Continuous Integration](https://github.com/www-splitcells-net/net.splitcells.network/workflows/Continous%20Integration/badge.svg)](https://github.com/www-splitcells-net/net.splitcells.network/actions)
[![Gitlab Continuous Integration](https://gitlab.com/splitcells-net/net.splitcells.network/badges/master/pipeline.svg)](https://gitlab.com/splitcells-net/net.splitcells.network/-/pipelines)
[![builds.sr.ht status](https://builds.sr.ht/~splitcells-net/net.splitcells.svg)](https://builds.sr.ht/~splitcells-net)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/www-splitcells-net/net.splitcells.network.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/www-splitcells-net/net.splitcells.network/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/www-splitcells-net/net.splitcells.network.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/www-splitcells-net/net.splitcells.network/context:java)
[![Language grade: Python](https://img.shields.io/lgtm/grade/python/g/www-splitcells-net/net.splitcells.network.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/www-splitcells-net/net.splitcells.network/context:python)
[![Language grade: JavaScript](https://img.shields.io/lgtm/grade/javascript/g/www-splitcells-net/net.splitcells.network.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/www-splitcells-net/net.splitcells.network/context:javascript)

* [Github Security Analysis](https://github.com/www-splitcells-net/net.splitcells.network/security)

* [Changelog](./CHANGELOG.md)
* Current Version: 3.0.0-SNAPSHOT:
  This version therefore takes a lot of time until it is done.
* Also, drastic changes are not planned, API is currently not stable.
* Some integration and production like tests are present, but the software is currently not used in production.
* Licensing is currently not cleaned up.
  Contributing on source code level is therefore currently not encouraged.

### Planning
* Current Tickets:
  * Solve new problems.
     * [#8 Solve school scheduling problem.](https://github.com/www-splitcells-net/net.splitcells.network/issues/8)
     * [#9 Solve sport lesson assignment.](https://github.com/www-splitcells-net/net.splitcells.network/issues/9)
     * [#34 Try to get 1 optimization configuration, that solves all 3 new major problems.](https://github.com/www-splitcells-net/net.splitcells.network/issues/34)
  * [#10 Create minimal Java grammar for this project in order to make code less complex.](https://github.com/www-splitcells-net/net.splitcells.network/issues/10)
  * [#26 Support arbitrary renderers for website server.](https://github.com/www-splitcells-net/net.splitcells.network/issues/26)
  * [#37 Simplify default web layout.](src/main/md/net/splitcells/network/tickets/open/37.md)
  * [#55 Minimize number of used languages and protocols.](https://github.com/www-splitcells-net/net.splitcells.network/issues/55)
  * [#58 Publish private documentation of Version 2.0.](https://github.com/www-splitcells-net/net.splitcells.network/issues/58)
  * [#64 Create git based ticket system.](https://github.com/www-splitcells-net/net.splitcells.network/issues/69)
  * [#70 Create Version 3 of Gel.](https://github.com/www-splitcells-net/net.splitcells.network/issues/70)
    * [#2 Determine Licensing and Contribution protocol.](https://github.com/www-splitcells-net/net.splitcells.network/issues/2)
    * [#63 Define API development model.](https://github.com/www-splitcells-net/net.splitcells.network/issues/63)
    * [#64 Define project development model.](https://github.com/www-splitcells-net/net.splitcells.network/issues/64)
### Service
* [#72 Improve Software Quality](./src/main/md/net/splitcells/network/tickets/open/72.md)
## Network's Structure
This project is meant to be part of a cluster, with a certain filesystem structure in mind.
The cluster's filesystem consists of a folder containing repositories:

```
Project Cluster
├── net.splitcells.network
│   └── projects
│       ├── net.splitcells.dem
│       ├── net.splitcells.gel
│       ├── net.splitcells.os.state.interface
│       ├── net.splitcells.system
│       └── ...
├── net.splitcells.os.state.interface.lib.gpl.2
├── net.splitcells.os.state.interface.lib.gpl.3
└── ...
```
> This image illustrates the networks structure by showing relevant parts of the filesystem.

* [net.splitcells.network](http://splitcells.net):
  This repository integrates all projects, repositories and hosting services, that are part of the network.
  * [dem](./projects/net.splitcells.dem/README.md): Provides a standardized fundament for Java projects.
  * [gel](./projects/net.splitcells.gel/README.md): This framework delivers optimization capabilities.
  * [os.state.interface](./projects/net.splitcells.os.state.interface/README.md):
    The projects helps the user to organize and execute commands in the terminal via dependency injection.
  * [system](./projects/net.splitcells.system/README.md):
    Manages all integrated subprojects of the network.
    In particular, it can be used to build all integrated projects.
* **Related projects/repositories**:
  Related projects are located in repositories, which are at the same folder as the
  [net.splitcells.network](http://splitcells.net) project. 
  These projects are not inside this repository and are managed more independently.
  They may be managed by users with [OS state interface](./projects/net.splitcells.os.state.interface/README.md).
  It is recommended to not nest repositories.

## Social Contacts

* [Main Blog](https://splitcells-net.srht.site/)
* Livestreaming via [Twitch](https://www.twitch.tv/splitcellsnet) 
* Messaging via [Mastadon/Fosstodon](https://fosstodon.org/@splitcells)
* Blogging with Images on [Medium](https://martins-avots.medium.com/)

## Infrastructure

* Rendered Documentation via [a personal website](http://splitcells.net/net/splitcells/index.html)
* Tickets are managed via [Github](https://github.com/www-splitcells-net/net.splitcells.network/issues) and [Sourcehut](https://todo.sr.ht/~splitcells-net/net.splitcells.network).
* Repository Hosts:
  * [Github](https://github.com/www-splitcells-net/net.splitcells.network)
  * [Gitlab](https://github.com/www-splitcells-net/net.splitcells.network) (Currently only used for CI.)
  * [sourcehut](https://sr.ht/~splitcells-net/net.splitcells.network)

## Contributing

* [Source Code Guidelines](http://splitcells.net/net/splitcells/dem/guidelines/index.html)
