# Why no Ansible etc. integration?
The framework's aim is to require as little as possible and to be compatible to as 
many things as possible.
Therefore, this framework could be integrated via wrapper programs into Ansible.
It is also trivial to integrate Ansible into this framework via Ad-hoc commands.

There are certainly good arguments to convert this project to Ansible.
The biggest point that currently works against this conversion is the fact that I do not know how  
to implement dependency injection as it is implemented in this framework.
(This seems to be important as Ansible modules like package are licensed under the GPL v3.0+)[https://github.com/ansible/ansible/blob/f76556e56d586e339fae9fabb7e0c112baff0849/lib/ansible/modules/packaging/os/package.py]

Also I find it quite hard to change the state of the system fast and easy in Ansible:
* Ansible: sudo ansible localhost -m package -a "name=xonotic state=present"
* os.state.interface: package.install xonotic

Note that for most sh consoles autocomplete support for the command 'package.install' could be implemented as well with a reasonable amount of resources.
In contrast this seems to be a complex task for the Ansible command.

# Program Compatibility

It assumed that every program, that can be accessed by the current user are compatible with each other.
If a program cannot be access by the current, but root is able to access this program, it is assumed that the program is not compatible with the system.
Incompatible programs are only handled, if the user explicitly wants it.

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
