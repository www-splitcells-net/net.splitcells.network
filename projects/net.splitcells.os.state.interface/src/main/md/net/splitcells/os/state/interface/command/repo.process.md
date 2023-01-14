----
* SPDX-License-Identifier: EPL-2.0 OR MIT
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
## Repo Process Reasoning
### Objectives
Provide a way to create a collection of all file system based repositories of a user, which can be worked on as one.
This is especially useful for decentralized backups or if many projects need to be organized/used.

The necessary meta info should be stored as simple and portable as possible.
In best case scenario only the relative paths of the sub repos and its possible remote servers has to be stored.
### Solution
Support processing a tree of repositories (meta repo) and therefore allow working on all repos as one
(i.e. in order to backup everything).

The following tree structure is recommended for the meta repo:
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
## Implementation Details
Repo process is shell centric.
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