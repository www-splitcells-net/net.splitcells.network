#!/usr/bin/env bash
# TODO This is an hack. The build process should create and executable installer file that contains all files.
mkdir ~/bin/net.splitcells.os.state.interface.commands.managed
find ~/bin/net.splitcells.os.state.interface.commands.managed  -maxdepth 1 -type f -delete
chmod +x ./src/main/bash/user.commands.install
find ./src/main -mindepth 1 -type d -exec ./src/main/bash/user.commands.install {} +
chmod +x ~/bin/*
	chmod +x ~/bin/net.splitcells.os.state.interface.commands.managed/*