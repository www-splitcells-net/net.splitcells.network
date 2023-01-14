# Building The Splitcells Network Project
2 repositories are needed in order to build the software:
* `git@github.com:www-splitcells-net/net.splitcells.network.git`
* `git@github.com:www-splitcells-net/net.splitcells.network.bom.git`

Clone these into the same directory:
* `git clone git@github.com:www-splitcells-net/net.splitcells.network.git`
* `git clone git@github.com:www-splitcells-net/net.splitcells.network.bom.git`

Java 11 (AdoptOpenJDK is preferred), Maven, Python 3 and Bash is required in
order to build this project.
Execute `./bin/build`, that's it. ✨
The Java projects can be build via `mvn clean install` at the root folder.