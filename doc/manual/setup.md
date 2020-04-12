# Setup Instructions
In order to install this program following instructions have to be executed in the console.
Note that thereby a folder will be created in the current location.
The newly created folder should not be moved, after the setup.

There are 2 ways to install this program.

## Requirements

Currently a bash compatible terminal and Python >= 3.7 is required.

## Most Compatible Setup Instructions
```sh
git clone https://gitlab.com/splitcells/net.splitcells.os.state.interface.git
cd net.splitcells.os.state.interface
chmod +x ./bin/install
./bin/install
```

## Make based Installation
```sh
git clone https://gitlab.com/splitcells/net.splitcells.os.state.interface.git
cd net.splitcells.os.state.interface
chmod +x ./configure
./configure
make
make install
```

# Extend Functionality

Congratulations!
Now you can do nothing!
On a more serious ;)
This framework primarily provides a set of commands in order to do things independent from technical details.
This set of commands define an interface, but does not contain the concrete implementation for these commands.

For this additional repositories has to be installed.
These provide the implementations for the different operation systems.

This can be done in the following ways in the terminal:
```sh
# Find and download a repository that fits for this framework.
git clone <URL>
# Enter the newly downloaded repository.
cd <New clone of repository>
# Register the new repository.
command.repository.register $(pwd)
# Install the new repository.
user.bin.configure
```

# End Your Adventure

So you decided that enough is enough?
You want to get rid of the program?
[Go on.](./unistall.md)

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
