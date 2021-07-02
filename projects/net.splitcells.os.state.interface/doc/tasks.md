## Tasks
Tasks at the top have generally a higher priority than task at the button.
This does not mean, that tasks are processed according to the priority order.
The priority of a task is just a guideline.
### Services
1. Document commands.
1. Add tests.
1. Improve echo output and make it nice, for a certain setting.
### Current Tasks
1. Implement testing:
   1. Fix Gitlab-Runner Test and ensure that test failures are automatically found,
      which is currently not the case.
   1. Use [toolbox](https://github.com/containers/toolbox).
      1. https://docs.fedoraproject.org/en-US/fedora-silverblue/toolbox/
      1. https://fedoramagazine.org/a-quick-introduction-to-toolbox-on-fedora/
1. Use sh instead of bash via the shebang "#!/usr/bin/env sh" in order to avoid GPL dependencies.
1. Support project specific repo commands.
1. Remove unecessary tasks.
### Things needed to be fixed.
### Strategies needed to be defined.
1. Define break points of this project.
1. Fork Wars
### Todos
1. Create integration into user home portability projects.
1. Create ssh deployment machanism via other frameworks like ansible.
1. Create display brightness control commands.
1. Create sound control command.
1. Create installation script for Windows.
   1. Installation of Git Bash.
   1. Installation of Choco.
1. Create shell for blind usage.
   1. Things need to be considered:
      1. Way to speak currently entered command without executing it.
1. Create a dictionary for bash in order to be able to quickly lookup functionality.
   1. Revert Search
1. Improve testing on local machine.
   1. https://spin.atomicobject.com/2016/01/11/command-line-interface-testing-tools/
   1. https://github.com/debarshiray/toolbox
   1. https://github.com/debarshiray/libpod
1. Documents project folder structure.
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
1. Filesystem/harddrive checking. See fsck.
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
1. Support alternative repo managers.

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
