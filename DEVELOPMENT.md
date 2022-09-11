# Development With Splitcells Network
* [Licensing](./LICENSE.md) and [License Notices](./NOTICE.md) Of This Project
* [The project's source code guidelines](https://splitcells.net/net/splitcells/network/guidelines/index.html) may help you to better understand the API.
* [Development/Contribution Instructions](https://splitcells.net/net/splitcells/network/CONTRIBUTING.html) For Splitcells Network Itself
## Public Dependency Repository
Normally, one does normally build the complete system byself,
but there is a public m2 repository containing snapshot builds.
Keep in mind and check their dates as these are not always up to date.

The repository can be looked up at [GitHub](https://github.com/orgs/www-splitcells-net/packages).
In order to use these snapshots, one has to extend ones local `~/.m2/settings.xml`.
A working example can be found [here](src/main/xml-pom/net/splitcells/network/build-with-github-snapshot.pom.xml).

Provided your local terminal's current folder is the folder of this project,
one can use the example via the following command:
`mvn --settings src/main/xml-pom/net/splitcells/network/build-with-github-snapshot.pom.xml verify`.
## Adding The Dependency
The simplest way to start, is to add the following dependency:
```
<dependencies>
    <dependency>
        <groupId>net.splitcells</groupId>
        <artifactId>system</artifactId>
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