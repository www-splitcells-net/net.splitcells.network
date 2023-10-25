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
package net.splitcells.website.server.processor;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.resource.Trail;

import static net.splitcells.dem.data.set.map.Maps.map;

public class ProcessorRegistry<Source, Target> implements Processor<Source, Target> {
    public static <Source, Target> ProcessorRegistry<Source, Target> binaryProcessorRegistry() {
        return new ProcessorRegistry<>();
    }

    private final Map<Trail, Processor<Source, Target>> processors = map();

    private ProcessorRegistry() {

    }

    @Override
    public Response<Target> process(Request<Source> request) {
        return processors.get(request.trail()).process(request);
    }

    public ProcessorRegistry<Source, Target> register(Trail trail, Processor<Source, Target> processor) {
        processors.put(trail, processor);
        return this;
    }
}
