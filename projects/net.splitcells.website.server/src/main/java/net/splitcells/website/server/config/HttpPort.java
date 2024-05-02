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
package net.splitcells.website.server.config;

import net.splitcells.dem.environment.config.framework.Option;

/**
 * Defines the default port for HTTP.
 * Using 80 is not desirable,
 * as port 80 sometimes requires additional rights as it is in the Linux's privileged port range 1-1023.
 * If this is the case, no real error message is created sometimes and can eat up a lot of time.
 */
public class HttpPort implements Option<Integer> {
    @Override
    public Integer defaultValue() {
        return 8081;
    }
}
