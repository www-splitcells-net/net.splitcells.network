package net.splitcells.website.server.renderer.extension.commonmark;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;
import net.splitcells.website.server.renderer.extension.ProjectRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkRenderer.commonMarkRenderer;

public class CommonMarkExtension implements ProjectRendererExtension {
    public static CommonMarkExtension commonMarkExtension() {
        return new CommonMarkExtension();
    }

    private final CommonMarkRenderer renderer = commonMarkRenderer();

    private CommonMarkExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        return null;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        return null;
    }
}
