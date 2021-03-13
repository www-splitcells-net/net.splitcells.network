package net.splitcells.website.server.translation.to.html;

import static java.nio.file.Files.newInputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;


public class PathBasedUriResolver implements URIResolver {

    private final Path folder;

    public PathBasedUriResolver(Path folder) {
        this.folder = folder;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        try {
            final Path path;
            if (Paths.get(href).isAbsolute()) {
                path = Paths.get(href);
            } else {
                path = folder.resolve(href);
            }
            final var rVal = new StreamSource(newInputStream(path));
            /*
             * Setting systemId to the underlying file in order to resolve relative paths
             * used in the return value.
             */
            rVal.setSystemId(Paths.get(href).toString());
            return rVal;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
