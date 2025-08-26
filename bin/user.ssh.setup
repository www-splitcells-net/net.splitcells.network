#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
mkdir -p ~/.ssh/
touch ~/.ssh/known_hosts
ssh-keygen -F codeberg.org || ssh-keyscan codeberg.org >> ~/.ssh/known_hosts
if [ -f "$HOME/.config/net.splitcells.network.worker/dominant.management" ] && [ "1" = "$(cat $HOME/.config/net.splitcells.network.worker/dominant.management)" ]; then
  touch ~/.ssh/config
  if ! grep -F 'Host *' "$HOME/.ssh/config"; then
    # On Codeberg not reusing connections causes the Codeberg server to reject new SSH connections.
    # This causes git pulls in workflows to fail and therefore SSH connections are reused.
    echo """Host *
      # Reuse SSH connection
      ControlMaster auto
      ControlPath ~/.ssh/connection-%r@%h-%p
      ControlPersist 10m""" >> ~/.ssh/config
  fi
else
  echo Warning: dominant management is not enabled.
fi
