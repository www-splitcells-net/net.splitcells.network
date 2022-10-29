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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtensionMerger;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.ContentType.UTF_8;
import static net.splitcells.dem.resource.Files.fileExists;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Files.readFileAsBytes;
import static net.splitcells.dem.resource.Files.readFileAsString;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.StreamUtils.concat;
import static net.splitcells.website.server.project.FileStructureTransformer.fileStructureTransformer;
import static net.splitcells.website.server.project.renderer.extension.CsvChartProjectRendererExtension.csvChartRenderer;
import static net.splitcells.website.server.project.renderer.extension.CsvProjectRendererExtension.csvRenderer;
import static net.splitcells.website.server.project.renderer.extension.JacocoReportProjectRendererExtension.jacocoReportRenderer;
import static net.splitcells.website.server.project.renderer.extension.JavadocProjectRendererExtension.javadocReportRenderer;
import static net.splitcells.website.server.project.renderer.extension.JavascriptProjectRendererExtension.javascriptRenderer;
import static net.splitcells.website.server.project.renderer.extension.ProjectRendererExtensionMerger.rendererMerger;
import static net.splitcells.website.server.project.renderer.extension.ResourceProjectRendererExtension.resourceRenderer;
import static net.splitcells.website.server.project.renderer.extension.TextProjectRendererExtension.textExtension;
import static net.splitcells.website.server.project.renderer.extension.UserCommandProjectRendererExtension.userCommandRenderer;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.renderer.extension.XmlProjectRendererExtension.xmlRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkBuildProjectRendererExtension.commonMarkBuildRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension.commonMarkChangelogEventRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogProjectRendererExtension.commonMarkChangelogRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkContributingProjectRendererExtension.commonMarkContributingRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkDevelopmentProjectRendererExtension.commonMarkDevelopmentRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkProjectRendererExtension.commonMarkExtension;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkReadmeProjectRendererExtension.commonMarkReadmeRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.RootFileProjectRendererExtension.rootFileProjectRendererExtension;

/**
 * <p>TODO Use resource folder for xml, txt and etc.</p>
 * <p>TODO Move rendering for any type to dedicated extension.</p>
 * <p>TODO Support layout with meta information like title, in order to have a better "Local Path Context".</p>
 * <p>IDEA Create an extension in order to render Java, Python 3 and Shell scripts, so it's easy to inspect
 * and reference it.</p>
 */
public class ProjectRendererI implements ProjectRenderer {
    private static final String MARKER = "198032jrf013jf09j13f13f4290fj2394fj24";
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

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
    private final SourceValidator sourceValidator;
    private final ProjectRendererExtensionMerger renderer = rendererMerger();
    private final Config config;
    private Optional<FileStructureTransformer> transformer = Optional.empty();

    protected ProjectRendererI(String renderer, Path projectSrcFolder, Path xslLibs, Path resources, String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , SourceValidator sourceValidator
            , Path projectFolder
            , Config config) {
        if (resourceRootPath.isEmpty()) {
            throw executionException("resourceRootPath is not allowed to be empty. It has to at least be `/`.");
        }
        this.typedFolder = typedFolder;
        this.profile = renderer;
        this.projectSrcFolder = projectSrcFolder;
        this.xslLibs = xslLibs;
        this.resources = resources;
        this.resourceRootPath = resourceRootPath;
        this.flatRepository = flatRepository;
        this.sourceValidator = sourceValidator;
        this.projectFolder = projectFolder;
        this.config = config;
        // TODO MOVE
        this.renderer.registerExtension(commonMarkReadmeRenderer())
                .registerExtension(commonMarkChangelogRenderer())
                .registerExtension(userCommandRenderer())
                .registerExtension(commonMarkExtension())
                .registerExtension(csvRenderer())
                .registerExtension(jacocoReportRenderer())
                .registerExtension(commonMarkChangelogEventRenderer())
                .registerExtension(javascriptRenderer())
                .registerExtension(javadocReportRenderer())
                .registerExtension(xmlRenderer())
                .registerExtension(textExtension())
                .registerExtension(resourceRenderer())
                .registerExtension(csvChartRenderer())
                .registerExtension(commonMarkContributingRenderer())
                .registerExtension(commonMarkDevelopmentRenderer())
                .registerExtension(commonMarkBuildRenderer())
                .registerExtension(rootFileProjectRendererExtension("LICENSE"))
                .registerExtension(rootFileProjectRendererExtension("NOTICE"));
        if (config.cacheRenderers()) {
            transformer = Optional.of(createRenderer());
        }
    }

