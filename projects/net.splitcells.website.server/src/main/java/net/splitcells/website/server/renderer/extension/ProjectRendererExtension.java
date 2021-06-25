package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

public interface ProjectRendererExtension {
    Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer);

    /**
     * TODO A list of paths should be returned instead. Also the layout would not be needed anymore in this case.
     *
     * @param layout
     * @param projectRenderer
     * @return
     */
    Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer);

}
