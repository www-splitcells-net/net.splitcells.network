# Shell Software
## Recommended And Supported Software Stack
* [Fish](https://fishshell.com/):
  User/Beginner friendly shell with great autocompletion.
* [starship cross-shell prompt](https://starship.rs/):
  shell-prompt that is compatible with many shells.
* [chezmoi](https://github.com/twpayne/chezmoi): this is a dotfiles manager.
  It is a very recognized and maintained dotfile manager.
  Dotbot is not recommended instead, because it only supports sym linking and not copying,
  This is an issue for `~/.ssh`.
  because Linux does not support permissions for symlinks and therefore creates issues,
  when a ssh daemon is used.
  Linking also creates issues, when the source repo is deleted or moved,
  which can be the case, when one deletes all repos in order to test repo backups.
  Chezmoi also provides a command by default,
  that configures dot files regardless of the current folder in the shell.
  This ensures, that 2 different dot files repos are not used by accident.
## Interesting Shell Software
* [Zsh](https://www.zsh.org/): Advanced popular shell.
* [Awesome Shell](https://github.com/alebcay/awesome-shell): A curated list of awesome command-line frameworks, toolkits, guides and gizmos

As long as not otherwise noted,
this text is licensed under the EPL-2.0 OR MIT (SPDX-License-Identifier).
