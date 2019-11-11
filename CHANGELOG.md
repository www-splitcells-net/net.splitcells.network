# Changelog
This Changelog is inspired by [keepachangelog.com](https://keepachangelog.com/en/1.0.0/).
Version numbers are based on [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
Please, create a one sentence summary for each release.
## Unreleased
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
