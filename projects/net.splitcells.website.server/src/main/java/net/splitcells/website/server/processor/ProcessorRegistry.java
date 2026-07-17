/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.processor;

import lombok.val;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.resource.Trail;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.website.server.processor.Response.emptyResponse;

public class ProcessorRegistry<Source, Target> implements Processor<Source, Target> {
    public static <Source, Target> ProcessorRegistry<Source, Target> binaryProcessorRegistry() {
        return new ProcessorRegistry<>();
    }

    private final Map<Trail, Processor<Source, Target>> processors = map();

    private ProcessorRegistry() {

    }

    @Override
    public Response<Target> process(Request<Source> request) {
        val trail = request.trail();
        if (processors.hasKey(trail)) {
            return processors.get(trail).process(request);
        }
        return emptyResponse();
    }

    public ProcessorRegistry<Source, Target> register(Trail trail, Processor<Source, Target> processor) {
        processors.put(trail, processor);
        return this;
    }
}
