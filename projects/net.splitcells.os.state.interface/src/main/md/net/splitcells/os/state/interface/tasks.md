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
1. Use sh instead of bash via the shebang "#!/usr/bin/env sh" in order to avoid GPL dependencies.
2. Document reasoning for command line based dependency injection,
   in light of the fact, that there are stability issues with that.
   Also, document how to avoid stability issues.
3. Move command documentation into the commands itself.
4. Generate admin script instead of just executing such,
   in order to make the software more stable.
### Todos
1. Implement interface to shell via speech to text and the other way around
   for output.
3. Do not use recursion via shell in repo commands.
   Use recursion via Python functions.
4. Clean up python implementations of repo commands.
5. Create integration into user home portability projects.
6. Create ssh deployment machanism via other frameworks like ansible.
7. Create display brightness control commands.
8. Create sound control command.
9. Create shell for blind usage.
   1. Things need to be considered:
      1. Way to speak currently entered command without executing it.
10. Improve testing on local machine.
    1. https://spin.atomicobject.com/2016/01/11/command-line-interface-testing-tools/
    1. https://github.com/debarshiray/toolbox
    1. https://github.com/debarshiray/libpod
11. Filesystem/harddrive checking. See fsck.
12. Support alternative repo managers.
13. Support script mode by default, where a script is generated,
    instead of executing commands directly.
14. Integrate project commands into repo commands:
   Project repo commands may be relevant to repo commands.
   At the very least one should inform one about the existence of related repo
   commands during execution,
   because it may be important for the user.
   Note that one cannot execute the corresponding repo command automatically,
   because this may not always be possible
   (i.e. `repo.push` without an internet connection may not be possible).

As long as not otherwise noted,
this text is licensed under the EPL-2.0 OR MIT (SPDX-License-Identifier).
