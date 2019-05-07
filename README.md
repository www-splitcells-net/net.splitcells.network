# Operation System State Interface
1. This project is still in an experimental state.
1. Note that there is no guaranty that the scripts are correct or that they do no harm.

## Manifesto
We want to install programs.

It does not interest us, whether the program is installed by PackageKit, dnf, apt, Flatpak, snap, a Windows installer, a bash script or something else.

We want to block network traffic of the system.

We do not want to activate the panic mode of Firewalld.

We want to declare our goals.

We do not want to have to provide secondary technical information beforehand without knowing where to look.

We want a common dictionary for common tasks.

We avoid remembering implementation specific commands for common tasks.
## Challenge
When administrating systems one has to consider many things.
Some of these may be even unknown by the administrator because no problems were occurring during the task.

Let's take the update mechanism for Fedora as an example.
An enormous number of things need to be respected (This is no assessment of the complexity of the update process on other systems or Linux distributions.).
[Did you know that the package manager dnf does not support updates while being logged in the desktop?](https://lwn.net/Articles/702629/)
The funny thing about this is, that most of the time an update via console while being logged in the desktop works.
Until it does not.
Also, there are no warnings printed by dnf onto the console regarding this.
Additionally, an update via dnf on Fedora might not update all software components like pip, pip3, Flatpak applications, Snaps, apps on AppImage, Docker images etc.
Also different Systems may have different functionality.

This problem affects other systems like Windows and Mac OS as well.
## Solutions

### Language
In many cases the user does know, what he wants.
In other cases the user can find out his needs by answering questions.
This projects provides a dictionary in order to be able to articulate precisely his needs on a high level.
Secondly the words are organized in a structure in order to easily find the right commands to be executed without looking at documentation i.e.:
ssh.key.generate ~ Generate and store ssh keys if not already present for the current user. Also sets appropriate access rights for these keys. 

### Terminal based Dependency Injection
On different systems different programs need to be executed in order to achieve the same thing.
In most cases the user does not have any interest about this detail, but he still has to use these programs.
This framework provides simple terminal commands that execute the required code depending on the current system.

The programs called by this framework can be developed independently or used on its own as long as some rules are meet.
Also the programs are mostly not provided by this framework, but are copied into an existing installation of this framework.

In other words the detailed technical know-how of managing operation systems is not provided by this framework.

## Project Structure
1. [Objectives of this Project](./doc/objectives.md)
1. [Why are things done a certain way?](./doc/reasoning.md)
## Guide Lines
1. [Intended Development Workflow Using this Framework](doc/development.workflow.md)
1. [Dictionary](doc/guide.lines/dictionary.md)
1. [File Extension](doc/guide.lines/file.extensions.md)
1. __TODO__ Define publication methods.
1. [Licensing](doc/licensing.md)
## __TODO__ Contributing
   1. __TODO__ Contributor License Agreement
      1. https://opensource.stackexchange.com/questions/668/are-there-reusable-clas
      1. http://www.apache.org/licenses/#clas
## Tasks
Some open tasks can be found [here](doc/tasks.md).

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
