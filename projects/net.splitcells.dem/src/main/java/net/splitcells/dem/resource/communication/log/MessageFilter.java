/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.environment.config.framework.OptionI;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.communication.interaction.LogMessage;

import java.util.function.Predicate;

import static net.splitcells.dem.resource.communication.interaction.LogLevel.DEBUG;

/**
 * TODO RENAME This is not a filter but a selector.
 * <p>
 * If a {@link LogMessage} is true according to this predicate,
 * it should be printed according to this predicate.
 */
public class MessageFilter extends OptionI<Predicate<LogMessage<Perspective>>> {
    public MessageFilter() {
        super(() -> logMessage -> logMessage.priority().smallerThan(DEBUG));
    }
}
