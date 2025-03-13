/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.network.worker.via.java;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.network.worker.via.java.NetworkWorker.networkWorker;

public class NetworkWorkerTest {
    @UnitTest
    public void testTestAtRemote() {
        requireEquals(networkWorker().testAtRemote("user@address", config -> config.withDryRun(true))
                .remoteExecutionScript(), "ssh user@address -t \"cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.execute --name=net.splitcells.network.worker --command=cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap.container --execute-via-ssh-at=user@address --use-host-documents=false --publish-execution-image=false --verbose=false --only-build-image=false --only-execute-image=false --dry-run=true --use-playwrightfalse --auto-configure-cpu-architecture-explicitly=true\"");
    }
}
