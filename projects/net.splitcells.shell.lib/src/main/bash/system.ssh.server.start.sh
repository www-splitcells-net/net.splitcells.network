#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
system.ssh.server.require 2>&1 >/dev/null && exit
# TODO Create script to disable passwordless authentication:
if [ "1" -eq "$(system.is.Ubuntu)" ]; then
	sudo apt install -y openssh-server
	sudo systemctl start ssh
	sudo systemctl is-active --quiet ssh
	if [ "0" -ne "$?" ]; then
		echo.error "SSH serve could not be started."
		exit 1
	fi
	# TODO Check PasswordAuthentication configuration.
elif command -v dnf ; then
	sudo dnf install -y openssh-server
	sudo systemctl start sshd.service
else
	echo.error This operation system is not explicitly supported and therefore the required packages for SSH are not checked. It could be the case, that this still works.
fi
system.ssh.server.require