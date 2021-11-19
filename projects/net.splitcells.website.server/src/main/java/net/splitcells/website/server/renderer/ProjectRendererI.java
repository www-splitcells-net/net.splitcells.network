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
package net.splitcells.website.server.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.website.Validator;
import net.splitcells.website.server.renderer.extension.RendererMerger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static java.util.stream.Stream.concat;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.website.server.renderer.extension.RendererMerger.rendererMerger;
import static net.splitcells.website.server.renderer.extension.ResourceExtension.resourceExtension;
import static net.splitcells.website.server.renderer.extension.TextExtension.textExtension;
import static net.splitcells.website.server.renderer.extension.UserCommandExtension.userCommandExtension;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;
import static net.splitcells.website.server.renderer.extension.XmlExtension.xmlExtension;
import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkChangelogExtension.commonMarkChangelogExtension;
import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkExtension.commonMarkExtension;
import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkReadmeExtension.commonMarkReadmeExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * TODO Use resource folder for xml, txt and etc.
 * <p>
 * TODO Merge website rendering and support system rendering.
 * <p>
 * TODO Make XML rendering an extension.
 * <p>
 * TODO Move rendering for any type to dedicated extension.
 * <p>
 * IDEA Create an extension in order to render Java, Python 3 and Shell scripts, so it's easy to inspect
 * and reference it.
 */
public class ProjectRendererI implements ProjectRenderer {
    private static final String MARKER = "198032jrf013jf09j13f13f4290fj2394fj24";

    @Override
    public Path projectFolder() {
        return projectFolder;
    }

    private final Path projectFolder;
    private final Path projectSrcFolder;
    private final Path xslLibs;
    private final Path resources;
    private final String resourceRootPath;
    private final boolean flatRepository;
    private final String profile;
    /**
     * TODO REMOVE This is always true.
     */
    @Deprecated
    private final boolean typedFolder;
    private final Validator validator;
    private final RendererMerger renderer = rendererMerger();

    {
        renderer.registerExtension(commonMarkReadmeExtension());
        renderer.registerExtension(commonMarkChangelogExtension());
        renderer.registerExtension(userCommandExtension());
        renderer.registerExtension(commonMarkExtension());
    }

    protected ProjectRendererI(String renderer, Path projectSrcFolder, Path xslLibs, Path resources, String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , Validator validator
            , Path projectFolder) {
        this.typedFolder = typedFolder;
        this.profile = renderer;
        this.projectSrcFolder = projectSrcFolder;
        this.xslLibs = xslLibs;
        this.resources = resources;
        this.resourceRootPath = resourceRootPath;
        this.flatRepository = flatRepository;
        this.validator = validator;
        this.projectFolder = projectFolder;
        this.renderer.registerExtension(xmlExtension(renderer()));
        this.renderer.registerExtension(textExtension(renderer()));
        this.renderer.registerExtension(resourceExtension());
    }

    /**
     * A new renderer is created each time, in order to use updated XSL transformations.
     * <p>
     * IDEA Create mode where the renderer ist cached.
     */
    private FileStructureTransformer renderer() {
        return new FileStructureTransformer(projectSrcFolder.resolve("xml")
                , xslLibs
                , "main." + profile + ".xsl"
                , validator);
    }

