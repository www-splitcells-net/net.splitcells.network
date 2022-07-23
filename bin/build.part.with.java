#!/usr/bin/env sh
# This command exists, so that the project can be build with one command.
# This makes usage of tools building this project easier,
# as it can be hard to understand in such tools,
# how to chain commands calls.
set -e
cd projects/net.splitcells.system.root
./bin/build.part.with.java
