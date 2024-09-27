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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.Optional;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.dem.resource.Files.readAsString;
import static net.splitcells.dem.resource.Files.walk_recursively;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.countChar;
import static net.splitcells.dem.utils.StringUtils.removePrefix;

/**
 * <p>Provides an {@link FileSystem} API for {@link Class#getResource(String)}.</p>
 * <p>Never place multiple {@link FileSystemViaClassResourcesImpl} at the same package across modules or jars,
 * but with a different class name,
 * because this will create hard to debug problems while accessing files.</p>
 * <p>TODO For maximal portability {@link #walkRecursively()} and co.
 * should use a resource file, containing a list of all resource files.
 * Otherwise, relative much support may be required for {@link #walkRecursively()} and co.
 * to work in different environments.
 * This would probably the case for i.e. TeaVM.</p>
 */
@JavaLegacyArtifact
public class FileSystemViaClassResourcesImpl implements FileSystemView {

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

    public static String resourceListPath(String groupdId, String artifiactId) {
        return groupdId + "." + artifiactId + ".resources.list.txt";
    }

    public static FileSystemView _fileSystemViaClassResourcesImpl(Class<?> clazz, String groupdId, String artifactId) {
        return new FileSystemViaClassResourcesImpl(clazz, resourceBasePath(groupdId, artifactId)
                , resourceListPath(groupdId, artifactId));
    }

    /**
     * All files accessed via paths has {@link #basePath} as the prefix for these paths.
     */
    private final String basePath;
    private final Class<?> clazz;
    private Set<String> resourceList;
    private final boolean populatedResourceList;

    private FileSystemViaClassResourcesImpl(Class<?> clazz, String basePath, Set<String> resourceListArg, boolean populatedResourceListArg) {
        this.clazz = clazz;
        this.basePath = basePath;
        this.resourceList = resourceListArg;
        populatedResourceList = populatedResourceListArg;
    }

    private FileSystemViaClassResourcesImpl(Class<?> clazz, String basePath, String resourceListPath) {
        this.clazz = clazz;
        this.basePath = basePath;
        resourceList = setOfUniques();
        final var resourceListContent = clazz.getResourceAsStream("/" + resourceListPath);
        populatedResourceList = resourceListContent != null;
        if (populatedResourceList) {
            for (final var resource : readAsString(resourceListContent).split("\n")) {
                resourceList.add(normalize(resource));
            }
        }

    }

    /**
     * @see {@link #requireValidResourcePath(String)}
     * @param path Path starts with `/`.
     */
    private boolean isResourcePathValid(String path) {
        if (populatedResourceList && ENFORCING_UNIT_CONSISTENCY) {
            return resourceList.contains(path.substring(1));
        }
        return true;
    }

    /**
     * This method exists, in order to double-check, if a resource exists.
     * It tries to catch a situation, where a resource is present in the class loader,
     * but is not present in the {@link #resourceList}.
     * Thereby, invalid builds are attempted to be found.
     *
     * @param path Path starts with `/`.
     */
    private void requireValidResourcePath(String path) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            if (populatedResourceList) {
                if (!resourceList.contains(path.substring(1))) {
                    throw executionException(perspective("Unknown path requested.")
                            .withProperty("requested path", path));
                }
            } else if (resourceList.hasElements()) {
                throw executionException(perspective(getClass().getName() + " is not consistent, because the resource list has elements even though it is not populated.")
                        .withProperty("Is resource list populated?", "" + populatedResourceList)
                        .withProperty("Resource List", resourceList.toString())
                        .withProperty("Does resource exist natively?", "" + (clazz.getResourceAsStream(path) != null)));
            }
        }
    }

    @Override
    public InputStream inputStream(Path path) {
        final var resourcePath = normalize("/" + basePath + path.toString());
        requireValidResourcePath(resourcePath);
        return clazz.getResourceAsStream(resourcePath);
    }

    @Override
    public String readString(Path path) {
        final var resourcePath = normalize("/" + basePath + path.toString());
        requireValidResourcePath(resourcePath);
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
    public Optional<String> readStringIfPresent(Path path) {
        final var resourcePath = normalize("/" + basePath + path.toString());
        final var fileContent = clazz.getResourceAsStream(resourcePath);
        if (fileContent != null && isResourcePathValid(resourcePath)) {
            try {
                return Optional.of(readAsString(fileContent));
            } catch (Throwable th) {
                throw executionException(perspective("Could not optionally read file from class path resources:")
                                .withProperty("path requested", path.toString())
                                .withProperty("calculated resource path", path.toString())
                        , th);
            }
        }
        return Optional.empty();
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
        if (populatedResourceList) {
            final var pathStr = normalize(basePath + path);
            for (final var r : resourceList) {
                if (r.length() == pathStr.length() && r.equals(pathStr)) {
                    return true;
                } else if (r.length() == pathStr.length() + 1 && r.equals(pathStr + "/")) {
                    return false;
                }
            }
            return false;
        }
        final var pathStr = normalize("/" + basePath + path);
        final var inputStream = clazz.getResourceAsStream(pathStr);
        return inputStream != null
                && Files.readAsString(inputStream).length() != 0
                && walkRecursively(path)
                .filter(p -> !path.toString().equals(p.toString()))
                .count() == 0;
    }

    private String normalize(String path) {
        return path
                .replace("\\", "/")
                .replace("./", "")
                .replace("//", "/");
    }

    @Override
    public boolean isDirectory(Path path) {
        return walkRecursively(path).collect(Lists.toList()).size() > 1;
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
     * of {@link FileSystemViaClassResourcesImpl}.
     */
    private String defaultProtocol() {
        var testResource = FileSystemViaClassResourcesImpl.class.getClassLoader()
                .getResource("net/");
        if (testResource == null) {
            testResource = FileSystemViaClassResourcesImpl.class.getClassLoader()
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
            if (populatedResourceList) {
                final var pathStr = normalize(basePath + path.toString());
                final var pathStrFolder = pathStr + "/";
                final List<Path> walk = list();
                for (final var resource : resourceList) {
                    final var resourceStr = resource;
                    if (resourceStr.startsWith(pathStrFolder) || resourceStr.equals(pathStr)) {
                        walk.add(Path.of(resourceStr.substring(basePath.length())));
                    }
                }
                return walk.stream();
            }
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
            final var resourcePath = normalize("/" + basePath + path.toString());
            requireValidResourcePath(resourcePath);
            return clazz.getResourceAsStream(resourcePath).readAllBytes();
        } catch (IOException e) {
            throw executionException(e);
        }
    }

    public FileSystemView subFileSystemView(String path) {
        return new FileSystemViaClassResourcesImpl(clazz
                , normalize(basePath + path + "/")
                , resourceList
                , populatedResourceList);
    }
}
