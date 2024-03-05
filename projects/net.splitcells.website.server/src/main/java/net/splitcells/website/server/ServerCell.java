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
package net.splitcells.website.server;

import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.Cell;

public class ServerCell implements Cell {
    @Override
    public String groupId() {
        return "net.splitcells";
    }

    @Override
    public String artifactId() {
        return "website.server";
    }

    @Override
    public void accept(Environment env) {
        env.config().withInitedOption(ServerService.class);
    }
}
