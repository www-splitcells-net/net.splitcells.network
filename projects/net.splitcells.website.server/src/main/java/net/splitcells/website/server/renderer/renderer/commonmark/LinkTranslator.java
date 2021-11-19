package net.splitcells.website.server.renderer.renderer.commonmark;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Link;

public class LinkTranslator extends AbstractVisitor {

    public static LinkTranslator linkTranslator() {
        return new LinkTranslator();
    }

    private LinkTranslator() {

    }

    @Override
    public void visit(Image image) {
        image.setDestination(image.getDestination()
                .replace("../", "")
                .replaceAll("src\\/main\\/[a-z]+\\/", "/"));
        this.visitChildren(image);
    }

    public void visit(Link link) {
        link.setDestination(link.getDestination()
                .replace("../", "")
                .replaceAll("src\\/main\\/[a-z]+\\/", "/"));
        if (link.getDestination().endsWith(".md")) {
            link.setDestination(link.getDestination().substring(0, link.getDestination().length() - 3) + ".html");
        }
        this.visitChildren(link);
    }
}