    /**
     * A new renderer is created each time, in order to use updated XSL transformations.
     * <p>
     * IDEA Create mode where the renderer ist cached.
     */
    private FileStructureTransformer renderer() {
        if (transformer.isEmpty()) {
            return createRenderer();
        }
        return createRenderer();
    }

    private FileStructureTransformer createRenderer() {
        return fileStructureTransformer(projectSrcFolder.resolve("xml")
                , xslLibs
                , "main." + profile + ".xsl"
                , sourceValidator
                , p -> {
                    if ("/net/splitcells/website/server/config/layout.xml".equals(p)) {
                        return config.layout();
                    } else if ("/net/splitcells/website/server/config/generation.style.xml".equals(p)) {
                        return Optional
                                .of("<val xmlns=\"http://splitcells.net/den.xsd\">"
                                        + config.generationStyle()
                                        + "</val>");
                    } else if ("/net/splitcells/website/server/config/site.folder.xml".equals(p)) {
                        return Optional
                                .of("<val xmlns=\"http://splitcells.net/den.xsd\">"
                                        + config.siteFolder()
                                        + "</val>");
                    } else if ("/net/splitcells/website/server/config/layout.relevant.xml".equals(p)) {
                        return config.layoutRelevant();
                    } else if ("/net/splitcells/website/server/config/root.path.xml".equals(p)) {
                        return Optional
                                .of("<val xmlns=\"http://splitcells.net/den.xsd\">"
                                        + config.rootPath()
                                        + "</val>");
                    }
                    return Optional.empty();
                });
    }

    @Override
    public Optional<RenderingResult> render(String path) {
        return addMissingMetaData(renderInternal(path));
    }

    /**
     * TODO Create root for each file type, that needs its one processing method.
     */
    private Optional<RenderingResult> renderInternal(String path) {
        try {
            if (path.length() > 0 && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            final var extensionRendering = renderer.renderFile(path, this, config);
            if (extensionRendering.isPresent()) {
                return extensionRendering;
            }
            // TODO Do not use path, in the following code.
            final var normalizedPath = path;
            // TODO Devide rendering function into routing and content type determination.
            // TODO Move this into extensions.
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
                if (fileExists(file)) {
                    return Optional.of(renderingResult(Files.readFileAsBytes(file), "text/csv"));
                } else {
                    return Optional.empty();
                }
            } else {
                return readArtifact(path).map(r -> renderingResult(r, "text/html"));
            }
        } catch (Exception e) {
            throw new RuntimeException("resourceRootPath: " + resourceRootPath, e);
        }
    }

    private Optional<RenderingResult> addMissingMetaData(Optional<RenderingResult> result) {
        return result.map(r -> {
            if (ContentType.HTML_TEXT.codeName().equals(r.getFormat())) {
                final var content = new String(r.getContent());
                final var docTypePrefix = "<!DOCTYPE html>";
                if (!content.startsWith(docTypePrefix)) {
                    try {
                        return renderingResult((docTypePrefix + content).getBytes(UTF_8.codeName())
                                , r.getFormat());
                    } catch (Exception e) {
                        throw executionException(e);
                    }
                }
            }
            return r;
        });
    }

