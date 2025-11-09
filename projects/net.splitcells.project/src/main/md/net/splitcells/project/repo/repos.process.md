----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# The Repo Process

* [Help page for parameters](repos.process.help.md)
* [Use cases](repos.process.examples.md)

# NOTICE

After thinking and researching about git's submodule for a while,
all functionality of repo process could have been done via git's submodules kind of easily as well.
It would require wrapper scripts for git's submodules,
as using git's submodules directly seems otherwise to be unnecessarily hard,
but repo process required creating scripts as well.
So, writing my own sub repo aka. submodule management from the ground up,
may have been a big error caused by a misunderstanding of git submodules.
The only advantage of repo process is the simplicity of the git commands, that are executed.
Maybe rewriting the history of meta repos with the current scripts is easier compared to git submodules.

# Introduction
> What is this?
 
Illustration of an exemplary meta repo structure:
```
Meta repo root
├── Repo of organization Alpha
│   ├── Project Gamma
│   ├── Project Delta
│   └── ...
├── Repo of organization Beta
│   ├── Project Zeta
│   ├── Project Eta
│   └── ...
```
The command repo process (`repos.process` is the name in the shell)
provides a highly portable and simple way to process a set of repos as one repo.

Its config files are simple,
so that either complex programs can process these repos
or people with nothing more than a shell and git can work on such repos.
This is especially useful, when during an initial repo clone some software is not setup yet.
The simplicity is also helpful for the setup procedure of complex development projects
consisting of multiple git repos and without requiring additional tools to be installed during the initial setup.

Initially this tool was created to synchronize repos across multiple servers and computers.
This is mainly done, by providing the `repos.process` program,
that takes a root repo and a command pattern as an argument.
Repo process iterates over all sub repositories,
generates commands via the given pattern for each repo and
executes the generated command for each sub repo.
Depending on the requirements,
this can even be used in order to add new or remove old repos from servers. 

The repos are assumed to be organized in a tree structure.
Therefore, the root repo (here called meta repo) contains the sub repos,
but usually does not provide version control of the sub repos
(for Git repos the subs are generally listed in `.gitignore`).
If the meta repo requires version control for the sub repos,
then Git's submodule system is probably more suitable for this at the moment.
So the main difference between repo process and git's submodule is the fact,
that repo process is usually not used to have an exact inventory of the repositories' commits,
but makes it easier for repo process to synchronize repos and branches of independent projects across a cluster of computers compared to git's submodule.

By default, every meta repo has a list of all sub repos stored in a file under version control of the meta repo.

In order to make this VCS independent, additional `repo.*` commands are provided,
that are built on top of repo process.
For instance, `repos.commit.all` is defined to commit all file changes of the current repo and its subs,
but it relies on other additional backend implementations,
that actually do this job.

