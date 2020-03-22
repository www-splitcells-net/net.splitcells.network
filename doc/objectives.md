# Objectives of this Project
## Primary Objectives
This project provides a framework in order to support the following main goals:
1. Support operation systems that are compatible with the Thompson Shell (sh).
   The current focus is on Linux distributions, but other operation systems are welcome.
1. Let system user easily find and execute OS related functions.
   For simplicity and automation purposes this project focuses on execution via console.
1. Help the user to deal with technical details.
1. Minimize the number of required dependencies.
   The number of optional dependencies is irrelevant and should be provided by other projects.
1. Provide a dependency management mechanism in order to support the tasks on different systems.
   This is done via dependency injection on the command line level.
1. Provide a workflow and dictionary in order make its usage and development simple.
## Secondary Objectives
This project provides a framework in order to also support the following secondary goals:
1. Provide reasonable defaults for complex tasks as most users just want to use the system.
1. Make the administration process transparent by logging each action done.
   Logging all executed commands to the console is a way to start this.
1. Let the user actively agree to changes that might cause problems.
   The user should thereby informed of possible problems that might occur.
1. Do not change the system or the user without being explicitly tolde by the user to do it.
## Non Objectives
1. This framework only provides a minimal a framework for system administration.
   System specific programs are provided by other projects.

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
