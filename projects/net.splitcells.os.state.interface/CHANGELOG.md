# Changelog
This Changelog is inspired by [keepachangelog.com](https://keepachangelog.com/en/1.0.0/).
Version numbers are based on [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
Please, create a one sentence summary for each release.

The concrete echo format of each command is not stable.
Changes to this are not considered major changes.
## Unreleased
### Major Changes
1. Disable debug logging by default.
   Add environment variable 'log_level'.
   When set to 'debug', debug log is enabled.
1. 'repo.verify' now also checks if sub folders, are really instances of a repository.
1. Create 'repo.repair' command, that downloads missing sub repositories.
1. Remove 'user.data.synchronize' as the same can be done via 'network.configure'.
### Patch
1. Command "echo.error" previously did only echo the message "error" and ignored the argument.
   This was fixed.
## Version 4.0.1
Release project in order to test release process.
### Major Changes
1. Remove "user.programs.close.gracefully", because it was defined incorrectly
   and also was not used.
### Minor Changes
1. Add support for manpages.
1. Create command 'system.configure.auto' in order provide configuration
   that fit most users.
1. Create command 'sh.is.interactive' in order to detect,
   if the current shell is interactive.
1. Verify folder structure before repository content.
   Move folder check functionality of 'repo.verify' to 'repo.verify.folder.structure'.
   'repo.verify' still calls 'repo.verify.folder.structure'.
   'repo.verify' now first verifies the folder structure of the complete repository recursively.
   This speeds up the checking process, if a folder is missing.
1. Exit 'repo.process' on first error by default.
## Version 4.0.0
Starting with this version, major changes,
that are not backwards compatible,
will only be done,
in order to fix errors as long as it is justifiable.
### Major Changes
1. Remove all logging levels in order to greatly simplify code.
1. Rename "ssh.key.generate" to "user.ssh.key.generate" in order to better
   represent meaning of program.
1. Add new secondary objectives:
   1. "Do not change the system or the user without being explicitly told by the user to do it."
   1. "Minimize the effects on the state of the system by this framework."
1. "command.mananged.environment.configure": Ensure that bash configuration is always active in graphical shell only.
1. Migrate to new repository layout.
1. Create command "repo.verify".
1. "echo.error" now just echos to stderr.
   Other commands do not filter echoed errors correctly anymore.
### Minor Changes
1. Setup contribution guide lines.
1. "this.active.if.presence.of": Create command in order to test, if environment has required command.
1. Document some interesting third party shell software.
1. Create command "run.and.show.on.success".
1. Setup CI for Github.
1. Migrate to Github as main repository while still supporting Gitlab.
1. Support command to enable framework in non interactive shells: '. ~/bin/net.splitcells.os.state.interface.commands.managed/command.managed.export.bin'
1. "system.info": Create command to show system info overview.
1. "system.shutdown" & "user.programs.close.gracefully": Create commands to shutdown system.
1. "user.trash.empty": Create command to empty trash.
1. "user.messages.add": Create command to send message to user.
1. "system.managed.automatically.by.user.assert": Create command to configure additional default configurations.
1. "user.weekly": Create command for weekly execution with additional task that is disabled
   by default.
### Patches
1. Improving debug logging during first installation.
1. "command.managed.execute": Clear current line after execution end.
1. "run.and.show.if.failed": Omit duplicate lines in output in error case.
1. "command.repositories.install": Install commands in alphabetic order.
1. Add some tests.
1. Improve performance of "run.and.show.if.failed".
1. "run.and.show.if.failed" echo command output, if command exited with error.
1. "package.install": fix output for software specific installation programs.
1. "repo.remote.set": correct command line argument order.
1. Solve performance problems of "run.and.show.on.success" and "run.and.show.if.failed".
## Version 3.0.0
### Major Changes
1. Remove variables inside profile.
   Use commands in order to get configuration values instead.
   Repalced variables and their new commands:
   
   current_echo_level -> echo.level.config
   
   critical_echo_level -> echo.level.critical
   
   error_echo_level -> echo.level.error
   
   warning_echo_level -> echo.level.warning
   
   info_echo_level -> echo.level.info
   
   debug_echo_level -> echo.level.debug
   
   tracing_echo_level -> echo.level.trace
   
1. Use only word 'shell' for terminal.
   Replaced commands:
   
   terminal width -> shell.width
   
   terminal.time.between.lines.in.text.config -> shell.time.between.lines.in.text.config
1. "package.install":
   1. Use program specific installer first,
      in order to install program even if starter
      script is already present.
   1. End package install package specific script ends succesfully.
### Minor Changes
1. Place all project programs in bin folder and make project make compatible.
1. Define optional default user specific repository.
1. Log at debug level the arguments of "run.and.show.if.failed".
1. Some documentation updates.
1. "echo.line.current.set": Create warning when echo filtering does not work.
1. Create "user.management.auto.config" option.
1. Create test for "repo.clone.into.current.test".
1. Create "repo.create" command.
1. Update "package.install" documentation according to usage experience.
1. Update "user.configure" documentation.
1. Define CI pipeline for gitlab.
1. Create test suite.
### Patches
1. "shell.width" supports non interactive shells.
1. "run.and.show.if.failed": Clear current echo line after command is executed.
1. Clear current output line after a managed command was executed.
1. Command "echo.filtered"
   1. Prevent argument interpretation in "echo.filtered".
   1. Prevent line wrapping, if the filtered echo of a command is longer than the terminal width.
      This is done by limiting the output of filtered echos to the width of the terminal.
      In other words: if there is nothing to be reported during the execution of the command, no output is visible after the commands execution.
1. Documentation improvements.
1. Provide fall back value for "shell.width", if it could not be determined.
1. Make "shell.width" more portable.
1. Correctly visit child repos in "repo.process" by removing duplicate "cd $subrepo".
1. Unify quoting of command names in logging.
1. Create error, if there is no implementation of a managed command.
1. Support "shell.width" in non interactive shell.
## Version 2.0.0
## Major Changes
1. Set main remote during synchronization with foreign remote.
1. Use MPL 2.0 for every program. Some programs with other licenses were moved to "net.splitcells.os.state.interface.lib".
1. Correct command names:
   1. Rename "exit.on.error" to "sh.session.exit.on.error".
   1. Rename "exit.with.error" to "sh.session.exit.with.error".
### Minor Changes
1. Support same features for processing of local and remote repositories.
1. Create "repo.mirror.into.current".
   This can be used in order to make mirrors of complex mirrors.
1. Support complex repositories for "repo.remote.set".
### Patches
1. Clean up last line in echo before exit of managed command.
1. Do only print argument literally for echo commands.
1. Prevent command "repo.synchronize" from doing the same multiple times.
## Version 1.7.1
Fix issues.
### Patches
1. Adapt to console width in "echo.line.current.set".
   This prevents the line reset to print too much or too less.
1. Make "echo.deprecated.command" compatible with "echo.filtered".
1. Fix bugs in "repo.process" found by usage.
   1. Prevent folders being processed twice.
   1. Only use the echo commands of this project.
   1. Ignore hidden folders if the remote is not ssh.
      Before that, this was only done for remotes accessed via remotes.
1. Fix duplicate writings of console configuration settings.
## Version 1.7.0
Fix issues found by usage.
### Major Changes
1. Rename command "echo.tracing" to "echo.trace".
1. Remove echo filtering from "command.managed.execute".
1. Abort repository processing on first error.
### Minor Changes
1. Update internal documentation of some commands.
### Patches
1. Correct parameter propagation in "repo.pull.from", "repo.push.to" and "repo.synchronize.with".
1. Correct parameter propagation of "repo.pull.from".
1. Correct parameter propagation in "repo.pull.from" and "repo.push.to".
1. Correct stderr piping in "run.and.show.if.failed".
1. Clear current line content with empty space in "echo.line.current.set".
   Previously the command just reset the cursor position.
   If this command was called twice with a longer string argument in the first call 
   compared to the second call, the part of the first call would be still visible.
   
   i.e.
   
   echo.line.current.set "Marvin has a pen."
   
   echo.line.current.set "Alice"
   
   Would result into:
   "Alice has a pen."
1. Make release configuration public in order to simplify release process on different computers.
## Version 1.6.1
### Patches
1. HOTFIX: Fix bug in logging of "command.managed.execute".
   The bug caused the echo not being shown after execution and ending the command with an error.
## Version 1.6.0
### Minor Changes
1. Create "read.text" command that reads multiple line strings from stdin.
### Patches
1. Filter output of release process.
1. Force quoting command scripts.
1. Prevent wait time trough echo filtering by default.
## Version 1.5.0
### Major Changes
1. Filter 
## Version 1.5.0
### Major Changes
1. Create release script.
## Version 1.4.0
### Minor Changes
1. Introduce new configuration guide line.
1. Filtered echo is now visible on the last line in the console.
   It is deleted by the next echo.
   This way one can follow the execution progress without filling output with unimportant information.
## Version 1.3.0
### Major Changes
1. Echo filtering for managed command execution.
1. Remove command "run.and.show.only.errors".
   This command is not used.
1. Add command "run.and.show.if.failed".
### Minor Changes
1. Bugfixes
## Version 1.2.1-1.2.7
1. Testing release with
## Version 1.2.0
Establish release process.
### Minor Changes
1. Use echo levels for every command.
### Patches
1. Prevent duplicate variable export in ".profile" after multiple installations.
## Version 1.1.0
Some changes are not listed as CHANGLOG was created after them.
### Major Changes
1. Rename command "user.task.daily" to "user.task".
1. Rename command "repo.commit" to "repo.commit.all".
1. Rename command "system.adaptations.update" to "system.update".
### Minor Changes
Introduce echo levels.
## Version 1.0.1
Fix issues found during first installation on other Fedora installation.
## Version 1.0.0
First usable version created.
