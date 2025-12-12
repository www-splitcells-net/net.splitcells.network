#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# TODO IDEA Upload JavaDoc to splitcells.net.

set -e
set +x # Prevent printing secrets.
# -z is used because -v is not supported in Forgejo workflows.
if [ -z "$NET_SPLITCELLS_MARTINS_AVOTS_WEBSITE_SFTP_PRIVATE_KEY" ]; then
  echo Upload to https://splitcells.net is disabled, because the environment variable NET_SPLITCELLS_MARTINS_AVOTS_WEBSITE_SFTP_PRIVATE_KEY is not set.
else
  mkdir -p ~/.ssh-website-upload/
  echo "$NET_SPLITCELLS_MARTINS_AVOTS_WEBSITE_SFTP_PRIVATE_KEY" > ~/.ssh-website-upload/id_rsa
  chmod 700 ~/.ssh-website-upload/id_rsa # Otherwise, scp may not work.
  mkdir -p target/website-upload/public_html/net/splitcells/martins/avots/website/jacoco-aggregate # Using subdirectories, ensures, that scp does not get a problem with missing directories.
  cp -r target/site/jacoco-aggregate/* target/website-upload/public_html/net/splitcells/martins/avots/website/jacoco-aggregate
  set -x
  set -e
  realpath target/website-upload/public_html/net/splitcells/martins/avots/website/jacoco-aggregate
  # The targeted scp upload folder is dependent on the relative source path. Therefore, cd and the current folder are used, in order to avoid this strange mapping.
  cd target/website-upload/
  scp -v -i ~/.ssh-website-upload/id_rsa -o PubkeyAuthentication=yes -o StrictHostKeyChecking=no -r ./ splitcm@www322.your-server.de:
  rm ~/.ssh-website-upload/id_rsa
fi