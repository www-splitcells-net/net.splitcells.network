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
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.Renderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.validator.RenderingValidator;
import net.splitcells.website.server.Server;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtensionMerger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Paths.generateFolderPath;
import static net.splitcells.dem.resource.Paths.path;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.website.server.project.LayoutUtils.extendPerspectiveWithSimplePath;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.validator.RenderingValidatorForHtmlLinks.renderingValidatorForHtmlLinks;
import static net.splitcells.website.server.project.LayoutUtils.extendPerspectiveWithPath;
import static net.splitcells.website.server.projects.extension.GlobalChangelogExtension.globalChangelogExtension;
import static net.splitcells.website.server.projects.extension.LayoutExtension.layoutExtension;
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

    private static final String LAYOUT_PATH = "/net/splitcells/website/layout/build";

    public static ProjectsRendererI projectsRenderer(String name
            , ProjectRenderer fallbackRenderer
            , List<ProjectRenderer> renderers
            , Config config) {
        return new ProjectsRendererI(name, fallbackRenderer, renderers, config);
    }

    @Override
    public void build() {
        final var generatedFiles = Paths.get("target", "generated");
        Files.createDirectory(generatedFiles);
        writeToFile(generatedFiles.resolve("generation.style.xml")
                , "<val xmlns=\"http://splitcells.net/den.xsd\">"
                        + config.generationStyle()
                        + "</val>");
        writeToFile(generatedFiles.resolve("layout." + profile + ".xml"), createLayout().toDom());
        generateFolderPath(Paths.get("target", "generated"));
    }

    private Perspective createLayout() {
        config.withLayout("<element>This is a placeholder. "
                + "This is used, so that calling projectsPaths returns a link to the layout."
                + "</element>");
        {
            final var relevantLayout = perspective(VAL, NATURAL);
            this.relevantProjectsPaths().forEach(p -> extendPerspectiveWithPath(relevantLayout, p));
            config.withLayoutRelevant(Xml.toPrettyString(relevantLayout.toDom()));
        }
        {
            final var simpleRelevantLayout = perspective("relevant-layout", NATURAL);
            this.relevantProjectsPaths().forEach(p -> extendPerspectiveWithSimplePath(simpleRelevantLayout, p));
            config.withLayoutRelevantPerspective(Optional.of(simpleRelevantLayout));
        }
        {
            final var simpleLayout = perspective("layout", NATURAL);
            this.projectsPaths().forEach(p -> extendPerspectiveWithSimplePath(simpleLayout, p));
            config.withLayoutPerspective(Optional.of(simpleLayout));
        }
        final var layout = perspective(VAL, NATURAL);
        this.projectsPaths().forEach(p -> extendPerspectiveWithPath(layout, p));
        config.withLayout(Xml.toPrettyString(layout.toDom()));
        return layout;
    }

    @Override
    public void serveTo(Path target) {
        renderingValidator.startReport("build" + config.rootPath());
        build();
        projectsPaths().stream()
                .map(path -> config.rootPath() + path.toString())
                .map(path -> {
                    // TODO This is an hack.
                    if (path.endsWith(".md")) {
                        return net.splitcells.dem.resource.Paths.removeFileSuffix(path) + ".html";
                    }
                    return path;
                })
                .forEach(path -> {
                    try {
                        final var targetPath = path(target, path.substring(1));
                        createDirectory(targetPath.getParent());
                        Files.writeToFile(targetPath, render(path).orElseThrow().getContent());
                    } catch (Exception e) {
                        throw new RuntimeException(target.toString() + path, e);
                    }
                });
        renderingValidator.endReport();
    }

    @Override
    public void serveToHttpAt() {
        build();
        new Server().serveToHttpAt(requestPath -> render(requestPath), config);
    }

    @Override
    public void serveAsAuthenticatedHttpsAt() {
        build();
        new Server().serveAsAuthenticatedHttpsAt(requestPath -> render(requestPath), config);
    }

    @Deprecated
    private final String profile;
    private final Config config;
    private final List<ProjectRenderer> renderers;

    private Optional<Set<Path>> projectPathsCache = Optional.empty();
    private final ProjectRenderer fallbackRenderer;
    private final RenderingValidator renderingValidator = renderingValidatorForHtmlLinks();
    private final ProjectsRendererExtensionMerger extension = projectsRendererExtensionMerger()
            .withRegisteredExtension(globalChangelogExtension())
            .withRegisteredExtension(networkStatusRenderExtension())
            .withRegisteredExtension(layoutExtension());

    private ProjectsRendererI(String name
            , ProjectRenderer fallbackRenderer
            , List<ProjectRenderer> renderers
            , Config config) {
        this.profile = name;
        this.fallbackRenderer = fallbackRenderer;
        this.renderers = renderers;
        this.config = config;
    }

    private String normalizedPath(String path) {
        final var normalizedSlash = path.replaceAll("//+", "/");
        final String rootMatched;
        if (config.possibleRootIndex().contains(normalizedSlash)) {
            rootMatched = config.rootIndex();
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

    /**
     * TODO Only use one return statement.
     *
     * @param path path
     * @return Rendering Result
     */
    @Override
    public Optional<RenderingResult> render(String path) {
        final var normalizedPath = normalizedPath(path);
        try {
            final var extensionRendering = extension.renderFile(normalizedPath, this, config);
            if (extensionRendering.isPresent()) {
                return extensionRendering;
            }
            if (normalizedPath.equals(LAYOUT_PATH)) {
                domsole().append(perspective("Refreshing layout."), LogLevel.INFO);
                this.build();
                return validateRenderingResult(Optional.empty(), Path.of(normalizedPath));
            }
            final var matchingRoots = renderers
                    .stream()
                    .filter(root -> normalizedPath.startsWith(root.resourceRootPath()))
                    .collect(toList());
            if (matchingRoots.isEmpty()) {
                // System.out.println("No match for: " + path);
                //System.out.println("Patterns: " + renderers.keySet());
                final var render = fallbackRenderer.render(normalizedPath);
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
                        final var render = renderer.render(normalizedPath);
                        if (render.isPresent()) {
                            return render;
                        }
                        return renderer.render(normalizedPath, config, fallbackRenderer);
                    })
                    .filter(Optional::isPresent)
                    .findFirst();
            if (renderingResult.isEmpty()) {
                domsole().append(perspective("Path could not be found: " + normalizedPath), LogLevel.ERROR);
                return validateRenderingResult(Optional.empty(), Path.of(normalizedPath));
            }
            return validateRenderingResult(renderingResult.get(), Path.of(normalizedPath));
        } catch (Exception e) {
            throw new RuntimeException("path: " + path + ", normalizedPath: " + normalizedPath, e);
        }
    }

    private Optional<RenderingResult> validateRenderingResult(Optional<RenderingResult> renderingResult, Path requestedPath) {
        renderingValidator.validate(renderingResult, this, requestedPath);
        return renderingResult;
    }

    @Override
    public Set<Path> projectsPaths() {
        if (config.mutableProjectsPath()) {
            return calculateProjectsPaths();
        }
        if (projectPathsCache.isEmpty()) {
            projectPathsCache = Optional.of(calculateProjectsPaths());
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
    private List<Perspective> s(Perspective current, String element) {
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
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config) {
        return renderers.get(0).renderHtmlBodyContent(bodyContent, title, path, config);
    }

    @Override
    public Optional<RenderingResult> renderContent(String content, LayoutConfig metaContent) {
        return Optional.of(renderingResult(fallbackRenderer.renderXml(content, metaContent, config).orElseThrow(), TEXT_HTML.toString()));
    }
}
