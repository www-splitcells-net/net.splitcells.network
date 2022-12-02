----
* SPDX-License-Identifier: EPL-2.0 OR MIT
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# `repo.process`
## `repo.process` Reasoning
### Objectives
Provide a way to create a collection of all file system based repositories of a user, which can be worked on as one.
This is especially useful for decentralized backups or if many projects needs to be organized/used.

The necessary meta info should be stored as simple and portable as possible.
In best case scenario only the relative paths of the sub repos and its possible remote servers has to be stored.
### Solution
Support processing a tree of repositories (meta repo) and therefore allows working on all repos as one
(i.e. in order to backup everything).

The following tree structure is recommended for the meta repo.
The tree should only have 3 levels of root folders, that are processed by this.
The first level consists of one folder and is the root of the meta repo.

The second level splits the repositories into organisational units like private and public repositories.
A minimal number of second level repositories is recommended in order to ease administration.
If there is no need for such organization the first and second level may be omitted.

The third level contains the roots of all repos containing the actual data.
There should be no repository roots in higher levels, except if it is managed by the backend (i.e. git submodules).
Only third level repositories should be assumed to be fully publicly portable,
because a flat meta repo structure is easiest to support by hosting platforms (i.e. Github, Gitlab, sourcehut etc).

The first and second level repositories are only used in order to organize third level repositories
by the user hosting the first and second level repository.
They are portable, but generally it is harder to migrate them to an other platform.

It is encouraged to use globally unique names for each repo in order to be able to minimize the number of second
level repositories.
Java package name convention is a good start for that.
