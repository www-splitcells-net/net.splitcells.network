# Code Guidelines
## Programming Language
1. Use Thompson Shell(sh) for simple scripts.
   Use Python 3 for programs with any complexity.
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
1. Each implemented feature should be used by someone.
   Otherwise the feature should be deprecated.
1. Document on which system the code was tested.
# External Code Guidelines
1. [Advanced Bash-Scripting Guide](http://www.ing.iac.es/~docs/external/bash/abs-guide/)
   TODO Check if this guide is suitable.