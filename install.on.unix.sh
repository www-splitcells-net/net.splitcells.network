#!/usr/bin/env bash

commandRepoRegister=./src/main/bash/command/managed/command.repository.register
chmod +x $commandRepoRegister
$commandRepoRegister $(pwd)

installer=./src/main/bash/command/managed/command.repositories.install
chmod +x $installer
$installer

echo
	# We create a empty line in order to visually separate the execution output from the result output.
echo OS state interface installed.
echo
echo Execute "'". ~/.profile"'" in order to access the installed programs on this terminal.
echo You may need to restart the computer in order to access the installed programs without this command on new terminals.
echo At the very least you need to open a new terminal in order to access these programs.