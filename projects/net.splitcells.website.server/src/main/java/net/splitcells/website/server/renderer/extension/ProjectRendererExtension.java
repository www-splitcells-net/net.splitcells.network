package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

public interface ProjectRendererExtension {
    Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer);

    Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer);

}
