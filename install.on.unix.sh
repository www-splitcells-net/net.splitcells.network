#!/usr/bin/env bash
# TODO This is an hack. The build process should create and executable installer file that contains all files.
rm ~/bin/*
chmod +x ./src/main/bash/user.commands.install
find ./src/main -mindepth 1 -type d -exec ./src/main/bash/user.commands.install {} +
chmod +x ~/bin/*