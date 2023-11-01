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
package net.splitcells.dem.resource;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Files.readAsString;
import static net.splitcells.dem.resource.Files.walk_recursively;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.countChar;
import static net.splitcells.dem.utils.StringUtils.removePrefix;

/**
 * <p>Provides an {@link FileSystem} API for {@link Class#getResource(String)}.</p>
 * <p>Never place multiple {@link FileSystemViaClassResources} at the same package across modules or jars,
 * but with a different class name,
 * because this will create hard to debug problems while accessing files.</p>
 * <p>TODO For maximal portability {@link #walkRecursively()} and co.
 * should use a resource file, containing a list of all resource files.
 * Otherwise, relative much support may be required for {@link #walkRecursively()} and co.
 * to work in different environments.
 * This would probably the case for i.e. TeaVM.</p>
 * <p>TODO In the future it would be probably best, to delegate the implementation of {@link #walkRecursively()} and co.
 * to external dependencies.
 * The reason for this is its complex implementation and the dependency on the runtime's platform and environment,
 * that in turn causes relative much work.
 * Spring Core's PathMatchingResourcePatternResolver seems to be a working example.
 * This delegation has to be optional, in order to minimize the number of required dependencies.
 * Therefore, this should be implemented at an extension project and not an core or API project.
 * </p>
 */
@JavaLegacyArtifact
public class FileSystemViaClassResources implements FileSystemView {

    /**
     * <p>Often, it is expected that resources of a jar are located under
     * `${project.groupId}.${project.artifactId}.resources/` inside the given jar.
     * This method recreates this folder name.</p>
     * <p>Such a sub folder is used in order to ensure,
     * that the only resources present in them are those, that are deliberately placed there.
     * Otherwise, files like `META-INF/MANIFEST.MF` and `** /*.java` would add noise to the resource file structure.
     * This in turn, could be critical for operations such as {@link FileSystemView#walkRecursively(String)}.
     * </p>
     *
     * @param groupdId
     * @param artifiactId
     * @return
     */
    public static String resourceBasePath(String groupdId, String artifiactId) {
        return groupdId + "." + artifiactId + ".resources/";
    }

    public static FileSystemView fileSystemViaClassResources(Class<?> clazz) {
        return new FileSystemViaClassResources(clazz, "");
    }

    public static FileSystemView fileSystemViaClassResources(Class<?> clazz, String basePath) {
        return new FileSystemViaClassResources(clazz, basePath);
    }

    /**
     * All files accessed via paths has {@link #basePath} as the prefix for these paths.
     */
    private final String basePath;
    private final Class<?> clazz;

    private FileSystemViaClassResources(Class<?> clazz, String basePath) {
        this.clazz = clazz;
        this.basePath = basePath;
    }

    @Override
    public InputStream inputStream(Path path) {
        return clazz.getResourceAsStream("/" + basePath + path.toString());
    }

    @Override
    public String readString(Path path) {
        final var resourcePath = ("/" + basePath + path.toString()).replace("\\", "/");
        try {
            return readAsString(clazz.getResourceAsStream(resourcePath));
        } catch (Throwable th) {
            throw executionException(perspective("Could not read file from class path resources:")
                            .withProperty("path requested", path.toString())
                            .withProperty("calculated resource path", resourcePath)
                    , th);
        }
    }

    @Override
    public boolean exists() {
        return true;
    }

    /**
     * <p>On directories {@link Class#getResourceAsStream(String)} returns a list of sub folders,
     * if the given argument is a folder.
     * Therefore, the path is checked, if the given path has no children,
     * in order to ensure, that the given path is no folder.</p>
     * <p>Empty folders return an empty string via {@link Class#getResourceAsStream(String)}.
     * Therefore, empty files and empty folders are not supported nad are considered to not exist.</p>
     * <p>TODO Query the list of all files in the resources of the class loader and check its content with the argument.
     * This can be done by reading the root resource path,
     * because on folders the {@link Class#getResourceAsStream(String)} returns a list of sub folders.
     * </p>
     *
     * @param path
     * @return
     */
    @Override
    public boolean isFile(Path path) {
        final var inputStream = clazz.getResourceAsStream(normalize("/" + basePath + path));
        return inputStream != null
                && Files.readAsString(inputStream).length() != 0
                && walkRecursively(path)
                .filter(p -> !path.toString().equals(p.toString()))
                .count() == 0;
    }

    private String normalize(String path) {
        return path.replace("./", "").replace("//", "/");
    }

    @Override
    public boolean isDirectory(Path path) {
        return walkRecursively(path).findAny().isPresent();
    }

    @Override
    public Stream<Path> walkRecursively() {
        return walkRecursively(Path.of("/"));
    }

