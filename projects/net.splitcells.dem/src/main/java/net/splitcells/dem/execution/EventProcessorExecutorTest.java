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
package net.splitcells.dem.execution;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static net.splitcells.dem.execution.EventProcessorExecutor.eventProcessorExecutor;
import static org.assertj.core.api.Assertions.assertThat;

@Deprecated
@JavaLegacyArtifact
public class EventProcessorExecutorTest {
    @Test
    @Disabled
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
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        assertThat(testResult.get()).isEqualTo(3 * 2);
    }
}
