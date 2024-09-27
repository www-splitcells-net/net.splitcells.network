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
package net.splitcells.website.server;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.perspective.Tree;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.ProcessorRegistry;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtensions;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.website.server.processor.ProcessorRegistry.binaryProcessorRegistry;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

/**
 * TODO IDEA Use string and enum based mapping as a backend,
 * so that clones can easily be created.
 * This would also make serialization easier.
 * Maybe create a serialization mini framework for that?
 */
public class Config {
    public static Config create() {
        return new Config();
    }

    /**
     * A SSL-Keystore is required for securing the web server.
     * No SSL-Keystore is required by default, in order to easy minimal deployments.
     */
    private boolean isSecured = false;

    /**
     * No SSL-Keystore is required by default, in order to easy minimal deployments.
     */
    private Optional<String> sslKeystorePassword = Optional.empty();
    /**
     * No SSL-Keystore is required by default, in order to easy minimal deployments.
     */
    private Optional<Path> sslKeystoreFile = Optional.empty();

    /**
     * TODO Use variable for storing the contents of the XSD in order to be file system independent,
     * instead of using a path to the XSD.
     */
    @Deprecated
    private Optional<Path> xmlSchema = Optional.of(net.splitcells.dem.resource.Paths.path("src/main/xsd/den.xsd"));

    /**
     * Points to the folder containing projects required for data, like XSL transformations.
     * These are needed in order to render the website.
     */
    private Optional<Path> mainProjectRepositoryPath = Optional.empty();

    /**
     * This is an XML document provided for the XSL based renderer.
     * It contains all paths supported by the renderer.
     */
    @Deprecated
    private Optional<String> layout = Optional.empty();
    /**
     * This is an XML document provided for the XSL based renderer.
     * It contains all paths supported by the renderer, that are relevant to the targeted common user.
     */
    @Deprecated
    private Optional<String> layoutRelevant = Optional.empty();
    /**
     * <p>This is a tree structure containing all paths supported by the renderer.</p>
     * <p>Replaces {@link #layout}.</p>
     */
    private Optional<Tree> layoutPerspective = Optional.empty();

    /**
     * <p>This is a tree structure containing all paths supported by the renderer,
     * that are relevant to the targeted common user.</p>
     * <p>Replaces {@link #layoutRelevant}.</p>
     */
    private Optional<Tree> layoutRelevantPerspective = Optional.empty();
    /**
     * A port bigger than 1023 is used, as on Linux port between 0-1023 are privileged ports.
     * This means opening such a port requires additional user privileges and
     * therefore complicates deployments with minimal configs.<
     */
    private int openPort = 8443;
    private String generationStyle = "standard";
    /**
     * Lists all CSS files, that should be used for the layouts instead of the standard ones, used by the layout.
     */
    private List<String> cssFiles = list();
    /**
     * Lists all JavaScript files, that should be used for the layouts.
     * These JavaScripts will be placed at the end of the layout and therefore loaded last.
     * These files are loaded in the given order.
     */
    private List<String> jsBackgroundFiles = list();
    /**
     * TODO This does not seem to be used actively anymore.
     * It was used for some specific resources,
     * but this way of resource loading should probably not be used anymore,
     * as it is unnecessarily hard to maintain.
     */
    @Deprecated
    private Optional<String> siteFolder;
    /**
     * All calls of {@link net.splitcells.website.server.projects.ProjectsRenderer#render(String)}
     * with a path not starting with {@link #rootPath} will not return anything.
     */
    private String rootPath = "/";

    private String sitesName = "splitcells.net";
    /**
     * This is the root index file of the website.
     */
    private String rootIndex = "/index.html";
    /**
     * List of paths, that are equivalent to {@link #rootIndex}.
     * Browsers like Firefox like to call <q>[...]/index.html</q> instead of <q>[...]/</q>.
     */
    private List<String> possibleRootIndex = list(rootIndex
            , "index.html"
            , ""
            , "/");

