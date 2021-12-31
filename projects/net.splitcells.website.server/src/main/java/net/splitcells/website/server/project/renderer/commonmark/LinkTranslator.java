package net.splitcells.website.server.project.renderer.commonmark;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Link;

/**
 * <p>This class translates links inside CommonMark documents.
 * It assumes, that relative links are all relative to the documents position
 * inside the enclosing project's source code.
 * It is assumed, that the enclosing project's file structure is similar
 * to this project's source code repository.</p>
 * <p>TODO The implementation is based on heuristics in order to make the
 * implementation easy.
 * Link validation via {@link net.splitcells.website.RenderingValidator} is
 * used in order to ensure,
 * that the heuristics are good enough for now.</p>
 */
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
                .replaceAll("src\\/main\\/[a-z]+\\/", "/")
                .replaceAll("projects\\/[a-z\\.]+\\/", "/")
                .replace("//", "/"));
        if (link.getDestination().endsWith(".md")) {
            link.setDestination(link.getDestination().substring(0, link.getDestination().length() - 3) + ".html");
        }
        this.visitChildren(link);
    }
}
