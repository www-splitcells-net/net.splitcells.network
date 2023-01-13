----
* SPDX-License-Identifier: EPL-2.0 OR MIT
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Standard Maven Pom for Java Projects for splitcells.net
## Profiles
### deployable-jar
Creates deployable jars:
* `${project.build.directory}/dependency/*`: contains the projects dependencies.
* `${project.build.directory}/*.jar`: this is the jar of the actual source code.

Copy the jars to the program's folder and execute it via `java -cp <program's folder>/* <Main Class To Execute>`.