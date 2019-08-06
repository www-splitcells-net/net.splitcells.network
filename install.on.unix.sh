#!/usr/bin/env bash
mvn clean install
mkdir -p ~/bin/net.splitcells.os.state.interface.commands.managed
	find ~/bin/net.splitcells.os.state.interface.commands.managed  -maxdepth 1 -type f -delete
buildFolder=./target/classes
	# TODO Define installation order.
	installer=$buildFolder/src/main/python/command/managed/command.managed.install
	chmod +x $installer
	find $buildFolder -mindepth 1 -type f -exec $installer {} \;
	chmod +x ~/bin/*
	chmod +x ~/bin/net.splitcells.os.state.interface.commands.managed/*
