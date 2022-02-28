package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutUtils;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.order.Comparators.comparator;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration.commonMarkIntegration;

public class CommonMarkChangelogEventProjectRendererExtension implements ProjectRendererExtension {

    public static CommonMarkChangelogEventProjectRendererExtension commonMarkChangelogEventRenderer() {
        return new CommonMarkChangelogEventProjectRendererExtension();
    }

    private final CommonMarkIntegration renderer = commonMarkIntegration();

    private CommonMarkChangelogEventProjectRendererExtension() {
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith("CHANGELOG.events.html") && is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            final var pathContent = readString(projectRenderer.projectFolder().resolve("CHANGELOG.md"));
            final var events = renderer.events(pathContent, projectRenderer, path, config);
            events.sort(Comparators.comparator((a, b) -> a.dateTime().compareTo(b.dateTime())));
            final var renderedEvents = events.stream().map(Event::node)
                    .map(e -> renderer.render(e))
                    .reduce((a, b) -> a + "\n" + b);
            if (renderedEvents.isPresent()) {
                return Optional.of(
                        renderingResult(renderedEvents.get().getBytes(StandardCharsets.UTF_8)
                                , TEXT_HTML.toString()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            LayoutUtils.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("CHANGELOG.events.html"));
        }
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final Set<Path> projectPaths = setOfUniques();
        if (is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            projectPaths.add(Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("CHANGELOG.events.html"));
        }
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
