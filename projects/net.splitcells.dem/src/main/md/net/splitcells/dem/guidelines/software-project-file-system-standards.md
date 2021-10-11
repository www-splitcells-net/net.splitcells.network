# Software Project File System Standards
This is the aggregation of common file and folder structures in software
projects.
# Files at `./*`
Files a the top level should represent an entry point to the project.
A readme describing the project and linking to other documents is such files.
The project should contain only one readme a the top level.
Files specifying the project are another type of these files (i.e.
`Makefile`, `pom.xml` etc.).
These are used to import the project and to do tasks related to the project like compiling
the software.
# `./CHANGELOG.*` and `./changelog/**`
[Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
is a site describing a standard how changes to the project based on versioning can be documented.
Its source code can
be found on
[Github](https://github.com/olivierlacan/keep-a-changelog).
If the project uses Semantic Versioning, it may make sense to use the chapters
`Major Changes`, `Minor Changes` and `Patches`
for each release.
# Files at `./LICENSE.*` and `./license/**`
Contains licensing information. For the most part it contains copies of licenses.
# Files at `./README.*` and `./doc/readme/**`
Contains the initial description of the project.
Description in alternative languages should be placed under
`./doc/readme/**`.
# Files at `./.*`
Contains meta data like IDE configuration files or version control data.
# Files at `./bin/*`
Contains executable programs regarding tasks of the project.
It is preferred, if the scripts are written in such a way,
that the current folder is located at the project's root folder.
Such commands are called project commands.
# Files at `./doc/*`
Contains projects documentation.</paragraph>
## Files at `./doc/task` and `./doc/task/*`
Contains task of the projects that are relevant.</paragraph>
# Files at `./lib/*`
Contains dependencies of the project.</paragraph>
# Files at `./projects/*`
Contains folders with subproject. Prefer to not use recursive projects.
# Files at `./src/*`
Contains the source code of the project.</paragraph>
# Files at `./target/*`
Contains results of the build and test system. Also contains the results of
executing other project tasks.