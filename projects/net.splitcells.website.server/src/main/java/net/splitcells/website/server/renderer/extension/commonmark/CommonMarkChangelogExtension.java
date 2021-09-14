package net.splitcells.website.server.renderer.extension.commonmark;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;
import net.splitcells.website.server.renderer.extension.ProjectRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.dem.resource.host.Files.is_file;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;
import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkRenderer.commonMarkRenderer;

public class CommonMarkChangelogExtension  implements ProjectRendererExtension {

    public static CommonMarkChangelogExtension commonMarkChangelogExtension() {
        return new CommonMarkChangelogExtension();
    }

    private final CommonMarkRenderer renderer = commonMarkRenderer();

    private CommonMarkChangelogExtension() {
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (path.endsWith("CHANGELOG.html") && is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            final var pathContent = readString(projectRenderer.projectFolder().resolve("CHANGELOG.md"));
            return Optional.of(
                    renderingResult(renderer.render(pathContent, projectRenderer, path)
                            , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            ProjectRenderer.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("CHANGELOG.html"));
        }
        return layout;
    }
}