[Here](https://splitcells-net.srht.site/blog/2022-01-10-a-case-for-repo-process/)
the initial blog article,
explaining the reasoning behind repo process,
can be found.

# Implementation Details
> Every abstraction leaks.

The repo process is shell centric.
Every interaction with the VCS directly and many other interactions are done via shell calls.
This makes it possible, to replace every command as one likes to.

All actions done via repo process and co,
is done on the current branch by default,
in order to simplify things for now.

Repo process and its additional commands are intended to be installed via `command.managed.install`.
(see [install instructions](/net/splitcells/shell/manual/setup.md) for details).
In order to have default Git implementations for the abstract commands (`repo.*`),
[Os State Interface lib GPL 2](https://github.com/www-splitcells-net/net.splitcells.os.state.interface.lib.gpl.v2)
has to be installed as well via `command.repository.register` and `user.bin.configure.sh`.

Alternatively, repo process commands can be used directly, as these are made without additional dependencies.
Therefore, only Python 3 is required.
Just make sure, that every required command is present in the environment path and
that the file suffixes are trimmed.
# Recommended Repo Organization
## Meta Repo Structure
> How could one organize repos? Where do I put a new repo without thinking to much about it?

Support processing a tree of repositories (meta repo) and therefore allow working on all repos as one
(i.e. in order to back up everything).

The following tree structure is recommended for the meta repo,
in order to maximize the adaptability of the meta repo,
while still keeping a relatively simple folder structure:
The tree should only have 3 levels of root folders, that are processed by this:

```
Meta repo root
├── Organization 1
│   ├── Project 1
│   ├── Project 2
│   └── ...
├── Organization 2
│   ├── Project 1
│   ├── Project 2
│   └── ...
```

The first level consists of one folder and is the root of the meta repo.

The second level splits the repositories into organisational units like private and public repositories.
If there is no need for such organization, the second level should be present nevertheless.
By using globally unique organization names based on the Java package name convention,
one can ensure, that meta repos can be combined without conflicts.
This in turns makes it easy to add repos from a foreign server to the meta repo.

The third level contains the roots of all repos containing the actual data.
There should be no repository roots of higher levels,
except if it is managed by the backend (i.e. git submodules).
Only third level repositories should be assumed to be fully publicly portable,
because a flat meta repo structure is easiest to support by hosting platforms (i.e. Github, Gitlab, SourceHut etc).

The first and second level repositories are only used in order to organize third level repositories by the user.

It is encouraged to use globally unique names for each repo,
in order to be able to minimize the number of second level repositories.
Java package name convention is a good start for that.
## Partial Backups And Data Distribution
For some repos it is necessary to restrict access to it.
These may be repos containing personal data like invoices.
Other repos like open source repos may not need such restrictions,
which leads to a situation where some repos are present only on some storage sites.
Other repos may require too much storage space and
therefor may have a limited number of mirror servers as well.

This in turn may make it very complicated to ensure,
that all repos, mirrors and backups are up-to-date,
if no central accounting for all repos is present.
Such lack of accounting makes it also hard to reliably synchronize all repos via one command.
Instead, at least one command would have to be executed at each storage site.
### Central Server Backups
However, one could use a central server as centralized inventory list of all repositories.
This server requires the highest security clearance,
so it is allowed to store any data.

In order to synchronize such a network,
the central server pulls all data from the secondary servers and
thereby establishes a single source of true.
After that, all secondary servers can pull the updated data or
the central server could push all data to the secondary servers.
### Distributed Inventory List
Alternatively, the meta repo contains a repo, that entails a list of all servers.
This repo would have to be present on all servers.

If every server only pulls updates for the organizations, that it itself hosts,
it is ensured, that private data is not leaked.
Simultaneously, a somewhat global inventory list of all repos is provided and
can be updated via an edit at one server.
This edit will be distributed via subsequent server synchronizations.
# Alternatives
> Of course, this is a not invented here syndrome.

Of course, there is similar software, but before the software was created,
fitting alternatives, that provided following functionality, were not found.
Keep in mind, that it could very well be the case,
that the missing functionality could have been available in alternative software,
by using them in creative ways.
Unfortunately, these ways may have been overlooked.

1. Easy switching between different remotes.
2. Easy way of nesting meta repos, so it's easily and safely possible to use this
   process in order to synchronize public sub repos with public servers and
   without risking publishing private repos by accident.
   Simultaneously, synchronization of private and public repos with private servers
   should be easy and consist of only one manual step, in order to minimize user error.
   In other words, synchronization with private servers should be simple,
   while synchronization with public servers should not endanger private repos.
3. Support different implementations for common tasks,
   as there can always be important details,
   that need to be considered.
   Implementations should be easily changed, because adapting all existing
   synchronization scripts can be hard.
4. Ensure that it is easy to migrate from the chosen system to another one,
   by making the software simple and replaceable.
   There is no guarantee,
   that git will be widely available in 30 years or that there will not be
   a standard for managing multiple repos in the future.

Here are some alternatives.
Some of them are viable and some not:

## Git Submodules

As noted in the introduction now it seems I could have just used git submodules as well,
provided I would use custom wrapper scripts for that,
as the CLI is not that easy to use.
These wrapper scripts could have been an alternative version of the currently used repo process scripts.
Such wrappers could even have provided the same CLI interface.
When repo process was created a misunderstanding lead to the belief,
that switching and synchronizing multiple independent remotes for suche repos was not easily possible. 

## GRM — Git Repository Manager
[This software](https://github.com/hakoerber/git-repo-manager)
is the closest thing, to feature parity, when looking at the requirements.
It was implemented a few years after the repo process and was therefore not considered at
the time of creating the process.

It can search for repos [on remotes](https://hakoerber.github.io/git-repo-manager/forge_integration.html)
and [in the local file system](https://hakoerber.github.io/git-repo-manager/local_configuration.html#generate-your-own-configuration),
although, nested repos are not explicitly supported [yet](https://github.com/hakoerber/git-repo-manager/issues/49).

For the time being the following command can create a config file of a meta repo for GRM.
One has to keep in mind, that in the output every instance of `trees:`,
except the very first one, has to be removed,
because the output of the command is the concatenation of the config files for each git repo.
```
repos.process --command='grm repos find local $(pwd) --format yaml' > ../config.yml
```
## Javascript Project Meta
The [Javascript project meta](https://github.com/mateodelnorte/meta)
seems to be a similar tool.

The major downside of this is, that no repo nesting is explicitly supported.
Nesting could be achieved, by creating a dedicated config file for each level and sub meta repo.
I also think, that complete support for each remote, would have to be implemented,
by creating a new config file for each meta repo and each remote.

In other words, in order to use this software for nested repos with multiple remotes,
one would probably have to create config files on the fly.
This probably could be done via repo process,
so future compatibility is possible.
## Google's Mono Repo
[Google uses a single repository](https://cacm.acm.org/magazines/2016/7/204032-why-google-stores-billions-of-lines-of-code-in-a-single-repository/fulltext)
for most of its source code, but it is not available to the public,
as I understand it.
## Microsoft's Big Repo(s)
Microsoft seems to use a large monorepo and has a special tool for that.
[VGS for Git](https://github.com/microsoft/VFSForGit) was the first version
and seems to be deprecated.
It is replaced by [Scalar](https://github.com/microsoft/scalar),
which offers a similar functionality with a different technical approach.

## grm - git repo manager

This project was found at 2024-11-16.
The [git repo manager](https://github.com/Krasjet/grm) does not support repo nesting,
which makes rights management a bit more fragile.
It's configured via a environment variable, that points to the root of the meta repo.
The sub repos are found by listing all direct child directories,
that end with `.git` in its name.
GitHub had as of 2024-11-16 problems with such repos.

grm does not seem to really support doing git or any version control commands on all sub repos via one command.
Therefore, grm is not a viable option to manage meta repos.
