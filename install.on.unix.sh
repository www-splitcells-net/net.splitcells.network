#!/usr/bin/env bash

setupEnvironment=./src/main/bash/command/managed/command.managed.environment.configure
chmod +x $setupEnvironment
$setupEnvironment

commandRepoRegister=./src/main/bash/command/managed/command.repository.register
chmod +x $commandRepoRegister
$commandRepoRegister $(pwd)

installer=./src/main/bash/command/managed/command.repositories.install
chmod +x $installer
$installer

echo You may need to restart the computer in order to access the installed programs.
echo At the very least you need to open a new terminal in order to access these programs.