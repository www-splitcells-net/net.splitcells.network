package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.website.server.project.validator.RenderingValidator;
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
 * Link validation via {@link RenderingValidator} is
 * used in order to ensure,
 * that the heuristics are good enough for now.</p>
 */
public class LinkTranslator extends AbstractVisitor {

    private static Pattern PROTOCOL = Pattern.compile("([a-z]+://)(.*)");
    private static Pattern SUB_PROJECT_README = Pattern.compile("(\\./)?projects/[a-zA-Z\\.]+/README.md");

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
        final var protocolMatch = PROTOCOL.matcher(destination);
        final String protocol;
        if (protocolMatch.matches()) {
            protocol = protocolMatch.group(1);
        } else {
            protocol = "";
        }
        final String normalizedDestination;
        final var destinationWithoutProtocol = destination
                .substring(protocol.length());
        // TODO This is an hack.
        if (SUB_PROJECT_README.matcher(destinationWithoutProtocol).matches()) {
            normalizedDestination = destinationWithoutProtocol
                    .replace("./", "")
                    .substring(8)
                    .replace(".", "/")
                    .replaceAll("/md", ".md");
        } else {
            normalizedDestination = destinationWithoutProtocol.replace("../", "")
                    .replaceAll("^\\.?/src\\/main\\/[a-z]+\\/", "/")
                    .replaceAll("projects\\/[a-z\\.]+\\/", "/")
                    .replace("//", "/");
        }
        link.setDestination(protocol + normalizedDestination);
        // TODO This should only be done if, the link is relative and contains "src/main/md".
        if (link.getDestination().endsWith(".md") && protocol.isEmpty()) {
            link.setDestination(link.getDestination().substring(0, link.getDestination().length() - 3) + ".html");
        }
        // TODO This should only be done if, the link is relative and contains "src/main/xml".
        if (link.getDestination().endsWith(".xml") && protocol.isEmpty()) {
            link.setDestination(link.getDestination().substring(0, link.getDestination().length() - 4) + ".html");
        }
        this.visitChildren(link);
    }
}
