package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;

public class ExtensionMerger implements ProjectRendererExtension {
    public static ExtensionMerger extensionMerger() {
        return new ExtensionMerger();
    }

    private final List<ProjectRendererExtension> extensions = list();

    private ExtensionMerger() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        return null;
    }

    public void registerExtension(ProjectRendererExtension extension) {
        extensions.add(extension);
    }
}