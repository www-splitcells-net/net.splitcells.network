/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website.server.project;

import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.translation.to.html.XslTransformer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.nio.file.Files.newInputStream;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Paths.generateFolderPath;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.utils.CommonFunctions.appendToFile;
import static net.splitcells.website.server.translation.to.html.PathBasedUriResolver.pathBasedUriResolver;

public class FileStructureTransformer {

    public static FileStructureTransformer fileStructureTransformer(Path fileStructureRoot
            , Path xslLibs
            , String transformerXsl
            , SourceValidator sourceValidator
            , Function<String, Optional<String>> config) {
        return new FileStructureTransformer(fileStructureRoot, xslLibs, transformerXsl, sourceValidator, config);
    }

    private final Path fileStructureRoot;
    private final Path loggingProject = Paths.path(System.getProperty("user.home")
            + "/connections/tmp.storage/net.splitcells.dem");
    private final SourceValidator sourceValidator;
    private final Path xslLibs;
    private final String transformerXsl;
    private final Function<String, Optional<String>> config;

    /* TODO REMOVE by 2022
    @Deprecated
    public static void main(String... args) throws IOException {
        final var articles = java.nio.file.Paths.get(System.getProperty("user.home")
                + "/Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/src/main/xml/net/splitcells/martins/avots/website/articles");
        Files.list(articles).forEach(a -> {
            final var folder = articles.resolve(a.getFileName().toString().split("-")[0])
                    .resolve(a.getFileName().toString().split("-")[1])
                    .resolve(a.getFileName().toString().split("-")[2]);
            generateFolderPath(folder);
            Paths.copyFileFrom(a, folder.resolve(a.getFileName().toString().substring(11)));
        });
    }*/

    private FileStructureTransformer(Path fileStructureRoot
            , Path xslLibs
            , String transformerXsl
            , SourceValidator sourceValidator
            , Function<String, Optional<String>> config) {
        this.fileStructureRoot = fileStructureRoot;
        this.sourceValidator = sourceValidator;
        this.xslLibs = xslLibs;
        this.transformerXsl = transformerXsl;
        this.config = config;
    }

    public String transform(List<String> path) {
        return transform(Paths.path(fileStructureRoot, path));
    }

    public String transform(Path file) {

        sourceValidator.validate(file).ifPresent(error -> {
            final var loggingFolder = loggingProject.resolve("src/main/txt")
                    .resolve(fileStructureRoot.relativize(file).getParent());
            generateFolderPath(loggingFolder);
            appendToFile(loggingFolder.resolve(file.getFileName() + ".errors.txt"), error);
            domsole().append(perspective(error, STRING), LogLevel.ERROR);
        });
        return new String(transformer().transform(file));
    }

    /**
     * TODO Create an option, to enable or disable this, for better performance.
     * <p>
     * Creates a new XslTransformer, so that the XSL-files are reloaded
     * during each rendering.
     *
     * @return
     */
    private XslTransformer transformer() {
        try {
            return new XslTransformer
                    (newInputStream(xslLibs.resolve(transformerXsl))
                            , pathBasedUriResolver(xslLibs, p -> Optional.empty()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String transform(String content) {
        return transformer().transform(content);
    }
}
