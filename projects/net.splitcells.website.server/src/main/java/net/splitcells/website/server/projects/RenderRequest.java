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
package net.splitcells.website.server.projects;

import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.RequestImpl;

import java.util.Optional;

public class RenderRequest implements Request<Optional<byte[]>> {
    public static RenderRequest renderRequest(Trail trail, Optional<byte[]> data) {
        return new RenderRequest(trail, data);
    }

    private Trail trail;
    private final Optional<byte[]> data;

    private RenderRequest(Trail trailArg, Optional<byte[]> dataArg) {
        trail = trailArg;
        data = dataArg;
    }

    public Optional<byte[]> data() {
        return data;
    }

    public Trail trail() {
        return trail;
    }
}
