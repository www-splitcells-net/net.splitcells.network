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

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
