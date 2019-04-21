# Operation System State Interface
1. This project is still in experimental state.
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
## Challange
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
## Solution
__TODO__
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
