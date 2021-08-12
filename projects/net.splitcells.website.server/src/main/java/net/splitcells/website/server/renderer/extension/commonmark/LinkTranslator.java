package net.splitcells.website.server.renderer.extension.commonmark;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;

public class LinkTranslator extends AbstractVisitor {
    @Override
    public void visit(Image image) {
        image.setDestination(image.getDestination()
                .replace("../", "")
                .replaceAll("src\\/main\\/[a-z]+\\/", "/"));
        this.visitChildren(image);
    }
}
