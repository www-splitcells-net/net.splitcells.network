----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Development With Splitcells Network
* [Licensing](./LICENSE.md) and [License Notices](./NOTICE.md) Of This Project
* [The project's source code guidelines](https://splitcells.net/net/splitcells/network/guidelines/index.html) may help you to better understand the API.
* [Development/Contribution Instructions](https://splitcells.net/net/splitcells/network/CONTRIBUTING.html) For Splitcells Network Itself
## Adding The Dependency
The simplest way to start, is to add the following dependency:
```
<dependencies>
    <dependency>
        <groupId>net.splitcells</groupId>
        <artifactId>network.system</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```
If you don't have a Maven parent POM and are looking into using one,
instead of defining some defaults on your own,
you might want to use the following parent.
It enforces the used Java version etc.
```
<parent>
    <groupId>net.splitcells</groupId>
    <artifactId>pom-java-defaults</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath/>
</parent>
```