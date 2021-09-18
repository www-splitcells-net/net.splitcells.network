# Objectives of this Project
## Primary Objectives
This project provides a framework in order to support the following main goals:
1. Provide a simple language in order to execute commands,
   where the concrete implementation for a given command may be provided by
   other projects.
2. Support operation systems that are compatible with the Thompson Shell (sh)
   and Python 3.
   The current focus is on Linux distributions, but other operation systems are welcome.
4. Let system user easily find and execute OS related functions.
   For simplicity and automation purposes this project focuses on execution via console.
5. Help the user to deal with technical details.
6. Minimize the number of required dependencies.
   The number of optional dependencies is irrelevant and should be provided by other projects.
7. Provide a dependency management mechanism in order to support the tasks on different systems.
   This is done via dependency injection on the command line level.
8. Provide a workflow and dictionary in order make its usage and development simple.
9. Trust the system by default.
   The OS knows how the system should be managed best by default.
   Only override this, if the user explicitly wants this.
## Secondary Objectives
This project provides a framework in order to also support the following secondary goals:
1. Provide reasonable defaults for complex tasks as most users just want to use the system.
1. Make the administration process transparent by logging each action done.
   Logging all executed commands to the console is a way to start this.
1. Let the user actively agree to changes that might cause problems.
   The user should thereby informed of possible problems that might occur.
1. Do not change the system or the user without being explicitly told by the user to do it.
1. Minimize the effects on the state of the system by this framework.
1. Make this framework as portable as possible.
## Non Objectives
1. This framework only provides a minimal framework for system administration.
   System specific programs are provided by other projects.
## Conclusions Based on Objectives
1. We try to be only dependent on the thompson shell (sh) and Python 3.
   Currently some scripts also use bash.
2. We support multiple CI systems (Github and Gitlab) in order to test different environments.
3. We do not provide a default command such as `system.configure.auto` in order
   to fix all perceived of an operation system.
   Anyone can provide such a command, but the management of such a command is
   currently out of scope.

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
