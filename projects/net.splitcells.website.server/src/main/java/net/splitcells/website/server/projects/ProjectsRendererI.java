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
package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.TreeI;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.Renderer;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.project.validator.RenderingValidator;
import net.splitcells.website.server.Server;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.projects.extension.ProjectPathsRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtensionMerger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.lang.tree.XmlConfig.xmlConfig;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.Paths.path;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.project.LayoutUtils.extendPerspectiveWithSimplePath;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.project.validator.RenderingValidatorForHtmlLinks.renderingValidatorForHtmlLinks;
import static net.splitcells.website.server.project.LayoutUtils.extendPerspectiveWithPath;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;
import static net.splitcells.website.server.projects.extension.ColloquiumPlanningDemandsTestData.colloquiumPlanningDemandTestData;
import static net.splitcells.website.server.projects.extension.ColloquiumPlanningSuppliesTestData.colloquiumPlanningSuppliesTestData;
import static net.splitcells.website.server.projects.extension.DemConfigExtension.demConfigExtension;
import static net.splitcells.website.server.projects.extension.FrontMenuExtension.frontMenuExtension;
import static net.splitcells.website.server.projects.extension.GlobalChangelogExtension.globalChangelogExtension;
import static net.splitcells.website.server.projects.extension.LayoutExtension.layoutExtension;
import static net.splitcells.website.server.projects.extension.LayoutFancyTreeExtension.layoutFancyTreeExtension;
import static net.splitcells.website.server.projects.extension.LayoutTreeExtension.layoutTreeExtension;
import static net.splitcells.website.server.projects.extension.TestExtension.testExtension;
import static net.splitcells.website.server.projects.extension.UserProfilePageExtension.userProfilePageExtension;
import static net.splitcells.website.server.projects.extension.status.HostCpuUtilizationExtension.hostCpuUtilizationExtension;
import static net.splitcells.website.server.projects.extension.status.HostMemoryUtilizationExtension.hostMemoryUtilizationExtension;
import static net.splitcells.website.server.projects.extension.status.NetworkStatusRenderExtension.networkStatusRenderExtension;
import static net.splitcells.website.server.projects.extension.ProjectsRendererExtensionMerger.projectsRendererExtensionMerger;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>TODO Create extension system meta project rendering.</p>
 * <p>TODO Support rendering project to alternative formats like gemtext for Gemini sites or CommonMark.
 * TODOC Maximum chapter depth does not have to be limited in order to have compatibility with alternative formats.
 * One can instead generate informal pseudo chapters in generated outputs,
 * in order to adhere to maximum chapter depth requirements in the output format.</p>
 */
public class ProjectsRendererI implements ProjectsRenderer {

    private static final String LAYOUT_PATH = "/net/splitcells/website/layout/build.html";

    public static ProjectsRendererI projectsRenderer(String name
            , Function<ProjectsRenderer, ProjectRenderer> fallbackRenderer
            , Function<ProjectsRenderer, List<ProjectRenderer>> renderers
            , Config config) {
        return new ProjectsRendererI(name, fallbackRenderer, renderers, config);
    }

    /**
     * TODO This method should not require a write to the local file system.
     */
    @Override
    public void build() {
        final var generatedFilesPath = Paths.get("target", "generated");
        Files.createDirectory(generatedFilesPath);
        final var generatedFiles = fileSystemOnLocalHost(generatedFilesPath);
        generatedFiles.writeToFile(Path.of("generation.style.xml")
                , "<val xmlns=\"http://splitcells.net/den.xsd\">"
                        + config.generationStyle()
                        + "</val>");
        generatedFiles.writeToFile(Path.of("layout." + profile + ".xml"), createLayout().toXmlString());
    }

