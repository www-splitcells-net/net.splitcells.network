## Tasks
Tasks at the top have generally a higher priority than task at the button.
This does not mean, that tasks are processed according to the priority order.
The priority of a task is just a guideline.
### Services
1. Update project documentation.
1. Document commands.
1. Improve tests.
1. Improve echo output and make it nice, for a certain setting.
### Current Tasks
1. Implement testing:
   1. Fix Gitlab-Runner Test and ensure that test failures are automatically found,
      which is currently not the case.
   1. Use [toolbox](https://github.com/containers/toolbox).
      1. https://docs.fedoraproject.org/en-US/fedora-silverblue/toolbox/
      1. https://fedoramagazine.org/a-quick-introduction-to-toolbox-on-fedora/
2. Use sh instead of bash via the shebang "#!/usr/bin/env sh" in order to avoid GPL dependencies.
3. Document reasoning for command line based dependency injection,
   in light of the fact, that there are stability issues with that.
   Also, document how to avoid stability issues.
4. Move command documentation into the commands itself.
5. Generate admin script instead of just executing such,
   in order to make the software more stable.
### Todos
1. Do not use recursion via shell in repo commands.
   Use recursion via Python functions.
2. Clean up python implementations of repo commands.
3. Create integration into user home portability projects.
4. Create ssh deployment machanism via other frameworks like ansible.
5. Create display brightness control commands.
6. Create sound control command.
7. Create shell for blind usage.
   1. Things need to be considered:
      1. Way to speak currently entered command without executing it.
8. Improve testing on local machine.
   1. https://spin.atomicobject.com/2016/01/11/command-line-interface-testing-tools/
   1. https://github.com/debarshiray/toolbox
   1. https://github.com/debarshiray/libpod
9. Filesystem/harddrive checking. See fsck.
10. Support alternative repo managers.
11. Support script mode by default, where a script is generated,
    instead of executing commands directly.
12. Integrate project commands into repo commands:
   Project repo commands may be relevant to repo commands.
   At the very least one should inform one about the existence of related repo
   commands during execution,
   because it may be important for the user.
   Note that one cannot execute the corresponding repo command automatically,
   because this may not always be possible
   (i.e. `repo.push` without an internet connection may not be possible).

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
