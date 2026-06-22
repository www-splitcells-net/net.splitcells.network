#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# On Ubuntu the snap installation of gitui does not seem to work.
# Ubuntu's Java and Maven are highly unlikely to match this project's required versions.

# On Ubuntu apt might install Java JRE via Maven, which creates the following misleading error: Fatal error compiling: error: release version 21 not supported
# Also do not forget to restart all shells, if problems persist.
# TODO Consider development via container as main one.
sudo apt install -y openjdk-21-jdk

curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
sudo apt install make
cargo install gitui --locked

curl -s "https://get.sdkman.io" | bash
source "/home/mavo-stream/.sdkman/bin/sdkman-init.sh"
sdk install maven
sdk install mvnd