    private Tree createLayout() {
        config.withLayout("<element>This is a placeholder. "
                + "This is used, so that calling projectsPaths returns a link to the layout."
                + "</element>");
        {
            final var relevantLayout = TreeI.tree(VAL, NATURAL);
            this.relevantProjectsPaths().forEach(p -> extendPerspectiveWithPath(relevantLayout, p));
            config.withLayoutRelevant(relevantLayout.toXmlString(xmlConfig()));
        }
        {
            final var simpleRelevantLayout = TreeI.tree("relevant-layout", NATURAL);
            this.relevantProjectsPaths().forEach(p -> extendPerspectiveWithSimplePath(simpleRelevantLayout, p));
            config.withLayoutRelevantPerspective(Optional.of(simpleRelevantLayout));
        }
        {
            final var simpleLayout = TreeI.tree("layout", NATURAL);
            this.projectsPaths().forEach(p -> extendPerspectiveWithSimplePath(simpleLayout, p));
            config.withLayoutPerspective(Optional.of(simpleLayout));
        }
        final var layout = TreeI.tree(VAL, NATURAL);
        this.projectsPaths().forEach(p -> extendPerspectiveWithPath(layout, p));
        config.withLayout(layout.toXmlString(xmlConfig()));
        return layout;
    }

    @Override
    public void serveTo(Path target) {
        renderingValidator.startReport("build" + config.rootPath());
        build();
        projectsPaths().stream()
                .map(path -> (config.rootPath() + path.toString()).replace('\\', '/'))
                .map(path -> {
                    // TODO This is a hack.
                    if (path.endsWith(".md")) {
                        return net.splitcells.dem.resource.Paths.removeFileSuffix(path) + ".html";
                    }
                    return path;
                })
                .forEach(path -> {
                    try {
                        final var targetPath = path(target, path.substring(1));
                        Files.createDirectory(targetPath.getParent());
                        Files.writeToFile(targetPath, render(path).orElseThrow().getContent());
                    } catch (Exception e) {
                        throw executionException(tree("Could not serve path to file system.")
                                        .withProperty("target", target.toString())
                                        .withProperty("path", path.toString())
                                , e);
                    }
                });
        renderingValidator.endReport();
    }

    @Override
    public Service httpServer() {
        logs().appendWarning(tree("`ProjectsRenderer#httpServer()` should not be used anymore. Use `Server#serveToHttpAt()` instead, because multi threading is not supported for `ProjectsRenderer#httpServer()`."));
        build();
        return Server.serveToHttpAt(() -> this, config);
    }

    @Override
    public Service authenticatedHttpsServer() {
        build();
        return Server.serveAsAuthenticatedHttpsAt(requestPath -> render(requestPath), config);
    }

    @Deprecated
    private final String profile;
    private final Config config;
    private final List<ProjectRenderer> renderers;

    private Optional<Set<Path>> projectPathsCache = Optional.empty();
    private final ProjectRenderer fallbackRenderer;
    private final RenderingValidator renderingValidator = renderingValidatorForHtmlLinks();
    /**
     * TODO In the future, all extensions should be added via dependency injection.
     */
    private final ProjectsRendererExtensionMerger extension = projectsRendererExtensionMerger()
            .withRegisteredExtension(globalChangelogExtension())
            .withRegisteredExtension(networkStatusRenderExtension())
            .withRegisteredExtension(layoutExtension())
            .withRegisteredExtension(layoutTreeExtension())
            .withRegisteredExtension(frontMenuExtension())
            .withRegisteredExtension(colloquiumPlanningDemandTestData())
            .withRegisteredExtension(colloquiumPlanningSuppliesTestData());

    private final List<ProjectsRendererExtension> extensions = listWithValuesOf(
            hostCpuUtilizationExtension()
            , hostMemoryUtilizationExtension()
            , userProfilePageExtension()
            , demConfigExtension()
            , testExtension()
            , layoutFancyTreeExtension()
    );

    private ProjectsRendererI(String name
            , ProjectRenderer fallbackRenderer
            , List<ProjectRenderer> renderers
            , Config config) {
        this.profile = name;
        this.fallbackRenderer = fallbackRenderer;
        this.renderers = renderers;
        this.config = config;
        config.projectsRendererExtensions().forEach(extension::withRegisteredExtension);
    }

