# Introduction

## System Administration Challenges
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

# Leaky Abstractions aka. Terminal based Dependency Injection
(I kind of think, that I'm using the false term for this.)
On different systems different programs need to be executed in order to achieve the same thing on an abstract level.
For instance, the user might want to install a new program or just check if everything is fine.

In most cases the user/administrator does not have any interest about the details,
but he still has to use these programs.
Of course this is not always the case.
When something does not work as expected or the shit starts really to hit the fan,
than user might have an interest to look into the technical details instead of just buying a new system.
But for the most part, the system provides a function to the user,
and the system itself is of lesser interest.

In order for the use to ready conceptually simple goals,
this framework provides simple terminal commands,
that execute the required code depending on the current system.

The code called by this framework can be developed independently or used on its own as long as some simple rules are meet.

Keep in mind, that the system specific code is mostly not provided by this framework,
but is copied into an existing installation of this framework.
In other words the detailed technical know-how of managing operation systems is mostly not provided by this framework.

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
