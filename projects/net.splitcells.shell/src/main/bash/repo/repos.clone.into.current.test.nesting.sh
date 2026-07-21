#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
set -e
host=$(pwd)
mkdir -p $host/root/.net.splitcells.shell.repo
    echo '{"subs":{"sub-1":{},"sub-2":{}}}' > $host/root/.net.splitcells.shell.repo/subs.json
    mkdir $host/root/sub-1
    mkdir -p $host/root/sub-2/.net.splitcells.shell.repo
	cd $host/root
	repo.create
	repo.commit.all
cd $host/root/sub-1
	repo.create
	repo.commit.all
cd $host/root/sub-2
    mkdir $host/root/sub-2/sub-sub-1
    echo '{"subs":{"sub-sub-1":{}}}' > $host/root/sub-2/.net.splitcells.shell.repo/subs.json
	repo.create
	repo.commit.all
cd $host/root/sub-2/sub-sub-1
	repo.create
	repo.commit.all
mkdir $host/clone
	cd $host/clone
	repos.clone.into.current $host/root
path.is.folder $host/clone/sub-1
path.is.folder $host/clone/sub-2
path.is.folder $host/clone/sub-2/sub-sub-1
