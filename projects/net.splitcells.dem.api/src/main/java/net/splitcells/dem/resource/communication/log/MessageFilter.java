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
        super(() -> logMessage -> logMessage.priority().greaterThan(DEBUG));
    }
}
