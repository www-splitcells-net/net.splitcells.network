/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtensionMerger;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.optionalDirectChildElementsByName;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.ContentType.UTF_8;
import static net.splitcells.dem.resource.FileSystemVoid.fileSystemVoid;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.project.FileStructureTransformer.fileStructureTransformer;
import static net.splitcells.website.server.project.renderer.extension.CsvChartProjectRendererExtension.csvChartRenderer;
import static net.splitcells.website.server.project.renderer.extension.CsvProjectRendererExtension.csvRenderer;
import static net.splitcells.website.server.project.renderer.extension.JacocoReportProjectRendererExtension.jacocoReportRenderer;
import static net.splitcells.website.server.project.renderer.extension.JavadocProjectRendererExtension.javadocReportRenderer;
import static net.splitcells.website.server.project.renderer.extension.JavascriptProjectRendererExtension.javascriptRenderer;
import static net.splitcells.website.server.project.renderer.extension.ProjectRendererExtensionMerger.rendererMerger;
import static net.splitcells.website.server.project.renderer.extension.ResourceProjectRendererExtension.resourceRenderer;
import static net.splitcells.website.server.project.renderer.extension.SvgProjectRendererExtension.svgRenderer;
import static net.splitcells.website.server.project.renderer.extension.TextProjectRendererExtension.textExtension;
import static net.splitcells.website.server.project.renderer.extension.XmlProjectRendererExtension.xmlRenderer;
import static net.splitcells.website.server.project.renderer.extension.ZipProjectRendererExtension.zipRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension.commonMarkChangelogEventRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogProjectRendererExtension.commonMarkChangelogRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkProjectRendererExtension.commonMarkExtension;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkReadmeProjectRendererExtension.commonMarkReadmeRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.RootFileProjectRendererExtension.rootFileProjectRendererExtension;
import static net.splitcells.website.server.projects.ProjectsRendererSourceCodeFileSystem.projectsRendererSourceCodeFileSystem;

/**
 * <p>TODO Use resource folder for xml, txt and etc.</p>
 * <p>TODO Move rendering for any type to dedicated extension.</p>
 * <p>TODO Support layout with meta information like title, in order to have a better "Local Path Context".</p>
 * <p>IDEA Create an extension in order to render Java, Python 3 and Shell scripts, so it's easy to inspect
 * and reference it.</p>
 */
public class ProjectRendererI implements ProjectRenderer {
    private static final String MARKER = "198032jrf013jf09j13f13f4290fj2394fj24";
    private static final String PATH_CONTEXT_MARKER = "89efzu89eaz8FC9ZASE89ASDAPF";
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    @Override
    public FileSystemView projectFileSystem() {
        return projectFolder;
    }

    private final FileSystemView projectFolder;
    private final FileSystemView projectSrcFolder;
    private final FileSystemView xslLibs;

    /**
     * TODO It is unclear, for what this is useful.
     * Keep in mind that it makes hard to load files of projects via this variable.
     */
    @Deprecated
    private final FileSystemView resources;
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
    private Optional<ProjectsRenderer> projectsRenderer;

    public static ProjectRendererI projectRendererI(String renderer
            , FileSystemView projectSrcFolder
            , FileSystemView xslLibs
            , FileSystemView resources
            , String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , SourceValidator sourceValidator
            , FileSystemView projectFolder
            , Config config
            , Optional<ProjectsRenderer> projectsRendererArg) {
        return new ProjectRendererI(renderer
                , projectSrcFolder
                , xslLibs
                , resources
                , resourceRootPath
                , typedFolder
                , flatRepository
                , sourceValidator
                , projectFolder
                , config
                , projectsRendererArg);
    }

    private ProjectRendererI(String renderer
            , FileSystemView projectSrcFolder
            , FileSystemView xslLibs
            , FileSystemView resources
            , String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , SourceValidator sourceValidator
            , FileSystemView projectFolder
            , Config config
            , Optional<ProjectsRenderer> projectsRendererArg) {
        if (resourceRootPath.isEmpty()) {
            throw ExecutionException.execException("resourceRootPath is not allowed to be empty. It has to at least be `/`.");
        }
        projectsRenderer = projectsRendererArg;
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
                // .registerExtension(userCommandRenderer()) // TODO This renderer does not work.
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
                .registerExtension(rootFileProjectRendererExtension("LICENSE"))
                .registerExtension(rootFileProjectRendererExtension("NOTICE"))
                .registerExtension(rootFileProjectRendererExtension("DEVELOPMENT"))
                .registerExtension(rootFileProjectRendererExtension("BUILD"))
                .registerExtension(rootFileProjectRendererExtension("CONTRIBUTING"))
                .registerExtension(zipRenderer())
                .registerExtension(svgRenderer());
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
        return transformer.orElseGet(this::createRenderer);
    }

