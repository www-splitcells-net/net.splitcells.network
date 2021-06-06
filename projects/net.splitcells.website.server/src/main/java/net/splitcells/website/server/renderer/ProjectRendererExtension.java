package net.splitcells.website.server.renderer;

import net.splitcells.dem.lang.perspective.Perspective;

import java.nio.file.Path;
import java.util.Optional;

public interface ProjectRendererExtension {
    Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer);

    Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer);

}
