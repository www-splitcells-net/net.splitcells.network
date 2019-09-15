## Tasks
### Current Tasks
1. Document user cases and project architecture.
1. Define installation order.
1. Move tasks to other projects if suitable.
1. Faster update process via increment updates.
1. Backup ~/bin/* folders during install via source control.
1. Use sh instead of bash via the shebang "#!/usr/bin/env sh" in order to avoid GPL dependencies.
1. Create and use method to only partly implement a feature.
   The script user should be informed, if the missing part is required in the user's case.
1. Make managed commands with same name easier to identify.
   Expand existing naming convention: [object].[adjective].[verb].[adverb].via.[identifier].like.[identifier].[number to differentiate duplicate names]
1. Continous integration
### Fixings
1. Check licensing issues.
   1. https://www.gnu.org/licenses/gpl-faq.html#MereAggregation
   1. https://opensource.stackexchange.com/questions/6271/calling-gpld-programs-from-a-shell-script
   1. https://www.gnu.org/licenses/gpl-faq.html#GPLPlugins
   1. http://www.epiclaw.net/2012/01/31/combining-or-linking-proprietary-software-unmodified-code-licensed-under-gplv2
### Strategies
1. Actively support Linux users in order to advertise this and related projects.
1. Create community guidelines and contribution agreement.
1. Create/find text based ticket system.
1. Define break points of this project.
### Todos
1. Support automatic releases: https://github.com/release-it/release-it
   Setup development environment script in terminal:
   1. "package.install npm"
   1. Change directory in this reposiotry: cd <...>
   1. Install automatic release module in local directory: "npm install release-it"
   1. "release-it"
1. Creation of adhoc managed commands.
1. Command to show open ports.
1. [Create a manually curated changelog where the relevant changes are summarized.](https://github.com/olivierlacan/keep-a-changelog)
1. Support easy detection if a script should support host system, but does not do it 
   yet. An extra log message type in system-d seems to be suitable for this.
1. testing
   1. https://spin.atomicobject.com/2016/01/11/command-line-interface-testing-tools/
   1. https://github.com/debarshiray/toolbox
   1. https://github.com/debarshiray/libpod
1. dependencies backup concept
   1. Use git subtrees instead git submodules: https://www.atlassian.com/blog/git/alternatives-to-git-submodule-git-subtree
1. version planning via https://semver.org/ and https://github.com/semver/semver
1. Show template project that depends on this project.
1. Documents project folder structure.
1. Decide how to manage a high ration of comments to source code in repsective files?
1. bash.config script
   1. https://www.stefaanlippens.net/bashrc_and_others/
   1. symlink from these files to bash.config?
      1. .xinitrc
      1. .bashrc
      1. .bash_profile
      1. .xsessionrc
      1. .profile
1. code guidelines
   1. naming conventions
   1. Unit tests
1. folder structure
   1. for executables: https://unix.stackexchange.com/questions/36871/where-should-a-local-executable-be-placed
1. Scripts should print commands that are executed by default.
1. support for different linux distributions, operation systems, package managers and configuration managers
   1. file structure
   1. Linux
      1. Fedora
         1. https://fedoramagazine.org/what-is-silverblue/
      1. Ubuntu
      1. https://github.com/Linuxbrew/brew (install script: https://gist.github.com/kurobeats/670841aa2aa6de0711675ec8ff1d346f)
         1. https://blog.eduardovalle.com/2015/10/15/installing-software-without-root/
   1. Ansible
   1. Windows
      1. chocolate
   1. Mac
      1. homebrew
1. support for different package managers
   1. For programs installted by flatpack, an appropriate start command should be created automatically.
   1. Support Nix package manager: https://nixos.org/releases/nix/nix-1.8/manual/
   1. Support configurable package manager priority. (Command priorization is a general missing feature.)
   1. Back-Ends chapter in https://en.wikipedia.org/wiki/PackageKit list some other package managers.
1. tag based installation
   1. tags like https://www.heise.de/ix/heft/15-nach-12-4117289.html?artikelseite=&view= and other distros
1. standby and suspend
   1. Are there problems?
1. hibernate:
   1. complete shutdown but machine state saved on disk
   1. systemctl hibernate
   1. Enable hibernate for encrypted Fedora.
   1. Look for general problems with hibernate.
1. Preboot Execution Environment (PXE)
1. Compensate Linux problems:
   1. https://itvision.altervista.org/why.linux.is.not.ready.for.the.desktop.current.html
1. aggregation and linkage of documentation
1. http://tldp.org
1. List of distributions and distribution linkers:
   1. https://www.fsfla.org/ikiwiki/selibre/linux-libre/freed-ora.en.html
1. Filesystem/harddrive checking. See fsck.
1. Manage dotfiles and other dotfiles repos
   1. example: https://github.com/ajmalsiddiqui/dotfiles
   1. example: https://github.com/mathiasbynens/dotfiles
1. Configurable bin folder location.
# Ideas
1. GUI
1. text to speech interface
# Open Source Support
1. https://fedoramagazine.org/check-out-the-new-askfedora/

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
