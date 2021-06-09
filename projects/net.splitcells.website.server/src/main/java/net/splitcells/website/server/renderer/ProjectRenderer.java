package net.splitcells.website.server.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.Files;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.website.Validator;
import net.splitcells.website.server.renderer.commonmark.CommonMarkRenderer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static java.util.stream.Stream.concat;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.host.Files.isDirectory;
import static net.splitcells.dem.resource.host.Files.is_file;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.website.server.renderer.commonmark.CommonMarkExtension.commonMarkExtension;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;
import static net.splitcells.website.server.renderer.commonmark.CommonMarkRenderer.commonMarkRenderer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * TODO Use resource folder for xml, txt and etc.
 * <p>
 * TODO Merge website rendering and support system rendering. Make distinction between online and offline rendering.
 */
public class ProjectRenderer {
    private static final String MARKER = "198032jrf013jf09j13f13f4290fj2394fj24";

    public Path project() {
        return projectSrcFolder;
    }

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
    private final boolean typedFolder;
    private final Validator validator;
    private final ProjectRendererExtension extension = commonMarkExtension();
    private final Map<String, String> parameters;

    private final CommonMarkRenderer commonMarkRenderer = commonMarkRenderer();

    @Deprecated
    public ProjectRenderer(String renderer, Path projectSrcFolder, Path xslLibs, Path resources, String resourceRootPath
            , Validator validator) {
        this(renderer, projectSrcFolder, xslLibs, resources, resourceRootPath, true, false
                , validator
                , projectSrcFolder.resolve("../.."));
    }