    private ProjectsRendererI(String name
            , Function<ProjectsRenderer, ProjectRenderer> fallbackRenderer
            , Function<ProjectsRenderer, List<ProjectRenderer>> renderers
            , Config config) {
        this.profile = name;
        this.config = config;
        config.projectsRendererExtensions().forEach(extension::withRegisteredExtension);
        this.fallbackRenderer = fallbackRenderer.apply(this);
        this.renderers = renderers.apply(this);
    }

    private String normalizedPath(String path) {
        final var normalizedSlash = path.replaceAll("//+", "/");
        final String rootMatched;
        if (config.possibleRootIndex().contains(normalizedSlash)) {
            return config.rootIndex();
        } else {
            rootMatched = normalizedSlash;
        }
        final var translatedPathTmp = rootMatched.substring(config.rootPath().length());
        final String startingWithSlash;
        if (translatedPathTmp.startsWith("/")) {
            startingWithSlash = translatedPathTmp;
        } else {
            startingWithSlash = "/" + translatedPathTmp;
        }
        return startingWithSlash;
    }

    @Override
    public RenderResponse render(RenderRequest request) {
        final var deprecatedRender = render("/" + request.trail().unixPathString());
        if (deprecatedRender.isPresent()) {
            return renderResponse(deprecatedRender);
        }
        final var responses = extensions.stream()
                .map(e -> e.render(request, this))
                .filter(r -> r.data().isPresent())
                .collect(toList());
        if (responses.isEmpty()) {
            return renderResponse(Optional.empty());
        }
        if (responses.size() > 1) {
            logs().appendWarning(tree("Multiple matches for one requests. Choosing first one as final response.")
                    .withProperty("trail", request.trail().unixPathString()));
        }
        return responses.get(0);
    }

    /**
     * TODO Only use one return statement.
     *
     * @param path path
     * @return Rendering Result
     */
    @Override
    public Optional<BinaryMessage> render(String path) {
        final var normalizedPath = normalizedPath(path);
        try {
            final var extensionRendering = extension.renderFile(normalizedPath, this, config);
            if (extensionRendering.isPresent()) {
                return extensionRendering;
            }
            if (normalizedPath.equals(LAYOUT_PATH)) {
                logs().append(tree("Refreshing layout."), LogLevel.INFO);
                this.build();
                return render("/");
            }
            final var matchingRoots = renderers
                    .stream()
                    .filter(root -> normalizedPath.startsWith(root.resourceRootPath()))
                    .collect(toList());
            if (matchingRoots.isEmpty()) {
                // System.out.println("No match for: " + path);
                //System.out.println("Patterns: " + renderers.keySet());
                final var render = fallbackRenderer.render(normalizedPath, this);
                if (render.isPresent()) {
                    return validateRenderingResult(render, Path.of(normalizedPath));
                }
                return validateRenderingResult(fallbackRenderer.render(normalizedPath, config, fallbackRenderer)
                        , Path.of(normalizedPath));
            }
            // System.out.println("Match for: " + path);
            // System.out.println("Match on: " + matchingRoots.get(0));
            // Sort descending.
            matchingRoots.sort((a, b) -> Integer.valueOf(a.resourceRootPath().length()).compareTo(b.resourceRootPath().length()));
            matchingRoots.reverse();
            final var renderingResult = matchingRoots.stream()
                    .map(renderer -> {
                        final var render = renderer.render(normalizedPath, this);
                        if (render.isPresent()) {
                            return render;
                        }
                        return renderer.render(normalizedPath, config, fallbackRenderer);
                    })
                    .filter(Optional::isPresent)
                    .findFirst();
            if (renderingResult.isEmpty()) {
                logs().append(tree("Path could not be found: " + normalizedPath), LogLevel.ERROR);
                return validateRenderingResult(Optional.empty(), Path.of(normalizedPath));
            }
            return validateRenderingResult(renderingResult.get(), Path.of(normalizedPath));
        } catch (Exception e) {
            throw new RuntimeException("path: " + path + ", normalizedPath: " + normalizedPath, e);
        }
    }

