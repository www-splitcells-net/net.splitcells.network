package net.splitcells.website.server.project.renderer.commonmark;

import net.splitcells.dem.utils.CommonFunctions;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Link;

import java.util.regex.Pattern;

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

    private static Pattern PROTOCOL = Pattern.compile("([a-z]+://)(.*)");

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
        final var destination = link.getDestination();
        final var protocolMatch= PROTOCOL.matcher(destination);
        final String protocol;
        if (protocolMatch.matches()) {
            protocol = protocolMatch.group(1);
        } else {
            protocol = "";
        }
        final var destinationWithoutProtocol = destination
                .substring(protocol.length())
                .replace("../", "")
                .replaceAll("src\\/main\\/[a-z]+\\/", "/")
                .replaceAll("projects\\/[a-z\\.]+\\/", "/")
                .replace("//", "/");

        link.setDestination(protocol + destinationWithoutProtocol);
        if (link.getDestination().endsWith(".md") && protocol.isEmpty()) {
            link.setDestination(link.getDestination().substring(0, link.getDestination().length() - 3) + ".html");
        }
        this.visitChildren(link);
    }
}
