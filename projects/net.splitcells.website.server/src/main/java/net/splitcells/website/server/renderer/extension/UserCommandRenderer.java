package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.ProjectRendererExtension;
import net.splitcells.website.server.renderer.RenderingResult;

import java.util.Optional;

public class UserCommandRenderer implements ProjectRendererExtension {
    public static UserCommandRenderer userCommandRenderer() {
        return new UserCommandRenderer();
    }

    private UserCommandRenderer() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        return null;
    }
}
