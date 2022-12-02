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