    /**
     * TODO The handling of the transformer seems to be too complicated.
     *
     * @param arg
     * @return
     */
    @Override
    public Optional<byte[]> renderString(String arg) {
        try {
            return Optional.of(renderer()
                    .transform(arg)
                    .getBytes(UTF_8.codeName()));
        } catch (Exception e) {
            throw executionException(e);
        }
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
            if (fileExists(absolutePath)) {
                // System.out.println("Rendering: " + path);
                return Optional.of(renderer()
                        .transform(absolutePath)
                        .getBytes(UTF_8.codeName()));
            }
            // System.out.println("Reading artifact: " + path);
            return readArtifact(path);
        } catch (Exception e) {
            // System.out.println(path);
            throw new RuntimeException(e);
        }
    }


    /**
     * TODO Is decoding necessary?
     *
     * @param projectPath projectPath
     * @param type        type
     * @return return
     */
    @JavaLegacyBody
    private Path resolveSourceFolder(String projectPath, String type) {
        if (flatRepository) {
            projectPath = projectPath.replace(resourceRootPath.substring(1), "");
        }
        if (typedFolder) {
            try {
                return projectSrcFolder.resolve(type).resolve(java.net.URLDecoder.decode(projectPath, "UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return projectSrcFolder.resolve(java.net.URLDecoder.decode(projectPath, "UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config) {
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
                pathElement.appendChild(Xml.textNode(path.get().toString()));
                metaElement.appendChild(pathElement);
            }
        }
        final var contentAsString = Xml.toPrettyString(content);
        domsole().append(perspective(contentAsString), LogLevel.DEBUG);
        try {
            return Optional.of(renderer()
                    .transform(contentAsString)
                    .replace(MARKER, bodyContent)
                    .getBytes(UTF_8.codeName()));
        } catch (Exception e) {
            throw executionException(e);
        }
    }

    @Override
    public Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, Config config) {
        // TODO HACK
        if (xml.startsWith(XML_HEADER)) {
            xml = xml.substring(XML_HEADER.length());
        }
        final var layoutConfigElement = Xml.rElement(NameSpaces.SEW, "layout.config");
        {
            final var pathElement = Xml.elementWithChildren(NameSpaces.SEW, "path");
            pathElement.appendChild(Xml.textNode(layoutConfig.path()));
            layoutConfigElement.appendChild(pathElement);
        }
        if (layoutConfig.title().isPresent()) {
            final var titleElement = Xml.elementWithChildren(NameSpaces.SEW, "title");
            titleElement.appendChild(Xml.textNode(layoutConfig.title().get()));
            layoutConfigElement.appendChild(titleElement);
        }
        {
            final var contentElement = Xml.elementWithChildren(NameSpaces.SEW, "content");
            contentElement.appendChild(Xml.textNode(MARKER));
            layoutConfigElement.appendChild(contentElement);
        }
        try {
            return Optional.of(renderer()
                    .transform(Xml.toPrettyString(layoutConfigElement)
                            .replace(MARKER, xml))
                    .getBytes(UTF_8.codeName()));
        } catch (Exception e) {
            throw executionException(e);
        }
    }

    @Override
    public Optional<byte[]> renderRawXml(String xml, Config config) {
        try {
            return Optional.of(renderer()
                    .transform(xml)
                    .getBytes(UTF_8.codeName()));
        } catch (Exception e) {
            throw executionException(e);
        }
    }

    private Optional<byte[]> renderTextFile(String path) {
        try {
            final var absolutePath = resolveSourceFolder(path, "txt");
            if (fileExists(absolutePath)) {
                final var content = Xml.rElement(NameSpaces.NATURAL, "text");
                content.appendChild
                        (Xml.textNode
                                (readFileAsString(absolutePath)));
                return Optional.of(renderer()
                        .transform(Xml.toPrettyString(content))
                        .getBytes(UTF_8.codeName()));
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
            return Optional.of(readFileAsBytes(resourcePath));
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
            return Optional.of(readFileAsBytes(resourcePath));
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
                Files.walk_recursively(folder)
                        .filter(Files::fileExists)
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
            } catch (Exception e) {
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
                            return Files.walk_recursively(folder)
                                    .filter(Files::fileExists)
                                    .map(file -> {
                                        return folder.relativize(
                                                file.getParent()
                                                        .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                                                (file.getFileName().toString() + ".html"))
                                        );
                                    });
                        } catch (Exception e) {
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
                            return Files.walk_recursively(folder)
                                    .filter(Files::fileExists)
                                    .map(file -> folder.relativize(file));
                        } catch (Exception e) {
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

    @Override
    public Set<Path> relevantProjectPaths() {
        return this.renderer.relevantProjectPaths(this);
    }

}
