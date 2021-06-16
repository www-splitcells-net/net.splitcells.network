package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.renderer.ProjectRenderer.extendPerspectiveWithPath;
import static org.assertj.core.api.Assertions.assertThat;

public class ExtensionMerger implements ProjectRendererExtension {
    public static ExtensionMerger extensionMerger() {
        return new ExtensionMerger();
    }

    private final List<ProjectRendererExtension> extensions = list();

    private ExtensionMerger() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        final var rendering = extensions.stream()
                .map(e -> e.renderFile(path, projectRenderer))
                .filter(e -> e.isPresent())
                .collect(Lists.toList());
        if (rendering.size() > 1) {
            throw new RuntimeException(rendering.toString());
        }
        if (rendering.isEmpty()) {
            return Optional.empty();
        } else {
            return rendering.get(0);
        }
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        extensions.forEach(e -> e.extendProjectLayout(layout, projectRenderer));
        return layout;
    }

    public void registerExtension(ProjectRendererExtension extension) {
        extensions.add(extension);
    }
}