    /**
     * <p>States whether {@link net.splitcells.website.server.project.Renderer}s may cache certain parts,
     * like the output and styling information, or not.
     * Setting this to true, speeds up the rendering, but makes it impossible to reload the data of renderers quickly
     * during development and thereby generally slows down the development speed.</p>
     * <p>TODO Set this to true by default in order to speed up default production settings.</p>
     */
    private boolean cacheRenderers = false;

    /**
     * Determines whether it can be assumed, that {@link ProjectsRenderer#projectsPaths()} changes.
     * Otherwise, {@link ProjectsRenderer#projectsPaths()} can be cached, for increased performance,
     * which is useful, when a static website is rendered.
     */
    private boolean mutableProjectsPath = true;

    /**
     * Contains the content of the detailed menu for each page.
     * The content should be processable by XSL.
     */
    private Optional<String> detailedXslMenu = Optional.of("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<xsl:stylesheet version=\"2.0\" xmlns:s=\"http://splitcells.net/sew.xsd\"\n"
            + "                xmlns:svg=\"http://www.w3.org/2000/svg\"\n"
            + "                xmlns=\"http://www.w3.org/1999/xhtml\"\n"
            + "                xmlns:x=\"http://www.w3.org/1999/xhtml\"\n"
            + "                xmlns:d=\"http://splitcells.net/den.xsd\"\n"
            + "                xmlns:p=\"http://splitcells.net/private.xsd\"\n"
            + "                xmlns:m=\"http://www.w3.org/1998/Math/MathML\"\n"
            + "                xmlns:r=\"http://splitcells.net/raw.xsd\"\n"
            + "                xmlns:n=\"http://splitcells.net/natural.xsd\"\n"
            + "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
            + "                xmlns:xl=\"http://www.w3.org/1999/xlink\"\n"
            + "                xmlns:ns=\"http://splitcells.net/namespace.xsd\">\n"
            + "   <xsl:variable name=\"net-splitcells-website-server-config-menu-detailed\"/>\n"
            + "</xsl:stylesheet>");

    /**
     * Defines the content of the menu for the main tab on the page.
     */
    private Optional<String> xslWindowMenu = Optional.of("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<xsl:stylesheet version=\"2.0\" xmlns:s=\"http://splitcells.net/sew.xsd\"\n"
            + "                xmlns:svg=\"http://www.w3.org/2000/svg\"\n"
            + "                xmlns=\"http://www.w3.org/1999/xhtml\"\n"
            + "                xmlns:x=\"http://www.w3.org/1999/xhtml\"\n"
            + "                xmlns:d=\"http://splitcells.net/den.xsd\"\n"
            + "                xmlns:p=\"http://splitcells.net/private.xsd\"\n"
            + "                xmlns:m=\"http://www.w3.org/1998/Math/MathML\"\n"
            + "                xmlns:r=\"http://splitcells.net/raw.xsd\"\n"
            + "                xmlns:n=\"http://splitcells.net/natural.xsd\"\n"
            + "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
            + "                xmlns:xl=\"http://www.w3.org/1999/xlink\"\n"
            + "                xmlns:ns=\"http://splitcells.net/namespace.xsd\">\n"
            + "   <xsl:variable name=\"net-splitcells-website-server-config-window-menu\"/>\n"
            + "</xsl:stylesheet>");

    private List<ProjectConfig> additionalProjects = list();

    private List<ProgramConfig> programConfigs = list();
    private List<ProjectsRendererExtension> projectsRendererExtension = Dem.configValue(ProjectsRendererExtensions.class);

    private ProcessorRegistry<Tree, Tree> processor = binaryProcessorRegistry();

    /**
     * Signals whether this server is available for the general public.
     * If this is the case, some functionality may to have be changed.
     * This is typically used in order to show a terms of service or privacy pop-up,
     * when this web server instance is provided to the general public.
     */
    private boolean isServerForGeneralPublic = true;

