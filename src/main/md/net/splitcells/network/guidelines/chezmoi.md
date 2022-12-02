----
* SPDX-License-Identifier: EPL-2.0 OR MIT
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Chezmoi Guidelines For Dotfile Management
Chezmoi is the recommended software in order to manage dotfiles.
## Initialize Chezmoi Repo
Init repo via `chezmoi init`.
The source repo is the repo, where dotfiles' commit history is stored.
Configure the location of the source repo at config file located at the folder `~/.config/chezmoi/` and
use the sub folder `src/main/chezmoi/*` as the base.
This way, the top level folder of the repo can be used for other meta data:
```
{
    "sourceDir": "[path to repo]/src/main/chezmoi"
}
```
## Notes
Use `chezmoi doctor` to check for warnings.
Add files to the Chezmoi repo via `chezmoi add`.
Commit changes to the Chezmoi repo manually, which is a Git repo.
## Why use Chezmoi?
[Dotbot](https://github.com/anishathalye/dotbot) was considered before,
as it is simpler and pretty popular and therefore hopefully mature.
Dotbot also only supports linking and not copying,
which is an issue for `~/.ssh`,
because Linux does not support permissions for symlinks.
Linking also creates issues, when the repo containing the dotfiles is deleted,
which happens during backup tests.

Chezmoi instead is considered, as it seems to be more recognized, supported and,
most importantly, supports applying dotfiles via copying instead of linking.
Chezmoi also provides a command, that configures dot files regardless of the current folder.
This ensures, that 2 different dot files repos are not used by accident.