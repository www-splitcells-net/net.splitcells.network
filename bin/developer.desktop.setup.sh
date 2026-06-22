#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# On Ubuntu the snap installation of gitui does not seem to work.

curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
sudo apt install make
cargo install gitui --locked
