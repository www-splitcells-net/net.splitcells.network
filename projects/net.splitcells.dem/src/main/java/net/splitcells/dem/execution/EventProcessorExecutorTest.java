/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.execution;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static net.splitcells.dem.execution.EventProcessorExecutor.eventProcessorExecutor;
import static org.assertj.core.api.Assertions.assertThat;

@Deprecated
public class EventProcessorExecutorTest {
    @Test
    public void test() {
        final var testResult = new AtomicInteger(0);
        try (final EventProcessorExecutor testSubject = eventProcessorExecutor()) {
            testSubject.start();
            final var f = new Semaphore(1);
            IntStream.rangeClosed(1, 3).forEach(i ->
                    testSubject.register(new EventProcessor() {
                        @Override
                        public void processEvents() {
                            testResult.addAndGet(1);
                            testSubject.register(new EventProcessor() {
                                @Override
                                public void processEvents() {
                                    testResult.addAndGet(1);
                                    if (i == 3) {
                                        f.release();
                                    }
                                }
                            });
                        }
                    })
            );
            try {
                f.acquire(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        assertThat(testResult.get()).isEqualTo(3 * 2);
    }
}
