package net.splitcells.website.server.renderer.extension.commonmark;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.extension.ProjectRendererExtension;
import net.splitcells.website.server.renderer.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.dem.resource.host.Files.is_file;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;
import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkRenderer.commonMarkRenderer;

public class CommonMarkReadmeExtension implements ProjectRendererExtension {

    public static CommonMarkReadmeExtension commonMarkReadmeExtension() {
        return new CommonMarkReadmeExtension();
    }

    private final CommonMarkRenderer renderer = commonMarkRenderer();

    private CommonMarkReadmeExtension() {
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (path.endsWith("README.html") && is_file(projectRenderer.projectFolder().resolve("README.md"))) {
            final var pathContent = readString(projectRenderer.projectFolder().resolve("README.md"));
            return Optional.of(
                    renderingResult(renderer.render(pathContent, projectRenderer)
                            , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (is_file(projectRenderer.projectFolder().resolve("README.md"))) {
            ProjectRenderer.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("README.html"));
        }
        return layout;
    }
}
