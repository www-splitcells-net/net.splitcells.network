package net.splitcells.website.server.renderer;

import net.splitcells.dem.resource.Paths;
import net.splitcells.website.server.Validator;
import net.splitcells.website.server.translation.to.html.PathBasedUriResolver;
import net.splitcells.website.server.translation.to.html.XslTransformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Files.newInputStream;

public class FileStructureTransformer {
    /**
     * This is cached, because it takes a lot of time to reinitialize it.
     */
    private static final Validator validator = Validator.validator(Paths.path(System.getProperty("user.home")
            + "/Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/src/main/resources/den.xsd"));

    private final Path fileStructureRoot;
    private final XslTransformer transformer;
    private final Path loggingProject = Paths.path(System.getProperty("user.home")
            + "/connections/tmp.storage/dem");

    @Deprecated
    public static void main(String... args) throws IOException {
        final var articles = java.nio.file.Paths.get(System.getProperty("user.home")
                + "/Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/src/main/xml/net/splitcells/martins/avots/website/articles");
        Files.list(articles).forEach(a -> {
            final var folder = articles.resolve(a.getFileName().toString().split("-")[0])
                    .resolve(a.getFileName().toString().split("-")[1])
                    .resolve(a.getFileName().toString().split("-")[2]);
            Paths.generateFolderPath(folder);
            Paths.copyFileFrom(a, folder.resolve(a.getFileName().toString().substring(11)));
        });
    }

    public FileStructureTransformer(Path supportSystem, Path xslLibs, String transformerXsl) {
        this.fileStructureRoot = supportSystem;
        try {
            transformer = new XslTransformer(
                    newInputStream(xslLibs.resolve(transformerXsl)), new PathBasedUriResolver(xslLibs));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String transform(List<String> path) {
        return transform(Paths.path(fileStructureRoot, path));
    }

    public String transform(Path file) {
        final var t = loggingProject.resolve("net/splitcells/martins/avots/website/log")
                .resolve(fileStructureRoot.relativize(file).getParent());
        Paths.generateFolderPath(t);
        // TODO
        // validator.validate(file).ifPresent(error -> CommonFunctions.appendToFile(t.resolve(file.getFileName() + ".errors.txt"), error));
        return new String(transformer.transform(file));
    }

    public String transform(String content) {
        return transformer.transform(content);
    }
}
