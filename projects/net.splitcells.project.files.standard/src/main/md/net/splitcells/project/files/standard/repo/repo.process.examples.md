----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Common Use Cases For Repo Process
> It's all about advertisement.

The following contains a small gallery of use cases,
in order to get a feeling,
of the programs capabilities and scope.
Look into the program's `--help` flag output via `repo.process --help`,
for more detailed documentation.
## Push to all servers.
```
repo.push.at.all
```
## Synchronize Servers
This uses some commands of the OSI project.
Note, that `repo.synchronize.with` downloads sub repos,
that are present on the remote, but not present at the local meta repo.
```
system.network.peer.ssh.reachable hostname \
	&& repo.synchronize.with \
		--remote-host 'hostname' \
		--remote-repo 'ssh://user@hostname:/path/to/repo/at/host/' \
	|| echo.error Could not synchronize with "hostname".
```
## Add new remote to each sub repo.
This can be done via a Git specific command pattern:
```
repo.process --command='git remote add GitHub git@github.com:www-splitcells-net/$subRepo'
```
Alternatively, there is also a way, to do this more independently from Git:
```
repo.remote.set 'git@github.com:www-splitcells-net/'
```
This has the advantage of being independent of the underlying VCS.
This way, the concrete underlying implementation of updating the remote, can be changed later,
without adjusting the command itself.
This may be important, if such scripts are maintained for a longer period of time or
if errors are found in the git commands itself.
## Cloning Meta Repos
The following command clones a meta repo and its sub repos from another computer.
```
repo.clone.into.current ssh://some-user@computer.local:/home/some-user/repos
```
## Cloning Meta Repos From Git Hosters
A bug is preventing peer repos being cloned.
So the following does not work at the moment.
```
repo.clone git@git.sr.ht:~splitcells-net/net.splitcells.network
```
The nice thing about this, is the fact,
that this works with any Git hoster and without any hoster specific adaptations.