    /**
     * Finds the resource protocol of the source code,
     * in order to get more information about the protocols used for loading resources.
     *
     * @return The source code resource protocol of the {@link Class#getClassLoader()}
     * of {@link FileSystemViaClassResources}.
     */
    private String defaultProtocol() {
        var testResource = FileSystemViaClassResources.class.getClassLoader()
                .getResource("net/");
        if (testResource == null) {
            testResource = FileSystemViaClassResources.class.getClassLoader()
                    .getResource("/net/");
        }
        if (testResource == null) {
            throw executionException(perspective("Could not test default protocol for resources of the class loader via the `net` source code package."));
        }
        final var resourceTestPath = testResource
                .getProtocol();
        if (resourceTestPath == null) {
            throw executionException(perspective("Could not determine default protocol for resources of the class loader."));
        }
        return resourceTestPath;
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        try {
            final var resourcePath = clazz.getClassLoader().getResource(normalize((basePath + path + "/")));
            if (resourcePath == null) {
                return Stream.empty();
            }
            if ("jar".equals(resourcePath.getProtocol())) {
                final var defaultProtocol = defaultProtocol();
                if (!"file".equals(defaultProtocol)) {
                    // Load resources from class loader via jars or something, that is not the file protocol.
                    try {
                        final var pathStr = basePath + path.toString();
                        final var dirURL = clazz.getClassLoader().getResource("/" + pathStr);
                        final var explenationCount = countChar(dirURL.getPath(), '!');
                        if (explenationCount == 0) {
                            throw executionException(perspective("Cannot process jar path, as no explanation mark is present in the class loader path. This indicates, that the resource is not located inside a jar:")
                                    .withProperty("clazz", clazz.toString())
                                    .withProperty("path", path.toString())
                                    .withProperty("pathStr", pathStr)
                                    .withProperty("dirURL", dirURL.toString()));
                        } else if (explenationCount == 1) {
                            final var jarPath = dirURL.getPath().substring(5, dirURL.getPath().lastIndexOf("!"));
                            // The jar is provided by the class loader, and thereby this jar is trusted.
                            try (final var jarFile = new JarFile(URLDecoder.decode(jarPath, UTF_8))) {
                                final var jarEntries = jarFile
                                        .entries()
                                        .asIterator();
                                final List<Path> walk = list();
                                while (jarEntries.hasNext()) {
                                    walk.withAppended(Path.of((jarEntries.next().getRealName())));
                                }
                                /*
                                 * If the resources are loaded from a jar, the `META-INF` folder is actively filtered afterwards,
                                 * in order to avoid walking through it, even it is not request.
                                 * For example, without this hack requesting `net/splitcells/` would result in getting
                                 * `META-INF` and `META-INF/MANIFEST.MF` as well, even though it was not requested.
                                 */
                                return walk.stream().filter(w -> w.startsWith(pathStr))
                                        .map(w -> Path.of(w.toString().substring(basePath.length())));
                            }
                        } else {
                            throw notImplementedYet("Nested jars are not supported yet.");
                        }
                    } catch (Throwable e) {
                        throw executionException(e);
                    }
                } else {
                    // TODO Document when this branch is active.
                    // Loading resources from class loader via the file protocol.
                    final var pathStrWithoutProtocols = resourcePath.toURI().toString().substring(9);
                    final var jarPath = pathStrWithoutProtocols.substring(0, pathStrWithoutProtocols.indexOf("!"));
                    // The jar is provided by the class loader, and thereby this jar is trusted.
                    try (final var jarFile = new JarFile(URLDecoder.decode(jarPath, UTF_8))) {
                        final var jarEntries = jarFile
                                .entries()
                                .asIterator();
                        final List<Path> walk = list();
                        while (jarEntries.hasNext()) {
                            walk.withAppended(Path.of((jarEntries.next().getRealName())));
                        }
                        /*
                         * If the resources are loaded from a jar, the `META-INF` folder is actively filtered afterwards,
                         * in order to avoid walking through it, even it is not request.
                         * For example, without this hack requesting `net/splitcells/` would result in getting
                         * `META-INF` and `META-INF/MANIFEST.MF` as well, even though it was not requested.
                         */
                        return walk.stream().filter(w -> w.startsWith(normalize(basePath + path)))
                                .map(w -> Path.of("./" + (w + "/")
                                        .replace("//", "/")
                                        .toString()
                                        .substring(basePath.length())));
                    }
                }
            } else {
                /* Assume that class loader has resources from target folder of Maven build at the current folder.
                 * Checks if base path is present.
                 */
                final var resource = clazz.getClassLoader().getResource(basePath + "./");
                if (resource == null) {
                    return StreamUtils.emptyStream();
                }
                final var rootPathStr = Path.of(resource.toURI())
                        .toString()
                        .replace("test-classes", "classes")
                        + "/";
                final var startPath = Path.of(clazz.getClassLoader().getResource(basePath + path + "/").toURI());
                return walk_recursively(startPath)
                        .map(p -> Path.of(removePrefix(rootPathStr, p.toString() + "/")));
            }
        } catch (Throwable e) {
            throw executionException(e);
        }

    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        try {
            return clazz.getResourceAsStream("/" + basePath + path.toString()).readAllBytes();
        } catch (IOException e) {
            throw executionException(e);
        }
    }

    public FileSystemView subFileSystemView(String path) {
        return new FileSystemViaClassResources(clazz, (basePath + path + "/").replaceAll("//", "/"));
    }
}
