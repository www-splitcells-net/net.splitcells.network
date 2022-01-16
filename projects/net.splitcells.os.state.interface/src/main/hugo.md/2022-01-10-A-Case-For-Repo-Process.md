---
title: A Case For Repo Process
date: 2022-01-10
author: Mārtiņš Avots
license: EPL-2.0 OR MIT
---
# An Advantage Of SVN Over Git
> Git is a good version control system for one repository,
> but it seems to lack a protocol for managing multiple repos.

So, a week ago, I talked to a friend about one of SVN's big advantages over Git:
the ability to add sub repositories without the need for an additional repo
and software.

Git has the submodule feature,
which works somewhat similar,
but has one main difference:
a submodule is basically a link inside git to a commit of another git repo.
In order to add a sub repo via a submodule to a repo,
you therefore need an additional existing repo.

To my knowledge,
there is no general protocol in Git in order to create new repos on remote
servers.
Some servers allow one to push new repos over SSH via a push and will accept
these,
but most of them seem to require an additional action in a non-standard way,
in order to confirm the creation of such repos.

In other words:
SVN supports a way, where you can store all your data in one repository,
which is split among projects.
Each project has its own history and can be acted upon,
just by using SVN without additional tooling.
The downside being, that SVN's centralized nature makes it hard to unlock
the full potential of such a meta repo:
it seems to be unfeasible to have multiple independent instances of such a repo,
that are synchronized from time to time with each other.
## Meta Repos With An API
> Organizing all repositories with a meta repo.

A meta repository in this context is a repo,
which contains multiple sub repositories,
where each have their own content and history.
A meta repo can be used as a single point of access for all organization's
files.

It eliminates the need to interact with multiple and different services,
in order to edit files.
In such a case, it is not unheard of,
that such services are badly organized,
because every change takes a lot of effort as every server may work differently.
It can also be hard for new people to understand,
where certain files are located.

The following shows such a bad case:
```
Organization's Network
├── a:  Git Repo located at server 1 with Active Directory users
├── A:  Git Repo located at Server 2 with different users
├── A2: Git Repo on GitHub
├── b:  Git Repo on GitLab
├── B:  Git Repo on SourceHut
├── c:  This is the repo, that only one or 2 people know of, but everybody relies on implicitly.
└── ...
```
A meta repo can provide a single global namespace for projects,
where each repo contains a project.
If this is done smartly, this can be used in order to organize projects.
It is a lot easier to understand and organize some kind of project structure
instead of a long, unorganized list of projects.

Here you can see such a structure of projects:
```
Meta Repo
├──product
│  ├── a
│  ├── b
│  ├── c
│  └── ...
├──customer A
│  ├── contract
│  │  ├── A
│  │  └── B
│  └── ...
└── ...
```
# Some Abstract Advantages Of Meta Repos
> Access all data via one interface,
> but work on the files independently of each other.

If done correctly,
a meta repo provides a single point of access to all files and projects.
It does this by providing a path/namespace system,
where each project is identified by a path or name.
It forces one to categorize the inventory of one's data
and gives people a guidance for finding the things they want.
It can completely change the way, how one thinks of his data.

If the sub repositories of a meta repo have independent histories,
it is also possible to nest repos as much as one likes.

Integrating one meta repo into another one is feasible as well
and makes it easy to work with just part of data without downloading the
complete data of the repo.
Also, moving sensitive data into a separate repo,
with more strict access controls, is possible as well.

Furthermore, dedicated sub repositories can be used for artifacts,
that can change the way,
how one deploys software.

In short, a meta repo for all data,
if done correctly,
can make it possible to organize and work on projects independently of one
another and simultaneously provides a primary point of access for all data.
## Practical Usage Of Meta Repos
> Always backup your data on independent servers, if they are important.

