#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

set -e
user.ssh.key.generate
sshPrivateKey=$(cat ~/.ssh/id_rsa)
sshPublicKey=$(cat ~/.ssh/id_rsa.pub)
user.ssh.key.generate
if [[ $(cat ~/.ssh/id_rsa) != "$sshPrivateKey" ]]; then
	echo.error "Initial private key overwritten."
	exit 1
fi
if [[ $(cat ~/.ssh/id_rsa.pub) != "$sshPublicKey" ]]; then
	echo.error "Initial public key overwritten."
	exit 1
fi
