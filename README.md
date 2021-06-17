# net.splitcells.network

<img src="http://splitcells.net/net/splitcells/martins/avots/website/images/license.standard/net.splitcells.network.logo.jpg" width="50%" alt="Logo"/>

> Together we are more than the sum of our cells.

![Continous Integration](https://github.com/www-splitcells-net/net.splitcells.network/workflows/Continous%20Integration/badge.svg)
![Gitlab Continous Integration](https://gitlab.com/splitcells-net/net.splitcells.network/badges/master/pipeline.svg)
![builds.sr.ht status](https://builds.sr.ht/~splitcells-net/net.splitcells.svg)

#### **[Introduction](#introduction)** | **[Where to start?](#where-to-start)** | **[Network's Structure](#networks-structure)** | **[Contacts](#contacts)** | **[Website](http://splitcells.net/net/splitcells/network/README.html)**

## Introduction

We provide an open source ecosystem centered around optimization and operations research.

The main project is the [Generic Allocator](projects/net.splitcells.gel.doc/src/main/md/net/splitcells/gel/index.md).
It is a Java framework providing modeling, analytic and solving capabilities regarding optimization problems.
A detailed introduction and documentation can be found [here](http://splitcells.net/net/splitcells/gel/index.html).

An overview of the projects can be found [here](#networks-structure).
Some of them are not strictly related to optimization and can be used in other contexts as well.
[OS State Interface](./projects/net.splitcells.os.state.interface/README.md) is the main example of such.

## Where to start?

🗞️️ Keep [updated](https://martins-avots.medium.com/).

🚀 [Model and optimize problems.](projects/net.splitcells.gel.doc/src/main/md/net/splitcells/gel/index.md)

🔬 Analyze and organize your operations and prepare schedules.

🤝 Collaborate large decision-making networks.

🔭 Research optimization.

📚 Get [structured documentation](projects/net.splitcells.gel.doc/README.md).

✍ Contribute to projects.

💰 Support [contributors](https://www.patreon.com/splitcells_net).

📣 Spread the word!

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

## Contacts

### Social Contacts

* Livestreaming via [Twitch](https://www.twitch.tv/splitcellsnet) 
* Messaging via [Mastadon](https://fosstodon.org/@splitcells) and [Twitter](https://twitter.com/splitcells)
* Writings on [Medium](https://martins-avots.medium.com/)

### Infrastructure

* Rendered Documentation via [my own website](http://splitcells.net/net/splitcells/index.html)
* Tickets are managed via [Github](https://github.com/www-splitcells-net/net.splitcells.network/issues) and [Sourcehut](https://todo.sr.ht/~splitcells-net/net.splitcells.network).
* Repository Hosts:
  * [Github](https://github.com/www-splitcells-net/net.splitcells.network)
  * [Gitlab](https://github.com/www-splitcells-net/net.splitcells.network) (Currently only used for CI.)
  * [sourcehut](https://sr.ht/~splitcells-net/net.splitcells.network)

## Contributing

* [Source Code Guidelines](http://splitcells.net/net/splitcells/dem/guidelines/index.html)