The developer of the JavaScript libraries `colors.js` and `faker.js`,
deliberately changed the code in his repos in GitHub in such a way,
that the next version of these libraries did not work anymore
and [thereby destroyed the build of a number of dependent software projects](https://www.bleepingcomputer.com/news/security/dev-corrupts-npm-libs-colors-and-faker-breaking-thousands-of-apps/).

Because of this,
the [developer's GitHub access was suspended](https://www.techtimes.com/articles/270230/20220110/developer-gets-suspended-intentionally-sabotaging-github-open-source-libraries.htm),
but its access seems to be restored again as there are new commits from this
contributor as of the time of writing this article.
It is not the subject here,
if the actions of the developer were warranted or not.
Nether the less,
the main reason,
why the developer lost access to his repositories,
was the fact, that the hosting servers were not his own.

It also has to be noted,
that in principle one can lose access to one's GitHub account permanently.
Even if the public git repo servers are owned by the repo users,
a hardware or software failure can still destroy these repos.
Also, let us not forget,
that some also like to mirror the source code or artifact of their dependencies
(i.e. Maven proxy).

In short, creating backups for the version control system is not a bad thing,
but it can be cumbersome,
if there are many repos and one is not able to just mirror these
to a different server with one simple interaction.
# Repo Process
> A command, that iterates through each sub repo of a meta repo,
> can be used in order to work on multiple repos as if these were one.

The repo process is a command called `repo.process`, is part of
[OS state interface](https://github.com/www-splitcells-net/net.splitcells.network/tree/master/projects/net.splitcells.os.state.interface)
and is a Python 3 script.
When this project is installed,
the Python script is copied to `~/bin/net.splitcells.os.state.interface.commands.managed`.
This folder is added to the PATH variable for the user's shell,
and thereby the command can be used in the shell.

The repo process checks the file  `./.net.splitcells.os.state.interface.repo/subs.json`.
If no such file is present,
the command assumes that the current folder is a normal repository.
If such a file is present,
a list of sub repositories is read from it and
the repo process is called for each such sub repo.

For each repo of the process, 4 arguments are checked and propagated to each sub
repo:
* relative-path: When the user calls this function,
  it is set to the empty string by default and the current folder is assumed to
  be the root repo.
  During the iteration through all sub repos,
  this parameter is the relative path from the root repo to the current sub
  repo.
* command: This string is executed in the shell at the current folder.
  Before executing the string, the substring `$subRepo` is replaced with
  an empty string.
* command-for-missing: For every sub repo listed in the `subs.json` of the
  current repo, that is not actually present (sub repo folder does not exist),
  this string is executed in the shell.
  Before doing so, the folder of the sub repo is created,
  the substring `$subRepo` is replaced with the relative-path + `/[sub repo]`
  and the string is executed in the newly created folder.
  This can be used in order to download missing sub repositories.
* command-for-unknown:
  For every folder of the current repo/folder this command is executed,
  if this folder is not present in `subs.json`,
  because a meta repo is only allowed to contain sub repos and not other folders
  except for hidden folders.
  This can be used in order to delete a sub repository in all mirrors.
  Before doing so, `$subRepo` is replaced with relative-path + `/[sub repo]`
  and the execution is done in this folder.
* command-for-children:
  For every sub repo of the current repo,
  that is actually present,
  this parameter is executed in the current shell.
  Before doing so, `$subRepo` is replaced with relative-path + `/[sub repo]/$subRepo`
  and the execution is done in this folder.

This can be done recursively, if the provided parameters are itself calls
to repo process.
# Avoiding Git Specifics And Doing Recursive Processing
> Use `repo.*` commands for common tasks.

In order to pull data for the whole meta repo,
one could be tempted to call `repo.process --command-for-children='git pull'`.
It would work, but has the problem of taking a long time to write it down,
and it also only works if all sub repos are git repos.
So, you could not have git and mercurial repos in one meta repo.

Fortunately, here is where managed commands of the [OS state interface project](https://github.com/www-splitcells-net/net.splitcells.network/blob/master/projects/net.splitcells.os.state.interface/README.md)
come to action.
A managed command is basically a command,
that delegates its task to the command `command.managed.execute`.
Its main goal is to define an interface for a task and
its implementation is contained in a different command.
It's basically a form of dependency injection.
So let's take the example of pulling data from the server:

For pulling data from remote servers, the command `repo.pull` is defined,
and its content is the following code:
```
command.managed.execute disjunction repo.pull
```
This call basically does one thing:
1. It goes through the possible implementation names `repo.pull.[0...n]`.
2. Checks if the implementation exists by checking the shell's PATH variable.
   If this is not the case, the `repo.pull` call exits with an error.
3. If the command exists, it is executed.
   If the execution is successful, the `repo.pull` call is exited with a
   success.
   If the execution is not successful, it goes back to the 1st step.

In order to have a successful execution for a meta repo consisting of git
and Mercurial repos, 2 implementations have to be provided:
1. `repo.pull.0`: `git pull`
2. `repo.pull.1`: `hg pull`
# Complex Repo Processes Demonstration
> Synchronizing 2 meta repos.

In order to synchronize the local repo of the current folder in the command line
with a remote one,
the command `repo.synchronize.with` can be used,
as following:
```
repo.synchronize.with \
	--remote-host '[ssh server host]' \
	--remote-repo '[full URL for meta repo]'
```
Internally it roughly does the following thing:
```
system.network.peer.ssh.reachable {0} # Only do the follow, if the server is reachable.\
	&& repo.is.clean # Pause the command execution until the workspace is not dirty. \
	&& repo.remote.set {1} # Set the remote URLs for all repos. \
	&& repo.repair --remote-repo={1} # Download missing sub repositories and report error, if there is an unknown sub repo locally. \
	&& repo.pull # Pull data from remote server. \
	|| echo.error Could not synchronize with {0}. # Echo error, if there was an error during the process.
```
One can see that it is trivial,
to add a new server to the synchronization process.
# Implementation
> Not everything is dandy. 

Currently, there are 3 downsides of the repo process implementation:
1. Most `repo.*` commands just work on the main branch,
   because until now only this branch is a concern to me.
2. Currently, the `repo.process` command, does the recursion via shell calls.
   In other words, the Python scripts calls itself via the shell.
   This detail, is caused by the fact, that previously the `repo.process`
   command was implemented as a shell script.
3. There is no input validation of the command line parameters.
   One could, for instance, define a child repo name like `../../other-location`,
   which could lead to very strange situations.
   This currently causes a low security risk,
   because the input is considered to be trusted,
   as only the command user is expected to provide the parameters.
# Alternatives
> Of course, this is a not invented here syndrome.

Of course, there is similar software, but before I created the software I
could not find fitting alternatives,
that provided:
1. Easy switching between remotes.
2. Easy way of nesting meta repos, so it's easily and safely possible to use this
   process in order to synchronize public sub repos with public servers
   without risking publishing private repos by accident.
3. Support different implementations for common tasks,
   as there can always be important details,
   that need to be considered.
   Implementations should be easily changed, because adapting all existing
   synchronization scripts can be hard.
4. Ensure that it is easy to migrate from the chosen system to another one,
   by making the software simple.
   There is no guarantee,
   that git will be widely available in 30 years or that there will not be
   a standard for managing multiple repos.
6. Prefer Shell, Bash, Python 3 or Java for this,
   so that an additional language does not have to be supported.

It could be very well the case,
that I searched at the wrong places or did not understand some alternatives.
Here are some alternatives, that provide a somewhat similar functionality.
This demonstrates, that meta repos are indeed somewhat popular.

* [Javascript Project Meta](https://github.com/mateodelnorte/meta):
  seems to be the most similar tool and may have fit the bill,
  but I discovered the tool too late.
  I am also not sure,
  how easy it would be to support nested repos.
* [Google uses a single repository](https://cacm.acm.org/magazines/2016/7/204032-why-google-stores-billions-of-lines-of-code-in-a-single-repository/fulltext)
  for most of its source code, but it is not available to the public,
  as I understand it.
* Microsoft seems to use a large monorepo and has a special tool for that.
  [VGS for Git](https://github.com/microsoft/VFSForGit) was the first version
  and seems to be deprecated.
  It is replaced by [Scalar](https://github.com/microsoft/scalar),
  which offers a similar functionality with a different technical approach.
# Epilogue
> We can solve any problem by introducing an extra level of indirection. David Wheeler
> 
> ... except for the problem of too many levels of indirection.

Sometimes a repo (=monorepo) with many projects is a better idea,
as it is easier to manage a single repo than multiple one,
especially, if these are published at public software forges,
where each repo requires some manual work,
at least for the setup.
Also, keep in mind, that such a mono repo can always be split up into multiple
ones later on.