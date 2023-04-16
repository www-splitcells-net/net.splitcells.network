----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# The Repo Process
Repo process (`repo.process` is the name in the shell) is a command,
that enables one to iterate over a set of repos,
in order to interact with them as one.

This is done, by providing a program,
that takes a starting repo and a command pattern as an argument.
Repo process iterates over all sub repositories,
generates a command via the given pattern for each repo and
executes the generated command for each sub repo.

In order to make this VCS independent, additional `repo.*` commands are provided.
For instance, `repo.commit.all` is defined to commit all file changes.

[Here](https://splitcells-net.srht.site/blog/2022-01-10-a-case-for-repo-process/)
the initial blog article,
explaining the reasoning behind repo process,
can be found.
# Common Use Cases
The command `repo.process` provides a `--help` flag,
with details regarding all command flags.
## Add new remote to each sub repo.
This can be done via a Git specific command pattern:
```
repo.process --command='git remote add GitHub git@github.com:www-splitcells-net/$subRepo'
```
Alternatively, there is also a way, to do this more independently from Git.
This has the advantage of being independent of the underlying VCS.
Also, the concrete underlying implementation of updating the remote, can be changed later,
without adjusting the command itself.
This may be important, if such scripts are maintained for a longer period of time or
if errors are found in the git commands itself.
```
repo.remote.set git@github.com:www-splitcells-net
```
## Cloning Meta Repos
The following command clones a meta repo and its sub repos from another computer.
```
repo.clone.into.current ssh://some-user@computer.local:/home/some-user/repos
```
## Cloning Meta Repos From Git Hosters
A bug is preventing the peer repos being cloned.
So the following does not work at the moment.
```
repo.clone git@github.com:www-splitcells-net/net.splitcells.network.git
repo.clone git@gitlab.com:splitcells-net/net.splitcells.network.git
repo.clone git@git.sr.ht:~splitcells-net/net.splitcells.network
```
The nice thing about this, is the fact,
that this works with any Git hoster and without any hoster specific adaptations.
# Repo Process Reasoning
> Why are we doing this?
## Objectives
Provide a way to create a collection of all file system based repositories of a user, which can be worked on as one.
This is especially useful for decentralized backups or when many projects need to be organized/used.

The necessary meta info should be stored as simple and portable as possible.
In the simplest and still practical scenario only the relative paths of the sub repos and its possible remote servers has to be stored.

The program support a dry mode,
where an SH script is generated.
## Recommended Repo Organization
Support processing a tree of repositories (meta repo) and therefore allow working on all repos as one
(i.e. in order to backup everything).

The following tree structure is recommended for the meta repo,
in order to maximize the adaptability of the meta repo,
while still keeping a relatively simple folder structure:
The tree should only have 3 levels of root folders, that are processed by this.
The first level consists of one folder and is the root of the meta repo.

The second level splits the repositories into organisational units like private and public repositories.
A minimal number of second level repositories is recommended in order to ease administration.
If there is no need for such organization, the second level may be omitted.

The third level contains the roots of all repos containing the actual data.
There should be no repository roots of higher levels,
except if it is managed by the backend (i.e. git submodules).
Only third level repositories should be assumed to be fully publicly portable,
because a flat meta repo structure is easiest to support by hosting platforms (i.e. Github, Gitlab, sourcehut etc).

The first and second level repositories are only used in order to organize third level repositories by the user.
They are portable, but generally it is harder to migrate these to an other platforms.

It is encouraged to use globally unique names for each repo,
in order to be able to minimize the number of second level repositories.
Java package name convention is a good start for that.
# Implementation Details
> Every abstraction leaks.

The repo process is shell centric.
Every interaction with the VCS directly and many other interactions are done via shell calls.
This makes it possible, to replace every command completely,
but makes the integration of these commands more costly.

All actions done via repo process and co,
is done on the current branch by default,
in order to simplify things for now.

Repo process and its additional commands are intended to be installed via `command.managed.install`.
(see [install instructions](/net/splitcells/os/state/interface/manual/setup.md) for details).
In order to have default Git implementations for the abstract commands (`repo.*`),
[Os State Interface lib GPL 2](https://github.com/www-splitcells-net/net.splitcells.os.state.interface.lib.gpl.2)
has to be installed as well via `command.repository.register` and `user.bin.configure.sh`.

Alternatively, repo process commands can be used directly, as these are made without additional dependencies.
Therefore, only Python 3 is required.
Just make sure, that every required command is present in the environment path and
that the file suffixes are trimmed.
Every interaction between the commands is done via shell calls.
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
repo.process --command='grm repos find local $(pwd) --format yaml' > ../config.yml
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