    @Override
    public Optional<BinaryMessage> sourceCode(String trail) {
        final var sourceCodes = renderers
                .stream()
                .map(r -> r.sourceCode(trail, this))
                .filter(Optional::isPresent)
                .collect(toList());
        if (sourceCodes.size() > 1) {
            throw executionException(tree("Multiple source codes for one trail. Trail of source code has to be unambiguous.")
                    .withProperty("trail", trail.toString())
                    .withProperty("source codes", sourceCodes.toString()));
        }
        if (sourceCodes.hasElements()) {
            return sourceCodes.get(0);
        }
        return Optional.empty();
    }

    private Optional<BinaryMessage> validateRenderingResult(Optional<BinaryMessage> renderingResult, Path requestedPath) {
        try {
            renderingValidator.validate(renderingResult, this, requestedPath);
        } catch (Throwable th) {
            logs().appendWarning(tree("Could not validate rendering result:")
                            .withProperty("rendering result", renderingResult.toString())
                            .withProperty("requested path", requestedPath.toString())
                    , th);
        }
        return renderingResult;
    }

    @Override
    public Set<Path> projectsPaths() {
        if (config.mutableProjectsPath()) {
            return calculateProjectsPaths().with(Path.of(LAYOUT_PATH.substring(1)));
        }
        if (projectPathsCache.isEmpty()) {
            projectPathsCache = Optional.of(calculateProjectsPaths().with(Path.of(LAYOUT_PATH.substring(1))));
        }
        return projectPathsCache.get();
    }

    private Set<Path> calculateProjectsPaths() {
        return renderers.stream()
                .map(Renderer::projectPaths)
                .reduce((a, b) -> a.with(b))
                .orElse(setOfUniques())
                .with(extension.projectPaths(this));
    }

    @Override
    public Set<Path> relevantProjectsPaths() {
        return renderers.stream()
                .map(Renderer::relevantProjectPaths)
                .reduce((a, b) -> a.with(b))
                .orElse(setOfUniques())
                .with(extension.projectPaths(this));
    }

    @Deprecated
    private List<Tree> s(Tree current, String element) {
        final var children = current.children().stream()
                .filter(child -> child.nameIs(VAL, NATURAL))
                .filter(child -> child.propertyInstances(NAME, NATURAL).stream()
                        .anyMatch(property -> property.value().orElseThrow().name().equals(element)))
                .collect(toList());
        assertThat(children).hasSizeLessThan(2);
        return children;
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public List<ProjectRenderer> projectRenderers() {
        return renderers;
    }

    @Override
    public Optional<PageMetaData> metaData(String path) {
        final var metaData = renderers.stream()
                .map(r -> r.metaData(path, this))
                .filter(m -> m.isPresent())
                .collect(toList());
        if (metaData.isEmpty()) {
            return Optional.empty();
        }
        if (metaData.size() > 1) {
            logs().appendWarning(executionException("Expecting at most 1 meta data entries but found "
                    + metaData.size()
                    + " instead: "
                    + metaData));
        }
        return metaData.get(0);
    }

    @Override
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config) {
        return fallbackRenderer.renderHtmlBodyContent(bodyContent, title, path, config, this);
    }

    @Override
    public Optional<BinaryMessage> renderContent(String content, LayoutConfig metaContent) {
        return Optional.of(binaryMessage(fallbackRenderer.renderXml(content, metaContent, config).orElseThrow(), TEXT_HTML.toString()));
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        for (final var ext : extensions) {
            if (ext.requiresAuthentication(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectPathsRequest request) {
        return extensions.stream()
                .map(e -> e.projectPaths(request))
                .reduce(Set::with)
                .orElseGet(Sets::setOfUniques);
    }
}
