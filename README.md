# net.splitcells.network

<img src="http://splitcells.net/net/splitcells/martins/avots/website/images/license.standard/net.splitcells.network.logo.jpg" width="50%" alt="Logo"/>

> Together we are more than the sum of our cells.

#### **[Introduction](#introduction)** | **[Where to start?](#where-to-start)** | **[Project Status](#project-status)** | **[Network's Structure](#networks-structure)** | **[Contacts](#contacts)** | **[Website](http://splitcells.net/net/splitcells/network/README.html)**

TODO: Simple Image Describing The Core Features

## Introduction

We provide an open source ecosystem centered around optimization and operations research.

The main project is the [Generic Allocator](http://splitcells.net/net/splitcells/gel/index.html).
It is a Java framework providing modeling, analytic and solving capabilities regarding optimization problems.
A detailed introduction and documentation can be found [here](http://splitcells.net/net/splitcells/gel/index.html).

An overview of the projects can be found [here](#networks-structure).
Some of them are not strictly related to optimization and can be used in other contexts as well.
[OS State Interface](projects/net.splitcells.os.state.interface/README.md) is the main example of such.

## Where to start?

🚀 [Model and optimize problems.](http://splitcells.net/net/splitcells/gel/index.html)

🏗️ [Deploy](http://splitcells.net/net/splitcells/network/deployment.html) the software.

🔬 Analyze and organize your operations and prepare schedules.

🤝 Collaborate large decision-making networks.

🗞️️ Get an insight into our thoughts via our [blog](https://splitcells-net.srht.site/) ([also on Gemini](gemini://splitcells-net.srht.site)) and programming progress via our [changelog](./CHANGELOG.md).

🦉 Get an [bird's-eye view](src/main/md/net/splitcells/network/overview.md).

🔭 Research optimization.

📚 Get [structured documentation](projects/net.splitcells.gel.doc/README.md).

✍ [Contribute](./CONTRIBUTING.md) to projects.

💰 Support [contributors](https://www.patreon.com/splitcells_net).

📣 Spread the word!

## Features

TODO: Short And Compact Feature Description

## Project Status
[![Continuous Integration](https://github.com/www-splitcells-net/net.splitcells.network/workflows/Continous%20Integration/badge.svg)](https://github.com/www-splitcells-net/net.splitcells.network/actions)
[![Gitlab Continuous Integration](https://gitlab.com/splitcells-net/net.splitcells.network/badges/master/pipeline.svg)](https://gitlab.com/splitcells-net/net.splitcells.network/-/pipelines)
[![builds.sr.ht status](https://builds.sr.ht/~splitcells-net/net.splitcells.svg)](https://builds.sr.ht/~splitcells-net)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/www-splitcells-net/net.splitcells.network.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/www-splitcells-net/net.splitcells.network/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/www-splitcells-net/net.splitcells.network.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/www-splitcells-net/net.splitcells.network/context:java)
[![Language grade: Python](https://img.shields.io/lgtm/grade/python/g/www-splitcells-net/net.splitcells.network.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/www-splitcells-net/net.splitcells.network/context:python)
[![Language grade: JavaScript](https://img.shields.io/lgtm/grade/javascript/g/www-splitcells-net/net.splitcells.network.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/www-splitcells-net/net.splitcells.network/context:javascript)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fwww-splitcells-net%2Fnet.splitcells.network.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fwww-splitcells-net%2Fnet.splitcells.network?ref=badge_shield)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fwww-splitcells-net%2Fnet.splitcells.network.svg?type=small)](https://app.fossa.com/projects/git%2Bgithub.com%2Fwww-splitcells-net%2Fnet.splitcells.network?ref=badge_small)

* [Changelog](./CHANGELOG.md)
* [Github Security Analysis](https://github.com/www-splitcells-net/net.splitcells.network/security):
  The [CodeQL](https://github.com/www-splitcells-net/net.splitcells.network/actions/workflows/codeql-analysis.yml)
  workflow has to be run manually.
  The results can be found [here](https://github.com/www-splitcells-net/net.splitcells.network/actions/workflows/codeql-analysis.yml).
### API Compatibility
There is no guarantee of backwards compatibility.

All API changes are located and categorized in the [Changelog](./CHANGELOG.md).
Breaking changes are tried to be omitted, but there is no guarantee for that.
The author of the software use this project as a dependency for their own
private code.
So there is at least an interest, to keep breaking changes to a minimum.
On the other hand, the API is not polished,
so there will be breaking changes to the API.

Absolute backward compatibility creates a maintenance burden and if any kind
of backward compatibility is required it may be best to just contact this
project.
We do not break backward compatibility just for fun and would like to support
efforts to minimize breaking changes.

You can try to decrease the likelihood of breaking a certain feature,
by contributing an appropriate test case/suite for this feature.
Regardless of that, keep in mind, that there is no guarantee of backwards
compatibility.
### Service Tasks
Tasks that are being worked cyclically,
and probably will never be finished.
* [#72 Improve Software Quality](./src/main/md/net/splitcells/network/tickets/open/72.md)
* Migrate inactive tickets into source code repository,
  so that they each one acts as trigger at one fitting position.
* Add inspirational quotes to code documentation in order to provide an
  alternative perspective via metaphors.
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
├── net.splitcells.network.log
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
  * `net.splitcells.network.log` contains data like benchmark results.
  * `net.splitcells.os.state.interface.lib.*` are command repositories,
    that can be used independently or can be registered to an installation
    of `net.splitcells.os.state.interface`.

## Social Contacts

* [Main Blog](https://splitcells-net.srht.site/)
* Livestreaming via [Twitch](https://www.twitch.tv/splitcellsnet) 
* Messaging via [Mastadon/Fosstodon](https://fosstodon.org/@splitcells)
* Blogging with Images on [Medium](https://martins-avots.medium.com/)

## Infrastructure
* Rendered Documentation via [a personal website](http://splitcells.net/net/splitcells/system/index.html)
* Tickets are managed via [Github](https://github.com/www-splitcells-net/net.splitcells.network/issues) and [Sourcehut](https://todo.sr.ht/~splitcells-net/net.splitcells.network).
* Repository Hosts:
  * [Github](https://github.com/www-splitcells-net/net.splitcells.network) is used mainly for general software development.
  * [Gitlab](https://gitlab.com/splitcells-net/net.splitcells.network) is currently used only for continuous integration (CI).
  * [sourcehut](https://sr.ht/~splitcells-net/net.splitcells.network) is used mainly for writing articles and web rendering.
## Contributing

* [Licensing](./LICENSE.md) and [Notices](./NOTICE.md) of This Project
* [Source Code <Guide>lines](projects/net.splitcells.dem/src/main/xml/net/splitcells/dem/guidelines/index.xml)

## Credits

* [Main Author's Public Key](https://keys.openpgp.org/search?q=F844A8297DEB16D9B9486323A6A6108FC3486F37)