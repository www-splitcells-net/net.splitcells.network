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

import net.splitcells.dem.environment.resource.Console;
import net.splitcells.dem.environment.resource.ResourceOptionImpl;
import net.splitcells.dem.resource.communication.Sender;

import java.util.function.Predicate;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.resource.communication.log.CommonMarkLogger.commonMarkDui;

/**
 * <p>By default, only events, that require action, signal overhaul execution progress for the log inspector should be shown.
 * Log code should be separated from the actual as much as possible, in order to reduce noise of regular program code.
 * This is especially the case for trace logging.</p>
 * <p>TODO Create compact and standardize log format.
 * Each message should only take up one line, so that filtering messages is easy.</p>
 * <p>TODO In the future, this should be a counter part of the web server.</p>
 * <p>TODO Message filtering and routing should be done by dedicated classes,
 * so that rendering can be separated from the rest.
 * </p>
 */
public class Logs extends ResourceOptionImpl<Logger> {
    /**
     * {@link CommonMarkLogger#commonMarkDui(Sender, Predicate)} is used by default,
     * as it looks nice in consoles and on websites supporting CommonMark rendering.
     */
    public Logs() {
        super(() -> commonMarkDui(environment().config().configValue(Console.class)
                , environment().config().configValue(MessageFilter.class)));
    }

    public static Log logs() {
        return environment().config().configValue(Logs.class);
    }
}
