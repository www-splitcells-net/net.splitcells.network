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
package net.splitcells.dem.testing.need;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.log.LogLevel;

@Accessors(chain = true)
public class NeedException extends RuntimeException {
    public static NeedException needException(LogLevel logLevel, Tree message) {
        return new NeedException(logLevel, message);
    }

    @Getter private final Tree message;
    @Getter private final LogLevel logLevel;

    private NeedException(LogLevel argLogLevel, Tree argMessage) {
        message = argMessage;
        logLevel = argLogLevel;
    }
}
