package net.splitcells.website.server.renderer.commonmark;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class CommonMarkRenderer {
    public static CommonMarkRenderer commonMarkRenderer() {
        return new CommonMarkRenderer();
    }

    final Parser parser = Parser.builder().build();
    final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private CommonMarkRenderer() {
    }

    public String render(String arg) {
        return renderer.render(parser.parse(arg));
    }
}
