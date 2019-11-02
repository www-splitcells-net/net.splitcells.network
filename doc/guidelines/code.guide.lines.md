# Code Guidelines
## Configuration
   1. Configuration values should be accessed via commands and not via i.e. variables.
     This way Programs can rely on the configuration values without being set.
     In this case the configuration commands provide a default value.
     This also minimizes configuration tasks needed to be done during the setup.
   1. Configuration commands start with "config.".
## Logging
   1. If everything works nothing should be written to stderr.
   1. If optional feature do not work, only warning should be created via "echo.warning".
1. Each implemented feature should be used by someone.
   Otherwise the feature should be deprecated.
1. Document on which system the code was tested.
# External Code Guidelines
1. [Advanced Bash-Scripting Guide](http://www.ing.iac.es/~docs/external/bash/abs-guide/)
   TODO Check if this guide is suitable.