    /**
     * TODO Create root for each file type, that needs its one processing method.
     */
    @Override
    public Optional<RenderingResult> render(String path) {
        try {
            if (path.length() > 0 && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            final var extensionRendering = renderer.renderFile(path, this);
            if (extensionRendering.isPresent()) {
                return extensionRendering;
            }
            // TODO Do not use path, in the following code.
            final var normalizedPath = path;
            // TODO Devide rendering function into routing and content type determination.
            if (path.endsWith(".png")) {
                return readArtifact(path).map(r -> renderingResult(r, "image/png"));
            } else if (path.endsWith(".jpg")) {
                return readArtifact(path).map(r -> renderingResult(r, "image/jpg"));
            } else if (path.endsWith(".css")) {
                return readArtifact(path).map(r -> renderingResult(r, "image/css"));
            } else if (path.endsWith(".js")) {
                return readArtifact(path).map(r -> renderingResult(r, "text/javascript"));
            } else if (path.endsWith(".html")) {
                final var html = readSrc("html", path);
                if (html.isPresent()) {
                    return html.map(r -> renderingResult(r, "text/html"));
                }
                return Optional.empty();
            } else if (path.endsWith(".svg")) {
                final var artifactResult = readArtifact(path)
                        .map(r -> renderingResult(r, "image/svg+xml"));
                if (artifactResult.isEmpty()) {
                    return readSrc("svg", path)
                            .map(r -> renderingResult(r, "image/svg+xml"));
                }
                return artifactResult;
            } else if (path.endsWith(".csv")) {
                final var file = resolveSourceFolder(path, "csv");
                if (java.nio.file.Files.exists(file)) {
                    return Optional.of(renderingResult(java.nio.file.Files.readAllBytes(file), "text/csv"));
                } else {
                    return Optional.empty();
                }
            } else {
                return readArtifact(path).map(r -> renderingResult(r, TEXT_HTML.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(resourceRootPath, e);
        }
    }

    /**
     * TODO The handling of the transformer seems to be too complicated.
     *
     * @param arg
     * @return
     */
    @Override
    public Optional<byte[]> renderString(String arg) {
        return Optional.of(renderer()
                .transform(arg)
                .getBytes(UTF_8));
    }

    private Optional<byte[]> renderFile(String path) {
        // TODO REMOVE Split by dot.
        final var splitByDot = path.split("\\.");
        final var suffix = splitByDot[splitByDot.length - 1];
        try {

            final var sourcePath = path.substring(0, path.length() - suffix.length() - 1) + ".xml";
            final var absolutePath = resolveSourceFolder(sourcePath, "xml");
            // System.out.println("Rendering: " + path);
            // System.out.println("Rendering Relative Resource: " + sourcePath);
            // System.out.println("Rendering Absolute Resource: " + absolutePath);
            // TODO HACK Use optional instead of manual file checking.
            if (java.nio.file.Files.isRegularFile(absolutePath)) {
                // System.out.println("Rendering: " + path);
                return Optional.of(renderer()
                        .transform(absolutePath)
                        .getBytes(UTF_8));
            }
            // System.out.println("Reading artifact: " + path);
            return readArtifact(path);
        } catch (Exception e) {
            // System.out.println(path);
            throw new RuntimeException(e);
        }
    }

    private Path resolveSourceFolder(String projectPath, String type) {
        if (flatRepository) {
            projectPath = projectPath.replace(resourceRootPath.substring(1), "");
        }
        if (typedFolder) {
            try {
                return projectSrcFolder.resolve(type).resolve(URLDecoder.decode(projectPath, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return projectSrcFolder.resolve(URLDecoder.decode(projectPath, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path) {
        try {
            final var content = Xml.rElement(NameSpaces.SEW, "article");
            final var htmlBodyContent = Xml.rElement(NameSpaces.SEW, "html-body-content");
            htmlBodyContent.appendChild
                    (Xml.textNode(MARKER));
            content.appendChild(htmlBodyContent);
            if (title.isPresent()) {
                final var metaElement = Xml.elementWithChildren(NameSpaces.SEW, "meta");
                final var titleElement = Xml.elementWithChildren(NameSpaces.SEW, "title");
                metaElement.appendChild(titleElement);
                titleElement.appendChild(Xml.textNode(title.get()));
                content.appendChild(metaElement);
                if (path.isPresent()) {
                    final var pathElement = Xml.elementWithChildren(NameSpaces.SEW, "path");
                    pathElement.appendChild(Xml.textNode(Paths.get(path.get()).getParent().toString()));
                    metaElement.appendChild(pathElement);
                }
            }
            final var contentAsString = Xml.toPrettyString(content);
            domsole().append(perspective(contentAsString), LogLevel.INFO);
            return Optional.of(renderer()
                    .transform(contentAsString)
                    .replace(MARKER, bodyContent)
                    .getBytes(UTF_8));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<byte[]> renderTextFile(String path) {
        try {
            final var absolutePath = resolveSourceFolder(path, "txt");
            if (java.nio.file.Files.isRegularFile(absolutePath)) {
                final var content = Xml.rElement(NameSpaces.NATURAL, "text");
                content.appendChild
                        (Xml.textNode
                                (new String
                                        (readAllBytes
                                                (absolutePath))));
                return Optional.of(renderer()
                        .transform(Xml.toPrettyString(content))
                        .getBytes(UTF_8));
            }
            return readArtifact(path);
        } catch (Exception e) {
            return readArtifact(path);
        }
    }

    private Optional<byte[]> readSrc(String srcType, String path) {
        final var resourcePath = this.projectSrcFolder.resolve(srcType).resolve(path);
        if (!is_file(resourcePath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(readAllBytes(resourcePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<byte[]> readArtifact(String path) {
        final var resourcePath = resources.resolve(path);
        if (!is_file(resourcePath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(readAllBytes(resourcePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String resourceRootPath() {
        return resourceRootPath;
    }

    private static void extendProjectLayout(Perspective layout, Path folder, boolean replaceFileSuffix) {
        if (isDirectory(folder)) {
            try {
                java.nio.file.Files.walk(folder)
                        .filter(java.nio.file.Files::isRegularFile)
                        .forEach(file -> {
                            final var relativePath = folder.relativize(file);
                            if (replaceFileSuffix) {
                                final var fileName = relativePath.getFileName().toString();
                                final var newFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".html";
                                final var adjustedRelativePath = relativePath.getParent().resolve(newFileName);
                                LayoutUtils.extendPerspectiveWithPath(layout, adjustedRelativePath);
                            } else {
                                LayoutUtils.extendPerspectiveWithPath(layout, relativePath);
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(folder.toAbsolutePath().toString(), e);
            }
        }
    }

    @Override
    public Set<Path> projectPaths() {
        final Set<Path> projectPaths = setOfUniques();
        {
            final var renderedDocumentPaths = list
                    (projectSrcFolder.resolve("txt"))
                    .stream()
                    .filter(folder -> Files.isDirectory(folder))
                    .map(folder -> {
                        try {
                            return java.nio.file.Files.walk(folder)
                                    .filter(java.nio.file.Files::isRegularFile)
                                    .map(file -> {
                                        return folder.relativize(
                                                file.getParent()
                                                        .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                                                (file.getFileName().toString() + ".html"))
                                        );
                                    });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).reduce((a, b) -> concat(a, b));
            if (renderedDocumentPaths.isPresent()) {
                renderedDocumentPaths.get().forEach(projectPaths::add);
            }
        }
        {
            final var resourcePaths = list
                    (resources
                            , projectSrcFolder.resolve("html")
                            , projectSrcFolder.resolve("svg"))
                    .stream()
                    .filter(folder -> Files.isDirectory(folder))
                    .map(folder -> {
                        try {
                            return java.nio.file.Files.walk(folder)
                                    .filter(java.nio.file.Files::isRegularFile)
                                    .map(file -> folder.relativize(file));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).reduce((a, b) -> concat(a, b));

            if (resourcePaths.isPresent()) {
                resourcePaths.get().forEach(projectPaths::add);
            }
        }
        projectPaths.addAll(renderer.projectPaths(this));
        return projectPaths;
    }

}
