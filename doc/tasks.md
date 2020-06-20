## Tasks
Tasks at the top have generally a higher priority than task at the button.
This does not mean, that tasks are processed according to the priority order.
The priority of a task is just a guideline.
### Current Tasks
1. Manage power settings: improved performance and increased battery life
1. Automatic "system.update" via "user.weekly" if "system.config.management.automatic" is set to true.
   Default for "system.management.automatic" is false. 
1. Visualize concept hierarchy on a website.
1. Fix description created by automatic releases.
1. Document commands.
1. Create general open source project template.
   This is not part of this project.
   It is part of 'splitcells.net'.
1. Implement tests:
   Tests are implemented as normal commands.
   They are executed in a sandbox.
   1. Test injection in order to test implementations of managed commands.
      1. Format for test command name: all commands ending with ".test".
      1. Create command which executes all tests.
      1. Find or create a test framework. 
   1. Test without additional rights.
   1. Test with sudo rights.
   1. Fix Gitlab Test and ensure that test failures are automatically found,
      which is currently not the case.
   1. Use [toolbox](https://github.com/containers/toolbox).
      1. https://docs.fedoraproject.org/en-US/fedora-silverblue/toolbox/
      1. https://fedoramagazine.org/a-quick-introduction-to-toolbox-on-fedora/
1. Move tasks to other projects if suitable.
1. Use sh instead of bash via the shebang "#!/usr/bin/env sh" in order to avoid GPL dependencies.
1. Test Gitlab security settings.
1. Continous integration
1. Ticket managment inside git repository:
   1. https://github.com/augmentable-dev/tickgit
### Things needed to be fixed.
### Strategies needed to be defined.
1. General advertisment.
1. Actively support Linux users in order to advertise this and related projects.
1. Create community guidelines and contribution agreement.
1. Create/find text based ticket system.
1. Define break points of this project.
1. Fork Wars
### Todos
1. Creeate command to synchronize all repositories.
1. Document, why bashrc is used.
1. Dependency injection based configuration system.
1. Create installation script for Windows.
   1. Installation of Git Bash.
   1. Installation of Choco.
1. Create shell for blind usage.
   1. Things need to be considered:
      1. Way to speak currently entered command without executing it.
1. Support managed commands with no implementation like "user.configure".
1. Create a dictionary for bash in order to be able to quickly lookup functionality.
   1. Revert Search
1. Define and document document "repo.*" command arguments.
   1. Remotes paths should always contain of 3 (protocol, host, file path) elements as different systems have slightly 
      different syntax
   1. No subrepo variable in path.
1. Define and use documentation format.
   Maybe it should be compatible to man.
1. Create commands for fast path navigation via console.
   1. Select command output's i-th line and use it for other command.
1. file.partition.manager
   1. gnome-disks
   1. gparted
1. Unify configuration system.
   "shell.time.between.lines.in.text.config" and the echo variables use different style of configuration management.
1. Create configuration command template like "command.managed.execute".
1. Do not exit repo.synchhronize etc. with error if remote repository is not available. Just bring a warning.
1. Create tutorials.
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
             1. https://fedoramagazine.org/pieces-of-fedora-silverblue/
         1. yum
         1. rpm
      1. Ubuntu
      1. https://github.com/Linuxbrew/brew (install script: https://gist.github.com/kurobeats/670841aa2aa6de0711675ec8ff1d346f)
         1. https://blog.eduardovalle.com/2015/10/15/installing-software-without-root/
      1. dpkg
      1. portage
      1. zypper
      1. pkgng
      1. cave
      1. pkg_tools
      1. sun_tools
      1. Guix (try their video tutorial)
      1. Nix
   1. Ansible
   1. Windows
      1. chocolate
      1. https://scoop.sh/
   1. Mac
      1. homebrew
      1. macports
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
1. Show current execution state by showing only last echo in terminal.
   Use the same technique that is used for terminal animations.
1. Solving name conflicts in PATH via something like containers/sandboxes.
1. Maybe the best would be, to just document this and to use existing solutions.
   In other words, the rights management is only done via the operation system.
   Alternative rights management for command execution aka. alternative to sudo:
   1. Not all system support sudo like commands out of the box (i.e. Windows).
      It may be possible to provide an sudo command for all operation systems.
   1. Sudo has no protection against an infiltrated user.
      Depending on the situation, this is a problem or not.
      1. Generally speaking it is not advised to give direct root access to any person except, 
         if there is 
         only one owner of the system.
         Also, root login from remote should be disabled by default.
         In this situation the system administrators get sudo rights and is only used 
         in order to configure and maintain the system.
         It is not used for other tasks.
         If an administrator is hacked than the system is hacked as well.
         Generally speaking this seems to be something one cannot defend against.
      1. There is a normal user that has sudo rights.
         This is often the case, if the user of the system is also its owner.
         (i.e. You buy a laptop and install your tools and do your job with it.)
         In this case it is enough if you of one the user programs hacks 
         the user. Either by being a malware or by being exploited by an attacker.
         
         There are now multiple ways to gain root access:
         1. The attacker can get the necassary access keys and can execute programs with root rights via sudo.
         1. The attacker can manipulate user scripts, that the user executes via sudo.
            If the user does not notice it, the attacker effectively gains root rights.
         
         There seem to be generally speaking 2 tactics in order to defend against this:
         1. Application isolation: this way the access keys of the user can be secured.
            Note that the GUI needs to support this feature.
            This currently seems to be done by [Qube OS](https://www.qubes-os.org/).
         1. Instead of executing commands via sudo, sudo only request the command execution.
            This new command would send the request to an administration user with the required rights.
            In order to execute the request the administration user has to be logged 
            into the account and explicitly allow its execution after a review.
            Both, the normal user and the administrator user, could be accessed by 
            the same person.
            This process ensures that the process command creation, review and execution 
            is separated.
            This mainly helps to notice unexpected commands being requested to be executed 
            by root.
            
            Note that this process needs to be thought through if implemented and does not help, if 
            the user do not do their job.
            I.e. such command request should never execute programs of the user that is requesting the execution,
            because this way, it is not possible or hard to detect malicious via a review.
1. Support system testing for Linux.
   1. Phoronix
1. Configurable installation folders:
   1. bin folder for installed programs.
   1. Config folder, for configuration and update procedures.
1. Make filtered echos more practical:
   1. Document: Filtered echo are printed in order to be able, to view the process.
   1. They should be not present in console after its commands execution has ended.
      Do not print more symbols during a filtered echo in order to prevent creating a new line.
   1. Support colors.
1. Create compatibility system for major changes.
   1. This task is not important as long there as long as legacy code is not present.
   1. Document the tactic regarding compatibility on "README.md".
1. Faster update process via increment updates.
1. Backup ~/bin/* folders during install via source control.
1. Document why such long command names are used.
1. Manage contributions, if project gets big.
   1. TODO Create commands the execute each task.
   1. TODO Add repo verification to optional CI process.
   1. Based on signed pull requests.
       1. Pull request has to be based on signed tag.
   1. Proof identities.
      1. Create script to create gpg key.
      1. Implement script to create valid pull requests.
      1. Implement script to check foreign pull requests.
      1. Check presence of signatures automatically for the foreign parent commit of the merge via script.
         Store public keys in this repositories, so that is trivial to check public keys to the signatures.
         Note that this does not ensure that the commits are correct, because an attacker could change the public keys in the repository as well.
         The public keys in this repository would therefore not be trustworthy.
         Their primary goal is to ensure that the contribution process is adhered.
         1. Trust a public key: https://yanhan.github.io/posts/2014-03-04-gpg-how-to-trust-imported-key.html
         1. Create script that checks the signatures based on the keystore of the local user.
            1. git tag | xargs git verify-tag
         1. Create script that checks the signatures based on the public keys in this repo in order to check consistency.
      1. Require upload of public keys via second channel.
         1. Create a script for the upload.
   1. Use and check https://developercertificate.org/.
      1. DCO is better than CLA if one is sure that the license does not have to be changed.
   1. Document this process so that other can reproduce it easily.
   1. Helpful literature:
       1. Git
          1. https://git-scm.com/book/en/v2/Git-Tools-Signing-Your-Work
          1. https://mikegerwitz.com/2012/05/a-git-horror-story-repository-integrity-with-signed-commits
       1. Linux Kernel
          1. https://www.kernel.org/doc/html/v4.17/maintainer/pull-requests.html
          1. https://www.kernel.org/doc/html/v4.17/process/submitting-patches.html
   1. Ideas:
      1. https://opensource.stackexchange.com/questions/668/are-there-reusable-clas
      1. http://www.apache.org/licenses/#clas
# Ideas
1. Consider requiring using the project specific bin folder to the PATH explicitly.
   Either by adding this step via console configuration or by executing a command.
   The second option is interesting, as this results in a system that does not need 
   any intrusive change to the user or system in order to use this project.
   In this case one would just need a folder with this project's programs set as executable.
1. GUI
1. text to speech interface
   1. Input commands via voice: https://github.com/osprey-voice/osprey
   1. Support Orca screen reader usage.
1. Random quotes in order to improve emotional state of user.
1. Support multiple repositories in parallel like Github.
   1. Use taskwarrior with import and export scripts for ticket management via markdown 
      in this repository.
   1. Use configuration in repository for taskwarrior. See "https://taskwarrior.org/docs/configuration.html".
   1. Use bugwarrior in order to import tickets from other repositories.
1. Compatibility layer for service managers like systemd.
1. Create repository backup.
   The idea is to have a backup plan/infrastructure for dependencies because the internet is an every changing world.
1. Support different installation architectures
   1. Scripts related to a topic or program are located in a a specific folder, where there are no other scripts.
      Thereby, multiple versions of the scripts can be supported in a system at once.
   1. Support installation as a package of a package manager.
1. Support partial installation of repository.
   In this case only scripts related to a topic or program are installed.
   This requires that the repository has a certain structure.
1. Text to speech: text navigation by sentence, natural subclauses and pattern matchers.
1. Alternative echo concept: just use stdout for general information and stderr for errors.
1. Create alternative autocomplete command, which shows possible commands in tree format.
# Open Source Support
1. Fedora
   1. https://fedoramagazine.org/check-out-the-new-askfedora/
   1. https://fedoramagazine.org/fedora-projects-for-hacktoberfest/

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
