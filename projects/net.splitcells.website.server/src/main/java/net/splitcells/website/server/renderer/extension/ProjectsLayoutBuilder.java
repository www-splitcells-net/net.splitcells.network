package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

public class ProjectsLayoutBuilder implements ProjectRendererExtension {

    private static final String LAYOUT_PATH = "/net/splitcells/website/layout/build";

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (LAYOUT_PATH.equals(path)) {
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        projectRenderer.extendPerspectiveWithPath(layout
                , Path.of(LAYOUT_PATH));
    }
}
