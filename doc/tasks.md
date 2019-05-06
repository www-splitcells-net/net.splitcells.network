## Tasks
1. Move tasks to other projects if suitable.
1. [Publish to Maven Central.](https://www.eviltester.com/2016/10/how-to-create-and-release-jar-to-maven.html)
### Fixings
1. Create install script.
1. Manage modules.
1. Check licensing issues.
   1. https://www.gnu.org/licenses/gpl-faq.html#MereAggregation
   1. https://opensource.stackexchange.com/questions/6271/calling-gpld-programs-from-a-shell-script
   1. https://www.gnu.org/licenses/gpl-faq.html#GPLPlugins
   1. http://www.epiclaw.net/2012/01/31/combining-or-linking-proprietary-software-unmodified-code-licensed-under-gplv2
1. Create graph for user activities.
### Strategies
1. Actively support Linux users in order to advertise this and related projects.
1. Create community guidelines and contribution agreement.
1. Create/find text based ticket system.
1. Define break points.
### Todos
1. Support easy detection if a script should support host system, but does not do it 
   yet. An extra log message type in system-d seems to be suitable for this.
1. dependencies backup concept
   1. Use git subtrees instead git submodules: https://www.atlassian.com/blog/git/alternatives-to-git-submodule-git-subtree
1. version planning via https://semver.org/ and https://github.com/semver/semver
1. Git guide lines
   1. gitignore templates: https://github.com/dvcs/gitignore/
      1. Eclipse
      1. Intellij Idea
1. Template project that depends on this project.
1. Maven release
1. Prevent the mixing of incompatible package managers via the default system package scripts.
   1. compatibility determination
   1. Use tags for package managers in order to determine compatibility.
1. project folder structure
   1. Third parties should find required things by a decision tree or a simple discovery process.
1. Source code files should use their respective file extensions, in order to increase editor compatibility.
   In this case the source code files should never be referenced directly by programs.
   1. Bash and CMD/Powershell work different in this regard. Require bash?
1. Decide how to manage a high ration of comments to source code in repsective files?
1. bash.config script
   1. https://www.stefaanlippens.net/bashrc_and_others/
   1. symlink from these files to bash.config?
      1. .xinitrc
      1. .bashrc
      1. .bash_profile
      1. .xsessionrc
      1. .profile
1. Task management
   1. big task count management
   1. Tasks are marked with the "TODO".
   1. These are located where they are needed.
1. Package manager situation
   1. advantages and problems of pkcon (https://www.mankier.com/1/pkcon)
      1. update
      1. eclipse package
   1. pacapt: https://github.com/icy/pacapt/blob/ng/lib/help.txt
1. os administration guidelines
   1. preserve folder structure
1. code guidelines
   1. Each implemented features should be used by someone.
   1. Assertion programs should be executed in current bash, in order to allow program abort.
   1. external bash and python guidelines
   1. very basic architecture as long as viable
   1. declarative code with active verb naming convention
   1. Document on what system a script was used and the results of that usage.
   1. When creating interfaces make them general if the implementation
      is not general and no further implementations are expected soon.
      This is done in order to ease a possible later more generalized implementation.
   1. Inside the program the intended dependencies on the environment should be documented as abstract as
      possible (i.e. script for Linux on PackageKit).
   1. Unit tests
1. folder structure
   1. for executables: https://unix.stackexchange.com/questions/36871/where-should-a-local-executable-be-placed
1. Avoid bash for scripts for complex tasks and use python 3 instead.
   1. high performance via static compilation
      1. https://stackoverflow.com/questions/39913847/is-there-a-way-to-compile-python-application-into-static-binary
   1. Scripts should print commands that are executed by default.
   1. object oriented and fluent interface
1. configuration
1. collection, linking and integration to existing scripts
1. support for different linux distributions, operation systems, package managers and configuration managers
   1. file structure
   1. Linux
      1. Fedora
      1. Ubuntu
   1. Ansible
   1. Windows
      1. chocolate
   1. Mac
      1. homebrew
1. support for different packaging systems
   1. Install flatpack applications via system.install.
      1. Use flatpack applications instead of native system package manager in order to minimize side effects.
      1. Install as user and not as root in order to minimize side effects.
      1. Support start of flatpack application via command that would be provided for this application if it was installed by native package manager.
   1. snap
   1. Support custom packaing system priorities.
1. tag based installation
   1. managing and mixing personal configurations
   1. tags like https://www.heise.de/ix/heft/15-nach-12-4117289.html?artikelseite=&view= and other distros
1. shortcuts
   1. maven
   1. git
1. Detect OS or presence of required command and determine if script is supported.
1. unit tests
1. related to Git
   1. git difference between 2 commits in current workspace
   1. git gc --aggressive --prune=now
1. https://stackoverflow.com/questions/11287861/how-to-check-if-a-file-contains-a-specific-string-using-bash
1. standby and suspend
   1. Are there problems?
1. hibernate:
   1. complete shutdown but machine state saved on disk
   1. systemctl hibernate
   1. Enable hibernate for encrypted Fedora.
   1. Look for general problems with hibernate.
1. Preboot Execution Environment (PXE)
1. user package managers
   1. https://github.com/Linuxbrew/brew (install script: https://gist.github.com/kurobeats/670841aa2aa6de0711675ec8ff1d346f)
1. Compensate Linux problems:
   1. https://itvision.altervista.org/why.linux.is.not.ready.for.the.desktop.current.html
1. aggregation and linkage of documentation
1. Log all executed commands that do not call scripts of this project inside systemd log.
1. https://github.com/nvie/gitflow
1. Cache all update packages in order to allow offline installation of same system.
1. [Testing](https://leelevett.wordpress.com/2015/07/21/bash-script-project-with-tests-and-maven/)
1. http://tldp.org
1. List of distributions and distribution linkers:
   1. https://www.fsfla.org/ikiwiki/selibre/linux-libre/freed-ora.en.html
1. https://github.com/Linuxbrew/brew
   1. https://blog.eduardovalle.com/2015/10/15/installing-software-without-root/
# Ideas
1. GUI
1. text to speech interface