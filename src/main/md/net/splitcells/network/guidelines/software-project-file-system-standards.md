----
* SPDX-License-Identifier: EPL-2.0 OR MIT
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Software Project File System Standards

This is the aggregation of common file and folder structures in software
projects.
## Software Project File System Overview
```
Top Level Folder
├── bin
│   ├── [Project Commands]
│   └── [...]
├── [.*]
│   └── [...]
├── [.config]
│   ├── hakoerber.git-repo-manager
│   │   └── [config.yml]
│   └── [Software Specific Config Folders]
│       └── [...]
├── .reuse/dep5
├── lib
│   └── [...]
├── LICENSES
│   └── [...]
├── src
│   ├── doc
│   │   └── [Source Type]
│   │       └── [Files Of Source Type]
│   └── main
│       └── [Source Type]
│           └── [Files Of Source Type]
├── projects
│   ├── [Project Top Level Folders]
│   └── [...]
├── BUILD.*
├── CHANGELOG.*
├── CONTRIBUTING.*
├── DEVELOPMENT.*
├── NOTICE.*
├── pom.xml
├── README.*
└── [...]
```
## `net.splitcells.network` Specific Standard
The number of non-hidden files and folders at the top level folder should be minimized,
in order to simplify the introduction of the repo and
simplify the overview and overhaul structure of the repo.

Every project should have a POM at its root folder,
even if such a project does not use Maven.
By defining a Maven multi project module,
this can be used to import many of such projects into an IDE
(i.e. Eclipse and Intellij) with one import command.
## Files and Folders at `./*`
Files and folders at the top level should represent an entry point to the
project.
The number of files and folders at the top level should be minimized,
in order to have an easy overview at the entry level.

A readme describing the project and linking to other documents is such file.
Files specifying the project type are another type of these files (i.e.
`Makefile`, `pom.xml` etc.).
These are used to import the project and to do tasks related to the project like
compiling the software.

Try to minimize the number of non-hidden files and folders,
in order to provide a good starting point and overview of the project
without overwhelming the visitor.
## Files `./CHANGELOG.*`
[Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
is a site describing a standard how changes to the project based on versioning
can be documented.
Its source code can be found on [Github](https://github.com/olivierlacan/keep-a-changelog).
If the project uses Semantic Versioning, it may make sense to use the chapters
`Major Changes`, `Minor Changes` and `Patches` for each release.
## Copyright Files
### Files at `./LICENSE.*` and `./NOTICE.*`
Contains non-standardized licensing information.
### REUSE Conforming Files
See [REUSE software recommendations](https://reuse.software/) for more details. 
* `./reuse/dep5`
* `./LICENSES/*`
## Files at `./README.*`
Contains the initial description of the project.
## Files at `./.*`
Contains meta data like IDE configuration files or version control data.
## Files at `./bin/*`
The files are located at `./bin/*`.
Contains executable programs regarding tasks of the project.
It is preferred, if the scripts are written in such a way,
that the current folder is located at the project's root folder.
Such commands are called project commands.
## Files at `./lib/*`
Contains dependencies of the project.
## Files at `./projects/*`
Contains folders with subproject. Prefer to not use recursive projects.
## Files at `./src/main/[file-format]/*`
Contains the source code of the project.

Documentation is placed here for now,
because currently there is no need to make a distinction between files,
that are part of the program and files that are part of the documentation.
The build process creating the documentation determines,
which files are used for documentation.

One reason for this, is the fact, that the documentation might link to source code,
if the documentation's target audience are programmers.

Links in the README are the preferred way to find the documentation's root file.

Documentation and program source code files about the same topic should have the same
project path (`./src/main/[file-format]/[project-path].[file-suffix]`).
This is especially useful, if the program source cannot be used in order to generate
a fitting documentation for the program source's project path.

Following file formats are explicitly supported, but any formats can be used:
* `src/main/bash`
* `src/main/chezmoi`
* `src/main/csv`
* `src/main/css`
* `src/main/java`
* `src/main/hugo.md`
* `src/main/m2`
* `src/main/md`
* `src/main/null`
* `src/main/sh`
* `src/main/svg`
* `src/main/txt`
* `src/main/xml`
* `src/main/sum.xml`: This file format is like XML, but without the root element and the XML header.
  In other words, this file is a collection of XML elements,
  where just appending XML elements without doing anything else to such files results in still valid files.
  This makes it possible to easily render this file, even though the file is not fully written yet.
  This is often the case, when the program is still running.
## Files at `./target/*`
Contain results of the build and test system. Also contains the results of
executing other project tasks.

----
* SPDX-License-Identifier: EPL-2.0 OR MIT
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects