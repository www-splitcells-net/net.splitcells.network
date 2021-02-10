# Code Guidelines
Prefer aborting an command on first error.
## Backward Compatibility
Starting with version 4.0, major changes,
that are not backwards compatible,
will only be done,
in order to fix errors as long as it is justifiable.
This does only apply to documented or tested parts of the API.
## Programming Language
1. Use Thompson Shell(sh) for simple scripts that just execute
   and wire together commands.
   
   Use Python 3 for programs with any complexity or processing.
   Shells are not suited for programming.
   
   Shells are suited for interactions, simple configurations and simple mappings.
   
   Programs that are conceptually system and program independent and complex logic
   should not be written in shell,
   because shell has a poor syntax for that and often requires external commands
   for such things, that may not be present on the target system.
## Configuration
   1. Configuration values should be accessed via commands and not via i.e. variables.
     This way Programs can rely on the configuration values without being set.
     In this case the configuration commands provide a default value.
     This also minimizes configuration tasks needed to be done during the setup.
   1. Configuration commands start with "config.".
## Echoing/Logging
   1. There is a difference between echoing/logging and debugging
      Debugging should be done by command specific methods.
      (i.e. Using "set -x" for bash scripts or passing output verbosity options to programs).
   1. There are only the echo/log levels stdout and stderr.
   1. If everything works and the task is completed, nothing should be written to stderr.
   1. "Use echo.error" for following things:
   	1. Error messages describing that command did not work.
   	1. Messages describing why the command could not execute its task.
   	1. Things the user needs to do in order to complete the executed commands task.
   	   Like restarting the computer after an update.
   1. Do not do more advanced output filtering.
      The caller can do this.
      This simplifies and unifies logging across all programs.
   1. Logging/echoing should not have a large performance impact.
   1. Log format:
      1. Every log message should be only one line and not multiple.
      1. It has to start with the log message followed by ": " and the parameters of the log message
         (i.e. "Could not install package: git"). 
         This makes it easier to parse log messages via tools
         because a simple string based prefix search on lines shows all messages of a certain type.
1. Each implemented feature should be used by someone.
   Otherwise the feature should be deprecated.
1. Document on which system the code was tested.
# External Code Guidelines
1. [Advanced Bash-Scripting Guide](http://www.ing.iac.es/~docs/external/bash/abs-guide/)
   TODO Check if this guide is suitable.