    /**
     * <p>Signals whether the client is expected to support client side downloads.
     * If enabled, downloads created via a client side JavaScript framework,
     * must be stored in an hidden HTML element
     * with the id `net-splitcells-website-server-download-content` instead of creating a normal download.
     * The download's default file name is stored in an hidden HTML  with the id
     * `net-splitcells-website-server-download-name-default`.</p>
     * <p>This is used for GUI frameworks like JavaFX, which do no seem to support downloads,
     * created by JavaScript inside the web page.
     * Instead, a client is used, that periodically searches the current page for the HTML elements,
     * with the download data.
     * If it is present, the content's are parsed and a download/safe dialog is created.
     * Afterwards, the element's content are deleted.
     * </p>
     */
    private boolean isDownloadingViaHtmlElement = false;
    /**
     * If set to true, it is assumed, that the server is used to render the server as a static website once.
     * This option is used, in order to improve the runtime in this use case.
     */
    private boolean isRenderingStaticWebsite = false;

    /*
     * If true, the webserver HTTP backend is instanced as always, but multiple {@link ProjectsRenderer} are created.
     * Each {@link ProjectsRenderer} is dedicated to one thread and the HTTP backend dedicates each request
     * to one {@link ProjectsRenderer}.
     * The thread safety of the resources used by the {@link ProjectsRenderer} are to be ensured by the caller.
     */
    private boolean isMultiThreaded = false;

    private Config() {
    }

    public Config withLayout(String layout) {
        this.layout = Optional.of(layout);
        return this;
    }

    public Optional<String> layout() {
        return layout;
    }

    public Config withOpenPort(int openPort) {
        this.openPort = openPort;
        return this;
    }

    public int openPort() {
        return openPort;
    }

    public String generationStyle() {
        return generationStyle;
    }

    public Config withGenerationStyle(String arg) {
        generationStyle = arg;
        return this;
    }

    public Optional<String> sslKeystorePassword() {
        return sslKeystorePassword;
    }

    public Optional<Path> sslKeystoreFile() {
        return sslKeystoreFile;
    }

    public Config witSslKeystoreFile(Optional<Path> sslKeystoreFile) {
        this.sslKeystoreFile = sslKeystoreFile;
        return this;
    }

    @Deprecated
    public Optional<String> siteFolder() {
        return siteFolder;
    }

    @Deprecated
    public Config withSiteFolder(Optional<String> siteFolder) {
        this.siteFolder = siteFolder;
        return this;
    }

    public Config withLayoutRelevant(String layoutRelevant) {
        this.layoutRelevant = Optional.of(layoutRelevant);
        return this;
    }

    public Optional<String> layoutRelevant() {
        return layoutRelevant;
    }

    public Config withRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public String rootPath() {
        return rootPath;
    }

    public String rootIndex() {
        return rootIndex;
    }

    public List<String> possibleRootIndex() {
        return possibleRootIndex;
    }

    public Config withCacheRenderers(boolean cacheRenderers) {
        this.cacheRenderers = cacheRenderers;
        return this;
    }

    public boolean cacheRenderers() {
        return cacheRenderers;
    }

    public Optional<Tree> layoutPerspective() {
        return layoutPerspective;
    }

    public Optional<Tree> layoutPerspectiveSubtree(List<String> path) {
        return layoutPerspective;
    }

    @ReturnsThis
    public Config withLayoutPerspective(Optional<Tree> arg) {
        layoutPerspective = arg;
        return this;
    }

    public Optional<Tree> layoutRelevantPerspective() {
        return layoutRelevantPerspective;
    }

    @ReturnsThis
    public Config withLayoutRelevantPerspective(Optional<Tree> arg) {
        layoutRelevantPerspective = arg;
        return this;
    }

    public boolean mutableProjectsPath() {
        return mutableProjectsPath;
    }

    @ReturnsThis
    public Config withMutableProjectsPath(boolean arg) {
        mutableProjectsPath = arg;
        return this;
    }

    @ReturnsThis
    public Config withMainProjectRepositoryPath(Path arg) {
        mainProjectRepositoryPath = Optional.of(arg);
        return this;
    }

    public Optional<Path> mainProjectRepositoryPath() {
        return mainProjectRepositoryPath;
    }

    @ReturnsThis
    public Config withDetailedXslMenu(Optional<String> arg) {
        detailedXslMenu = arg;
        return this;
    }

    public Optional<String> detailedXslMenu() {
        return detailedXslMenu;
    }