    private FileStructureTransformer createRenderer() {
        final var createdRenderer = fileStructureTransformer(xslLibs
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
                    } else if ("/net/splitcells/website/server/config/menu/detailed.xsl".equals(p)) {
                        return config.detailedXslMenu();
                    } else if ("/net/splitcells/website/server/config/window/menu.xsl".equals(p)) {
                        return config.xslWindowMenu();
                    } else if ("/net/splitcells/website/server/config/css/files.xml".equals(p) &&
                            config.cssFiles().hasElements()) {
                        return Optional.of("<val xmlns=\"http://splitcells.net/den.xsd\">" +
                                config.cssFiles().stream()
                                        .map(css -> "<val>" + css + "</val>")
                                        .reduce("", (a, b) -> a + b) +
                                "</val>");
                    } else if ("/net/splitcells/website/server/config/js/background-files.xml".equals(p) &&
                            config.jsBackgroundFiles().hasElements()) {
                        return Optional.of("<val xmlns=\"http://splitcells.net/den.xsd\">" +
                                config.jsBackgroundFiles().stream()
                                        .map(js -> "<val>" + js + "</val>")
                                        .reduce("", (a, b) -> a + b) +
                                "</val>");
                    } else if ("/net/splitcells/website/server/config/is-server-for-general-public.xml".equals(p)) {
                        return Optional.of("<val xmlns=\"http://splitcells.net/den.xsd\">"
                                + config.isServerForGeneralPublic()
                                + "</val>");
                    }
                    return Optional.empty();
                }
                , projectsRenderer.map(p -> (FileSystemView) projectsRendererSourceCodeFileSystem(p))
                        .orElseGet(() -> fileSystemVoid()));
        if (config.cacheRenderers()) {
            transformer = Optional.of(createdRenderer);
        }
        return createdRenderer;
    }

    @Override
    public Optional<PageMetaData> metaData(String path, ProjectsRenderer projectsRenderer) {
        return renderer.metaData(path, projectsRenderer, this);
    }

    @Override
    public Optional<BinaryMessage> render(String path, ProjectsRenderer projectsRenderer) {
        return addMissingMetaData(renderInternal(path, projectsRenderer));
    }

    @Override
    public Optional<BinaryMessage> sourceCode(String path, ProjectsRenderer projectsRenderer) {
        return renderer.sourceCode(path, projectsRenderer, this);
    }

    /**
     * TODO Create root for each file type, that needs its one processing method.
     */
    private Optional<BinaryMessage> renderInternal(String path, ProjectsRenderer projectsRenderer) {
        try {
            if (path.length() > 0 && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            final var extensionRendering = renderer.renderFile(path, projectsRenderer, this);
            if (extensionRendering.isPresent()) {
                return extensionRendering;
            }
            // TODO Devide rendering function into routing and content type determination.
            // TODO Move this into extensions.
            if (path.endsWith(".png")) {
                return readArtifact(path).map(r -> binaryMessage(r, "image/png"));
            } else if (path.endsWith(".jpg")) {
                return readArtifact(path).map(r -> binaryMessage(r, "image/jpg"));
            } else if (path.endsWith(".css")) {
                return readArtifact(path).map(r -> binaryMessage(r, "text/css"));
            } else if (path.endsWith(".woff")) {
                return readArtifact(path).map(r -> binaryMessage(r, "font/woff"));
            } else if (path.endsWith(".js")) {
                return readArtifact(path).map(r -> binaryMessage(r, "text/javascript"));
            } else if (path.endsWith(".html")) {
                final var html = readSrc("html", path);
                if (html.isPresent()) {
                    return html.map(r -> binaryMessage(r, "text/html"));
                }
                return Optional.empty();
            } else if (path.endsWith(".svg")) {
                final var artifactResult = readArtifact(path)
                        .map(r -> binaryMessage(r, "image/svg+xml"));
                if (artifactResult.isEmpty()) {
                    return readSrc("svg", path)
                            .map(r -> binaryMessage(r, "image/svg+xml"));
                }
                return artifactResult;
            } else if (path.endsWith(".csv")) {
                final var file = resolveSourceFolder(path, "csv");
                if (projectSrcFolder.isFile(file)) {
                    return Optional.of(binaryMessage(Files.readFileAsBytes(file), "text/csv"));
                } else {
                    return Optional.empty();
                }
            } else {
                return readArtifact(path).map(r -> binaryMessage(r, "text/html"));
            }
        } catch (Exception e) {
            throw new RuntimeException("resourceRootPath: " + resourceRootPath, e);
        }
    }

    private Optional<BinaryMessage> addMissingMetaData(Optional<BinaryMessage> result) {
        return result.map(r -> {
            if (ContentType.HTML_TEXT.codeName().equals(r.getFormat())) {
                final var content = new String(r.getContent());
                final var docTypePrefix = "<!DOCTYPE html>";
                if (!content.startsWith(docTypePrefix)) {
                    try {
                        return binaryMessage((docTypePrefix + content).getBytes(UTF_8.codeName())
                                , r.getFormat());
                    } catch (Exception e) {
                        throw execException(e);
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
            throw execException(e);
        }
    }

    @Deprecated
    private Optional<byte[]> renderFile(String path) {
        // TODO REMOVE Split by dot.
        final var splitByDot = path.split("\\.");
        final var suffix = splitByDot[splitByDot.length - 1];
        try {

            final var sourcePath = path.substring(0, path.length() - suffix.length() - 1) + ".xml";
            final var convertedPath = resolveSourceFolder(sourcePath, "xml");
            // System.out.println("Rendering: " + path);
            // System.out.println("Rendering Relative Resource: " + sourcePath);
            // System.out.println("Rendering Absolute Resource: " + absolutePath);
            // TODO HACK Use optional instead of manual file checking.
            if (projectSrcFolder.isFile(convertedPath)) {
                // System.out.println("Rendering: " + path);
                return Optional.of(renderer()
                        .transform(projectSrcFolder.readString(convertedPath))
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
                return Path.of(type).resolve(java.net.URLDecoder.decode(projectPath, "UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return Path.of(java.net.URLDecoder.decode(projectPath, "UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config
            , ProjectsRenderer projectsRenderer) {
        final var content = Xml.rElement(SEW, "article");
        final var htmlBodyContent = Xml.rElement(SEW, "html-body-content");
        htmlBodyContent.appendChild(Xml.textNode(MARKER));
        content.appendChild(htmlBodyContent);
        final var metaElement = elementWithChildren(SEW, "meta");
        content.appendChild(metaElement);
        if (title.isPresent()) {
            final var titleElement = elementWithChildren(SEW, "title");
            metaElement.appendChild(titleElement);
            // TODO This is an heuristic.
            if (title.get().startsWith("<p>")) {
                titleElement.appendChild(titleElement.getOwnerDocument().importNode(Xml.parse(title.get()).getFirstChild(), true));
            } else {
                titleElement.appendChild(Xml.textNode(title.get()));
            }
        }
        if (path.isPresent()) {
            final var pathElement = elementWithChildren(SEW, "path");
            pathElement.appendChild(Xml.textNode(path.get()));
            metaElement.appendChild(pathElement);
        }
        if (path.isPresent()) {
            final var relevantParentPages = projectsRenderer.relevantParentPage(path.get());
            if (relevantParentPages.hasElements()) {

                final var container = optionalDirectChildElementsByName(metaElement, "relevant-parent-pages", SEW)
                        .orElseGet(() -> {
                            final var element = elementWithChildren(SEW, "relevant-parent-pages");
                            metaElement.appendChild(element);
                            return element;
                        });
                relevantParentPages.forEach(m -> {
                    final var parent = elementWithChildren(SEW, "parent");
                    parent.setAttribute("path", m.path());
                    parent.setAttribute("title", m.title()
                            .or(() -> m.indexedFolderName())
                            .or(() -> {
                                if (m.path().contains("/")) {
                                    return Optional.of(m.path());
                                } else {
                                    return Optional.of(config.sitesName());
                                }
                            })
                            .orElse(path.get()));
                    container.appendChild(parent);
                });
                metaElement.appendChild(container);
            }
        }
        final var contentAsString = Xml.toPrettyString(content);
        logs().append(tree(contentAsString), LogLevel.DEBUG);
        try {
            return Optional.of(renderer()
                    .transform(contentAsString)
                    .replace(MARKER, bodyContent)
                    .getBytes(UTF_8.codeName()));
        } catch (Exception e) {
            throw execException(e);
        }
    }

    @Override
    public Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, Config config) {
        // TODO HACK
        if (xml.startsWith(XML_HEADER)) {
            xml = xml.substring(XML_HEADER.length());
        }
        final var layoutConfigElement = Xml.rElement(SEW, "layout.config");
        {
            final var pathElement = elementWithChildren(SEW, "path");
            pathElement.appendChild(Xml.textNode(layoutConfig.path()));
            layoutConfigElement.appendChild(pathElement);
        }
        layoutConfig.title().ifPresent(title -> {
            final var titleElement = elementWithChildren(SEW, "title");
            titleElement.appendChild(Xml.textNode(layoutConfig.title().get()));
            layoutConfigElement.appendChild(titleElement);
        });
        {
            final var contentElement = elementWithChildren(SEW, "content");
            contentElement.appendChild(Xml.textNode(MARKER));
            layoutConfigElement.appendChild(contentElement);
        }
        layoutConfig.localPathContext().ifPresent(localPathContext -> {
            final var pathContext = elementWithChildren(SEW, "path.context");
            pathContext.appendChild(Xml.textNode(PATH_CONTEXT_MARKER));
            layoutConfigElement.appendChild(pathContext);
        });
        try {
            var xmlToRender = Xml.toPrettyString(layoutConfigElement).replace(MARKER, xml);
            final var localPathContext = layoutConfig.localPathContext();
            if (localPathContext.isPresent()) {
                xmlToRender = xmlToRender.replace(PATH_CONTEXT_MARKER, localPathContext.get().toHtmlString());
            }
            return Optional.of(renderer()
                    .transform(xmlToRender)
                    .getBytes(UTF_8.codeName()));
        } catch (Exception e) {
            throw execException(e);
        }
    }

    @Override
    public Optional<byte[]> renderRawXml(String xml, Config config) {
        try {
            return Optional.of(renderer()
                    .transform(xml)
                    .getBytes(UTF_8.codeName()));
        } catch (Exception e) {
            throw execException(e);
        }
    }

    private Optional<byte[]> renderTextFile(String path) {
        try {
            final var convertedPath = resolveSourceFolder(path, "txt");
            if (projectSrcFolder.isFile(convertedPath)) {
                final var content = Xml.rElement(NameSpaces.NATURAL, "text");
                content.appendChild(Xml.textNode(projectSrcFolder.readString(convertedPath)));
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
        final var resourcePath = Path.of(srcType).resolve(path);
        if (!projectSrcFolder.isFile(resourcePath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(projectSrcFolder.readFileAsBytes(resourcePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<byte[]> readArtifact(String path) {
        final var resourcePath = Path.of(path);
        if (!resources.isFile(resourcePath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(resources.readFileAsBytes(resourcePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String resourceRootPath() {
        return resourceRootPath;
    }

    @Deprecated
    private static void extendProjectLayout(Tree layout, Path folder, boolean replaceFileSuffix) {
        if (Files.isDirectory(folder)) {
            try {
                Files.walkRecursively(folder)
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
        if (projectSrcFolder.isDirectory(Path.of("html"))) {
            projectSrcFolder.subFileSystemView("html")
                    .walkRecursively()
                    .filter(projectSrcFolder::isFile)
                    .forEach(projectPaths::add);
        }
        if (projectSrcFolder.isDirectory(Path.of("svg"))) {
            projectSrcFolder.subFileSystemView("svg")
                    .walkRecursively()
                    .filter(projectSrcFolder::isFile)
                    .forEach(projectPaths::add);
        }
        if (projectSrcFolder.isDirectory(Path.of("txt"))) {
            projectSrcFolder.subFileSystemView("txt")
                    .walkRecursively()
                    .map(p -> {
                        if (p.getParent() == null) {
                            return Path.of(p.getFileName().toString() + ".html");
                        }
                        return p.getParent()
                                .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                        (p.getFileName().toString() + ".html"));
                    })
                    .filter(projectSrcFolder::isFile)
                    .forEach(projectPaths::add);
        }
        projectPaths.addAll(renderer.projectPaths(this));
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths() {
        return this.renderer.relevantProjectPaths(this);
    }

    @Override
    public Optional<BinaryMessage> render(String path) {
        throw notImplementedYet();
    }

    @Override public String toString() {
        return getClass().getName()
                + " for the file system "
                + projectFileSystem().toString()
                + " and resource path "
                + resourceRootPath
                + " and object id "
                + super.toString();
    }
}
