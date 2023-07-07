# Dependency Manager (Dem)
The project's history can be viewed [here](src/main/xml/net/splitcells/dem/history.xml).
## Objectives
* This framework standardizes Java based source code.
* The resulting code should maximize support for automatic refactoring like method renaming or moving.
* Provide an interface for programs, that maximizes the replace-ability of interface implementations
  (i.e. dependency injection) and
  supports the injection of functionality into existing code (i.e. aspect oriented programming).
* Enable one to write and maintain long lasting code with reasonable efforts in the context of ever
  changing language features and dependencies.
  * Therefore, make the resulting core functionality portable, adaptable and compatible as reasonable possible.
    Furthermore, minimizing the complexity of the core functionality is helpful as well. 
  * Define and enforce a subset of Java for core functionality in order to achieve this goal.
  * Provide access to all dependencies via static methods and static final flags,
    which work like header files in C source code.
  * Work with only one global state variable, that represents the complete environment of the current logical process.
    It can be thought of as a variable,
    that is implicitly passed through every method call.
    Multiple global state variables are not allowed.
  * Provide an easy workflow to setup an environment,
    execute a program and to destroy the environment after the execution.
* Contain side effects and provide an interface in order to manage side effects.
* Organize all things in a tree like structure,
  in order to support information management.