    @ReturnsThis
    public Config withCssFiles(List<String> arg) {
        cssFiles = arg;
        return this;
    }

    @ReturnsThis
    public Config withAdditionalCssFile(String arg) {
        cssFiles.add(arg);
        return this;
    }

    public List<String> cssFiles() {
        return cssFiles;
    }

    public boolean isSecured() {
        return isSecured;
    }

    @ReturnsThis
    public Config withIsSecured(boolean arg) {
        isSecured = arg;
        return this;
    }

    @ReturnsThis
    public Config withXmlSchema(Optional<Path> arg) {
        xmlSchema = arg;
        return this;
    }

    @Deprecated
    public Optional<Path> xmlSchema() {
        return xmlSchema;
    }

    public List<ProjectConfig> additionalProjects() {
        return additionalProjects;
    }

    public Config withAdditionalProject(ProjectConfig project) {
        additionalProjects.add(project);
        return this;
    }

    /**
     * TODO This is a hack. It would be better to collect all matching {@link ProjectRenderer} for a requested path and
     * choose the one, with the longest {@link ProjectRenderer#resourceRootPath2()}.
     *
     * @param project Adds the given project to the start of {@link #additionalProjects}.
     *                This is used when multiple {@link ProjectRenderer#resourceRootPath2()} share a common prefix with
     *                a given requested path.
     *                Matches at the start of {@link #additionalProjects} are prioritized over matches at the end.
     * @return
     */
    public Config withAdditionalProjectAtStart(ProjectConfig project) {
        additionalProjects.addFirst(project);
        return this;
    }

    public Config withJsBackgroundFiles(List<String> args) {
        jsBackgroundFiles = args;
        return this;
    }

    public Config withAdditionalJsBackgroundFiles(String arg) {
        jsBackgroundFiles.add(arg);
        return this;
    }

    public List<String> jsBackgroundFiles() {
        return jsBackgroundFiles;
    }

    public Config withXslWindowMenu(Optional<String> arg) {
        this.xslWindowMenu = arg;
        return this;
    }

    public Optional<String> xslWindowMenu() {
        return xslWindowMenu;
    }

    public List<ProgramConfig> programConfigs() {
        return programConfigs;
    }

    public Config withAdditionalProgramConfig(ProgramConfig programConfig) {
        programConfigs.add(programConfig);
        return this;
    }

    public Config clearAdditionalProgramConfigs() {
        programConfigs.clear();
        return this;
    }

    public Config withAdditionalProjectsRendererExtension(ProjectsRendererExtension arg) {
        projectsRendererExtensions().add(arg);
        return this;
    }

    public List<ProjectsRendererExtension> projectsRendererExtensions() {
        return projectsRendererExtension;
    }

    public ProcessorRegistry<Tree, Tree> processor() {
        return processor;
    }

    public Config withAdditionalProcessor(Trail trail, Processor<Tree, Tree> arg) {
        processor.register(trail, arg);
        return this;
    }

    public boolean isServerForGeneralPublic() {
        return isServerForGeneralPublic;
    }

    public Config withIsServerForGeneralPublic(boolean arg) {
        isServerForGeneralPublic = arg;
        return this;
    }

    public Config withIsDownloadingViaHtmlElement(boolean arg) {
        isDownloadingViaHtmlElement = arg;
        return this;
    }

    public boolean isDownloadingViaHtmlElement() {
        return isDownloadingViaHtmlElement;
    }

    public boolean isRenderingStaticWebsite() {
        return isRenderingStaticWebsite;
    }

    public Config withIsRenderingStaticWebsite(boolean arg) {
        isRenderingStaticWebsite = arg;
        return this;
    }

    public String sitesName() {
        return sitesName;
    }

    public Config withSitesName(String arg) {
        sitesName = arg;
        return this;
    }

    public boolean isMultiThreaded() {
        return isMultiThreaded;
    }

    public boolean isSingleThreaded() {
        return !isMultiThreaded;
    }

    public Config withIsMultiThreaded(boolean argIsMultiThreaded) {
        isMultiThreaded = argIsMultiThreaded;
        return this;
    }
}