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
}
