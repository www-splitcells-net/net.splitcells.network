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

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.network.worker.via.java.NetworkWorker.networkWorker;

public class NetworkWorkerTest {
    @UnitTest
    public void testBootstrapRemote() {
        val testExecution = networkWorker().bootstrapRemote("user@address");
        requireEquals(testExecution.getRemoteExecutionScript()
                , """
                        # Execute Main Task Remotely
                        ssh user@address /bin/sh << EOF
                          set -e
                          if [ ! -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network ]; then
                            mkdir -p ~/.local/state/net.splitcells.network.worker/repos/public/
                            cd ~/.local/state/net.splitcells.network.worker/repos/public/
                            git clone https://codeberg.org/splitcells-net/net.splitcells.network.git
                          fi
                          cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network \\
                          && bin/worker.execute \\
                            --name='net.splitcells.network.worker'\\
                            --flat-folders='true'\\
                            --command='cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap'\\
                            --use-host-documents='false'\\
                            --publish-execution-image='false'\\
                            --verbose='false'\\
                            --only-build-image='false'\\
                            --only-execute-image='false'\\
                            --dry-run='true'\\
                            --use-playwright='false'\\
                            --auto-configure-cpu-architecture-explicitly='true'
                        EOF
                        """);
    }

    @UnitTest
    public void testBootstrapRemoteViaADaemon() {
        val testExecution = networkWorker().bootstrapRemote("user@address"
                , c -> c.setDaemon(true).setForcedDaemonName("forced-daemon-name"));
        requireEquals(testExecution.getRemoteExecutionScript()
                , """
                        # Set up Systemd service remotely
                        ssh user@address /bin/sh << EOF
                          set -e
                          mkdir -p ~/.config/systemd/user
                          cat > ~/.config/systemd/user/forced-daemon-name <<SERVICE_EOL
                        [Unit]
                        Description=Execute forced-daemon-name
                        
                        [Service]
                        Type=oneshot
                        StandardOutput=journal
                        ExecStart=/usr/bin/date
                        SERVICE_EOL
                        
                        EOF
                        """);
    }

    @UnitTest
    public void testBootstrapRemoteWithNetworkLogPull() {
        val testExecution = networkWorker().bootstrapRemote("user@address"
                , c -> c.setPullNetworkLog(true));
        requireEquals(testExecution.getRemoteExecutionScript()
                , """
                        # Preparing Execution via Network Log Pull
                        if ssh -q user@address "sh -c '[ -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log ]'"
                        then
                          cd ../net.splitcells.network.log
                          git config remote.user@address.url >&- || git remote add user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
                          git remote set-url user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
                          git remote set-url --push user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
                          git pull user@address master
                          cd ../net.splitcells.network
                        fi
                        
                        # Execute Main Task Remotely
                        ssh user@address /bin/sh << EOF
                          set -e
                          if [ ! -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network ]; then
                            mkdir -p ~/.local/state/net.splitcells.network.worker/repos/public/
                            cd ~/.local/state/net.splitcells.network.worker/repos/public/
                            git clone https://codeberg.org/splitcells-net/net.splitcells.network.git
                          fi
                          cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network \\
                          && bin/worker.execute \\
                            --name='net.splitcells.network.worker'\\
                            --flat-folders='true'\\
                            --command='cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap'\\
                            --use-host-documents='false'\\
                            --publish-execution-image='false'\\
                            --verbose='false'\\
                            --only-build-image='false'\\
                            --only-execute-image='false'\\
                            --dry-run='true'\\
                            --use-playwright='false'\\
                            --auto-configure-cpu-architecture-explicitly='true'
                        EOF
                        
                        # Closing Execution via Network Log Pull
                        cd ../net.splitcells.network.log
                        git config remote.user@address.url >&- || git remote add user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
                        git remote set-url user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
                        git remote set-url --push user@address user@address:/home/user/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network.log
                        git pull user@address master
                        """);
    }

    @UnitTest
    public void testTestAtRemote() {
        requireEquals(networkWorker().testAtRemote("user@address")
                .getRemoteExecutionScript(), """
                # Execute Main Task Remotely
                ssh user@address /bin/sh << EOF
                  set -e
                  if [ ! -d ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network ]; then
                    mkdir -p ~/.local/state/net.splitcells.network.worker/repos/public/
                    cd ~/.local/state/net.splitcells.network.worker/repos/public/
                    git clone https://codeberg.org/splitcells-net/net.splitcells.network.git
                  fi
                  cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network \\
                  && bin/worker.execute \\
                    --name='net.splitcells.network.worker'\\
                    --flat-folders='true'\\
                    --command='cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/worker.bootstrap && bin/repos.test'\\
                    --use-host-documents='false'\\
                    --publish-execution-image='false'\\
                    --verbose='false'\\
                    --only-build-image='false'\\
                    --only-execute-image='false'\\
                    --dry-run='true'\\
                    --use-playwright='false'\\
                    --auto-configure-cpu-architecture-explicitly='true'
                EOF
                """);
    }
}
