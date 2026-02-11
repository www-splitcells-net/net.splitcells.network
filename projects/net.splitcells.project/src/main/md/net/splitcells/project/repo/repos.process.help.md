----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# The Repo Process Help
> Generates and executes a set of commands over a set of repositories given a set of command patterns.
## Flags
usage: repos.process
* `--command COMMAND`
* `[-h]`
* `[--relative-path RELATIVEPATH]`: this is the path of the currently processed sub repo.
* `[--host HOST]`
* `[--command-for-missing COMMANDFORMISSING]`
* `[--command-for-unknown COMMANDFORUNKNOWN]`
* `[--command-for-current COMMANDFORCURRENT]`
* `[--command-for-children COMMANDFORCHILDREN]`
## Settings via the environment
Setting the environment variable `log_level` to `debug`, enables debug level logging.
## Default environment variables inside patterns
All arguments starting with `command` support the variables `$childRepo` and `$peerRepo` inside its value.
These variables make it easy to create commands,
that pull or push a set of repos to a remote server by constructing the appropriate URLs via these variables.
For illustration purposes, let's assume the follow structure:
* `~/Documents/main-repo`
* `~/Documents/main-repo/sub-repo-1`
* `~/Documents/main-repo/sub-repo-2`
* `~/Documents/peer-repo-1`
* `~/Documents/peer-repo-2`

Illustration of the repo structure:
```
User home folder
└── Documents
    ├── main-repo
    │   ├── sub-repo-1
    │   └── sub-repo-2
    ├─peer-repo-1
    └─peer-repo-2
```
The meaning of the variables:
* The variable `$childRepo` is used mainly to push a tree of git repos to another server.
  This is often useful, when the target SSH server is hosted by oneself and the repositories are nested.
  `$childRepo` is replaced with `--relative-path + the name of the currently processed sub repo`.
  For example, given a relative-path of `~/Documents/main-repo` and the command `git clone ssh://user@remote/home/user/Documents/main-repo/$childRepo`,
  the following commands would be generated `git clone ssh://user@remote/home/user/Documents/main-repo`,
  `git clone ssh://user@remote/home/user/Documents/main-repo/sub-repo-1` and `git clone ssh://user@remote/home/user/Documents/main-repo/sub-repo-2`.
* The variable `$peerRepo` is used mainly to push a single folder of git repos.
  This is often useful, when the target server is a public server, where a user often can only manage a single folder of git repos.
  `$peerRepo` is replaced with the relative path from `--relative-path + /../ + the name of the currently processed peer repo`.
  For example, a relative-path of `~/Documents/main-repo` and the command `git clone ssh://user@remote/home/user/Documents/main-repo$peerRepo`,
  could generate the commands `git clone ssh://user@remote/home/user/Documents/main-repo`,
  `git clone ssh://user@remote/home/user/Documents/peer-repo-1` and `git clone ssh://user@remote/home/user/Documents/peer-repo-2`.
