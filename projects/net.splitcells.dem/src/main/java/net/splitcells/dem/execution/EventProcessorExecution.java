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

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.resource.communication.interaction.Dsui;

import java.util.function.Supplier;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.execution.EventProcessorExecutor.eventProcessorExecutor;

/**
 * Provides an event based effect system, that can be used by programs without side effects.
 */
@Deprecated
public class EventProcessorExecution extends ResourceI<EventProcessorExecutor> {
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
