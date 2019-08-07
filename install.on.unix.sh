#!/usr/bin/env bash
commandRepoRegister=./src/main/bash/command/managed/command.repository.register
chmod +x $commandRepoRegister
$commandRepoRegister $(pwd)
installer=./src/main/bash/command/managed/command.repositories.install
chmod +x $installer
$installer
