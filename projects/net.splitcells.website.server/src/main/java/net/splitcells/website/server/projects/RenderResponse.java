package net.splitcells.website.server.projects;

import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.processor.Response;

import java.util.Optional;

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
        return Optional.empty();
    }
}
