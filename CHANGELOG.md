# Changelog
This Changelog is inspired by [keepachangelog.com]()https://keepachangelog.com/en/1.0.0/).
## Unreleased
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
## v1.6.1
### Patches
1. HOTFIX: Fix bug in logging of "command.managed.execute".
   The bug caused the echo not being shown after execution and ending the command with an error.
## v1.6.0
### Minor Changes
1. Create "read.text" command that reads multiple line strings from stdin.
### Patches
1. Filter output of release process.
1. Force quoting command scripts.
1. Prevent wait time trough echo filtering by default.
## v1.5.0
### Major Changes
1. Filter 
## v1.5.0
### Major Changes
1. Create release script.
## v1.4.0
### Minor Changes
1. Introduce new configuration guide line.
1. Filtered echo is now visible on the last line in the console.
   It is deleted by the next echo.
   This way one can follow the execution progress without filling output with unimportant information.
## v1.3.0
### Major Changes
1. Echo filtering for managed command execution.
1. Remove command "run.and.show.only.errors".
   This command is not used.
1. Add command "run.and.show.if.failed".
### Minor Changes
1. Bugfixes
## v1.2.1-1.2.7
1. Testing release with
## v1.2.0
Establish release process.
### Minor Changes
1. Use echo levels for every command.
### Patches
1. Prevent duplicate variable export in ".profile" after multiple installations.
## v1.1.0
Some changes are not listed as CHANGLOG was created after them.
### Major Changes
1. Rename command "user.task.daily" to "user.task".
1. Rename command "repo.commit" to "repo.commit.all".
1. Rename command "system.adaptations.update" to "system.update".
### Minor Changes
Introduce echo levels.
## v1.0.1
Fix issues found during first installation on other Fedora installation.
## v1.0.0
First usable version created.