    public static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , Validator validator) {
        return new ProjectRenderer(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , validator
                , projectFolder);
    }

    public static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , Validator validator
            , Map<String, String> parameters) {
        return new ProjectRenderer(renderer, projectFolder, xslLibs, resources, resourceRootPath
                , true
                , false
                , validator
                , projectFolder
                , parameters);
    }

    public static ProjectRenderer projectRenderer(String renderer, Path projectSrcFolder, Path xslLibs, Path resources, String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , Validator validator
            , Map<String, String> parameters) {
        return new ProjectRenderer(renderer, projectSrcFolder, xslLibs, resources, resourceRootPath, typedFolder, flatRepository, validator
                , projectSrcFolder.resolve("../..")
                , parameters);
    }

    @Deprecated
    public ProjectRenderer(String renderer, Path projectSrcFolder, Path xslLibs, Path resources, String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , Validator validator) {
        this(renderer, projectSrcFolder, xslLibs, resources, resourceRootPath, typedFolder, flatRepository, validator
                , projectSrcFolder.resolve("../.."));
    }

    public ProjectRenderer(String renderer, Path projectSrcFolder, Path xslLibs, Path resources, String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , Validator validator
            , Path projectFolder) {
        this(renderer
                , projectSrcFolder
                , xslLibs
                , resources
                , resourceRootPath
                , typedFolder
                , flatRepository
                , validator
                , projectFolder
                , map());
    }

    private ProjectRenderer(String renderer, Path projectSrcFolder, Path xslLibs, Path resources, String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , Validator validator
            , Path projectFolder
            , Map<String, String> parameters) {
        this.typedFolder = typedFolder;
        this.profile = renderer;
        this.projectSrcFolder = projectSrcFolder;
        this.xslLibs = xslLibs;
        this.resources = resources;
        this.resourceRootPath = resourceRootPath;
        this.flatRepository = flatRepository;
        this.validator = validator;
        this.projectFolder = projectFolder;
        this.parameters = parameters;
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
                , validator
                , map());
    }

    /**
     * TODO Create root for each file type, that needs its one processing method.
     */
    public Optional<RenderingResult> render(String path) {
        try {
            final var extensionRendering = extension.renderFile(path, this);
            if (extensionRendering.isPresent()) {
                return extensionRendering;
            }
            if (path.length() > 0 && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            // TODO Devide rendering function into routing and content type determination.
            if (path.endsWith(".txt")) {
                return renderTextFile(path).map(r -> renderingResult(r, TEXT_HTML.toString()));
            } else if (path.endsWith(".png")) {
                return readArtifact(path).map(r -> renderingResult(r, "image/png"));
            } else if (path.endsWith(".jpg")) {
                return readArtifact(path).map(r -> renderingResult(r, "image/jpg"));
            } else if (path.endsWith(".css")) {
                return readArtifact(path).map(r -> renderingResult(r, "image/css"));
            } else if (path.endsWith(".js")) {
                return readArtifact(path).map(r -> renderingResult(r, "text/javascript"));
            } else if (path.endsWith(".html")) {
                final var renderedFile = renderFile(path);
                final var commonMarkSrc = readSrc("md", path.substring(0, path.lastIndexOf(".html")) + ".md");
                assertThat(renderedFile.isPresent() && commonMarkSrc.isPresent()).isFalse();
                if (commonMarkSrc.isPresent()) {
                    return commonMarkSrc
                            .map(r -> commonMarkRenderer.render(new String(r)).getBytes(UTF_8))
                            .map(r -> renderingResult(r, TEXT_HTML.toString()));
                }
                if (renderedFile.isPresent()) {
                    return renderedFile.map(r -> renderingResult(r, TEXT_HTML.toString()));
                }
                return Optional.empty();
            } else if (path.endsWith(".xml") || path.endsWith(".rss")) {
                return renderFile(path).map(r -> renderingResult(r, TEXT_HTML.toString()));
            } else if (path.endsWith(".svg")) {
                return readArtifact(path).map(r -> renderingResult(r, "image/svg+xml"));
            } else {
                return readArtifact(path).map(r -> renderingResult(r, TEXT_HTML.toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException(resourceRootPath, e);
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
            if (java.nio.file.Files.isRegularFile(absolutePath)) {
                // System.out.println("Rendering: " + path);
                return Optional.of(renderer()
                        .transform(absolutePath)
                        .getBytes(Charset.forName("UTF-8")));
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

    public Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title) {
        try {
            final var content = Xml.rElement(NameSpaces.SEW, "article");
            final var htmlBodyContent = Xml.rElement(NameSpaces.SEW, "html-body-content");
            htmlBodyContent.appendChild
                    (Xml.textNode(MARKER));
            content.appendChild(htmlBodyContent);
            domsole().append(content, LogLevel.INFO);
            if (title.isPresent()) {
                final var metaElement = Xml.elementWithChildren(NameSpaces.SEW, "meta");
                final var titleElement = Xml.elementWithChildren(NameSpaces.SEW, "title");
                metaElement.appendChild(titleElement);
                titleElement.appendChild(Xml.textNode(title.get()));
                content.appendChild(metaElement);
            }
            return Optional.of(renderer()
                    .transform(Xml.toPrettyString(content))
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

    public String resourceRootPath() {
        return resourceRootPath;
    }

    public Perspective projectLayout() {
        final var layout = perspective(NameSpaces.VAL, NameSpaces.NATURAL);
        final Path folder;
        if (typedFolder) {
            folder = projectSrcFolder.resolve("xml");
        } else {
            folder = projectSrcFolder;
        }
        try {
            if (isDirectory(folder)) {
                java.nio.file.Files.walk(folder)
                        .filter(java.nio.file.Files::isRegularFile)
                        .forEach(file -> extendPerspectiveWithPath(layout, folder.relativize(file)));
            }
        } catch (IOException e) {
            throw new RuntimeException(folder.toAbsolutePath().toString(), e);
        }
        return extension.extendProjectLayout(layout, this);
    }

    public Set<Path> projectPaths() {
        final var projectPaths = list(projectSrcFolder.resolve("xml"), projectSrcFolder.resolve("txt"), resources)
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
                }).reduce((a, b) -> concat(a, b))
                .get()
                .collect(toSetOfUniques());
        if (is_file(projectFolder.resolve("README.md"))) {
            projectPaths.add(Path.of(resourceRootPath.substring(1)).resolve("README.html"));
        }
        return projectPaths;
    }

    /**
     * @param current
     * @param relativeProjectPath Path relative to the project folders src/xml folder. This path also represent absolute path in projects namespace.
     */
    public Perspective extendPerspectiveWithPath(Perspective current, Path relativeProjectPath) {
        for (final var element : list(relativeProjectPath.toString().split("/"))
                .stream()
                .filter(e -> !"".contentEquals(e))
                .collect(toList())) {
            final var children = current.children().stream()
                    .filter(child -> child.nameIs(NameSpaces.VAL, NameSpaces.NATURAL))
                    .filter(child -> child.propertyInstances(NameSpaces.NAME, NameSpaces.NATURAL).stream()
                            .anyMatch(property -> property.value().get().name().equals(element)))
                    .collect(Lists.toList());
            final Perspective child;
            if (children.isEmpty()) {
                child = perspective(NameSpaces.VAL, NameSpaces.NATURAL)
                        .withProperty(NameSpaces.NAME, NameSpaces.NATURAL, element);
                current.withChild(child);
            } else {
                child = children.get(0);
            }
            current = child;
        }
        current.withChild(
                perspective(NameSpaces.LINK, NameSpaces.DEN)
                        .withChild(perspective(NameSpaces.URL, NameSpaces.DEN)
                                .withChild(perspective("/" + relativeProjectPath.toString(), STRING))));
        return current;
    }
}
