package net.splitcells.website.server.renderer;

import net.splitcells.dem.lang.perspective.Perspective;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.dem.resource.host.Files.is_file;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;

public class CommonMarkExtension implements ProjectRendererExtension {

    public static CommonMarkExtension commonMarkExtension() {
        return new CommonMarkExtension();
    }

    private CommonMarkExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (path.endsWith("README.html") && is_file(projectRenderer.projectFolder().resolve("README.md"))) {
            Parser parser = Parser.builder().build();
            final var pathContent = readString(projectRenderer.projectFolder().resolve("README.md"));
            final HtmlRenderer renderer = HtmlRenderer.builder().build();
            final Node document;
            final Optional<String> title;
            if (pathContent.startsWith("#")) {
                final var titleLine = pathContent.split("[\r\n]+")[0];
                title = Optional.of(titleLine.replaceAll("#", "").trim());
                document = parser.parse(pathContent.substring(titleLine.length()));
            } else {
                title = Optional.empty();
                document = parser.parse(pathContent);
            }
            return projectRenderer.renderHtmlBodyContent(renderer.render(document), title)
                    .map(result -> renderingResult
                            (result
                                    , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (is_file(projectRenderer.projectFolder().resolve("README.md"))) {
            return projectRenderer.extendPerspectiveWithPath
                    (layout
                            , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("README.html"));
        }
        return layout;
    }
}
