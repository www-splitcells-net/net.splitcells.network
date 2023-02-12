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

import net.splitcells.dem.environment.resource.ResourceOptionI;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.execution.EventProcessorExecutor.eventProcessorExecutor;

/**
 * Provides an event based effect system, that can be used by programs without side effects.
 */
@Deprecated
public class EventProcessorExecution extends ResourceOptionI<EventProcessorExecutor> {
    public EventProcessorExecution() {
        super(() -> {
            final var executor = eventProcessorExecutor();
            executor.start();
            return executor;
        });
    }

    public static void register(EventProcessor processor) {
        environment().config().configValue(EventProcessorExecution.class).register(processor);
    }
}
