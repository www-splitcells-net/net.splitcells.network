package net.splitcells.website.server.renderer.commonmark;

import net.splitcells.website.server.renderer.ProjectRenderer;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;

public class CommonMarkRenderer {
    public static CommonMarkRenderer commonMarkRenderer() {
        return new CommonMarkRenderer();
    }

    final Parser parser = Parser.builder().build();
    final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private CommonMarkRenderer() {
    }

    public byte[] render(String arg, ProjectRenderer projectRenderer) {
        final Optional<String> title;
        final String contentToRender;
        if (arg.startsWith("#")) {
            final var titleLine = arg.split("[\r\n]+")[0];
            title = Optional.of(titleLine.replaceAll("#", "").trim());
            contentToRender = arg.substring(titleLine.length());
        } else {
            title = Optional.empty();
            contentToRender = arg;
        }
        return projectRenderer
                .renderHtmlBodyContent(renderer.render(parser.parse(contentToRender)), title)
                .get();
    }
}
