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

import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Response;

import java.util.Optional;

/**
 * This interface alias was created, in order to not change {@link Processor}
 * for now.
 * TODO It might be necessary to do this in the future,
 * as unifying the {@link Processor} and {@link ProjectsRenderer} interface is a goal of the future.
 */
public class RenderResponse implements Response<Optional<BinaryMessage>> {

    public static RenderResponse renderResponse() {
        return new RenderResponse(Optional.empty());
    }

    public static RenderResponse renderResponse(BinaryMessage data) {
        return new RenderResponse(Optional.of(data));
    }

    public static RenderResponse renderResponse(Optional<BinaryMessage> data) {
        return new RenderResponse(data);
    }

    private final Optional<BinaryMessage> data;

    private RenderResponse(Optional<BinaryMessage> argData) {
        data = argData;
    }

    @Override
    public Optional<BinaryMessage> data() {
        return data;
    }

    @Override
    public boolean hasData() {
        return data.isPresent();
    }
}
