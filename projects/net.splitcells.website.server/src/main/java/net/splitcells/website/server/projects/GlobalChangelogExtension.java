package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectsRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension.commonMarkChangelogEventRenderer;

public class GlobalChangelogExtension implements ProjectsRendererExtension {
    public static GlobalChangelogExtension globalChangelogExtension() {
        return new GlobalChangelogExtension();
    }

    private GlobalChangelogExtension() {

    }

    private final CommonMarkChangelogEventProjectRendererExtension eventUtils = commonMarkChangelogEventRenderer();

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRenderer projectsRenderer, Config config) {
        if ("/net/splitcells/CHANGELOG.global.html".equals(path)) {
            final var events = projectsRenderer.projectRenderers().stream()
                    .map(pr -> eventUtils.extractEvent(pr.resourceRootPath2().resolve("CHANGELOG.events.html").toString(), pr, config))
                    .reduce(List::withAppended)
                    .orElseGet(Lists::list);
            return Optional.of(
                    renderingResult(projectsRenderer.renderHtmlBodyContent(eventUtils.renderEvents(events)
                                    , Optional.empty()
                                    , Optional.empty()
                                    , config).get()
                            , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRenderer projectsRenderer) {
        return setOfUniques(Path.of(projectsRenderer.config().rootPath() + "/CHANGELOG.global.html"));
    }
}
