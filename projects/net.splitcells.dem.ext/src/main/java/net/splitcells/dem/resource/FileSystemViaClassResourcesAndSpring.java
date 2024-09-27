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

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.resource.communication.log.LogLevel;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.perspective;
import static net.splitcells.dem.resource.FileSystemViaClassResourcesImpl.resourceBasePath;
import static net.splitcells.dem.resource.Files.readAsString;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StreamUtils.*;

/**
 * <p>Implements {@link FileSystemView} for the class loader resources based on
 * {@link PathMatchingResourcePatternResolver}.
 * This Spring Core class is used, in order to delegate complex resource loading code to external code.
 * </p>
 * <p>This delegation is optional, in order to minimize the number of required dependencies by avoiding Spring Core
 * inside core projects.
 * The reason for this is its complex implementation and the dependency on the runtime's platform and environment,
 * that in turn causes relative much work.
 * </p>
 * <p>This class seems to be slow with Equinox class loader and Eclipse applications.</p>
 * <p>TODO Test via {@link FileSystemViaClassResourcesTest}.</p>
 */
@JavaLegacyArtifact
public class FileSystemViaClassResourcesAndSpring implements FileSystemView {

    private static final boolean IS_TRACING = Boolean.parseBoolean(System.getProperty("net.splitcells.dem.resource.FileSystemViaClassResourcesAndSpring.IS_TRACING", "false"));

    public static FileSystemView fileSystemViaClassResourcesAndSpring(Class<?> clazz, String groupId
            , String artifactId) {
        return new FileSystemViaClassResourcesAndSpring(clazz, resourceBasePath(groupId, artifactId));
    }

    /**
     * All files accessed via paths has {@link #basePath} as the prefix for these paths.
     */
    private final String basePath;
    private final Class<?> clazz;
    private final PathMatchingResourcePatternResolver resourceResolver;

    private FileSystemViaClassResourcesAndSpring(Class<?> clazz, String basePath) {
        this.clazz = clazz;
        this.basePath = basePath;
        resourceResolver = new PathMatchingResourcePatternResolver(clazz.getClassLoader());
    }

    @Override
    public InputStream inputStream(Path path) {
        return clazz.getResourceAsStream(normalize("/" + basePath + path.toString()));
    }

    @Override
    public String readString(Path path) {
        try {
            return readAsString(resourceResolver.getResource(normalize("/" + basePath + path)).getInputStream());
        } catch (Throwable th) {
            throw executionException(perspective("Could not read file from class path resources:")
                            .withProperty("path requested", path.toString())
                            .withProperty("calculated resource path", path.toString())
                    , th);
        }
    }

    @Override
    public Optional<String> readStringIfPresent(Path path) {
        final var fileContent = resourceResolver.getResource(normalize(path));
        if (fileContent != null) {
            try {
                return Optional.of(readAsString(fileContent.getInputStream()));
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

    @Override
    public boolean isFile(Path path) {
        final var resource = resourceResolver.getResource(normalize(basePath + path));
        if (!resource.exists()) {
            return false;
        }
        try {
            return resource.contentLength() != 0;
        } catch (Throwable t) {
            logs().appendError(t);
            return false;
        }
    }

    private String normalize(String path) {
        return path.replace("./", "").replace("//", "/").replace("\\", "/");
    }

    private String normalize(Path path) {
        return normalize(path.toString());
    }

    @Override
    public boolean isDirectory(Path path) {
        if (IS_TRACING) {
            boolean isDirectory;
            final var resource = resourceResolver.getResource(normalize(basePath + path));
            if (!resource.exists()) {
                isDirectory = false;
            }
            try {
                isDirectory = resource.contentLength() == 0;
            } catch (Throwable t) {
                logs().appendError(t);
                isDirectory = false;
            }
            logs().append(perspective("isDirectory")
                            .withProperty("path", path.toString())
                            .withProperty("clazz", clazz.getName())
                            .withProperty("isDirectory", isDirectory + "")
                    , LogLevel.INFO);
            return isDirectory;
        }
        final var resource = resourceResolver.getResource(normalize(basePath + path));
        if (!resource.exists()) {
            return false;
        }
        try {
            return resource.contentLength() == 0;
        } catch (Throwable t) {
            logs().appendError(t);
            return false;
        }
    }

    @Override
    public Stream<Path> walkRecursively() {
        return walkRecursively(Path.of("/"));
    }

    /**
     * <p>This does not work for {@link #clazz} of Maven artifacts, that are inside a jar of a Eclipse Bundle.
     * Following could load a resource out of such {@link #clazz}: "resourceResolver.getResources(clazz.getClassLoader().getResource(normalize(basePath + path)).toString)"
     * but the following could not walk the resources recursively: "resourceResolver.getResources(clazz.getClassLoader().getResource(normalize(basePath + path)).toString() + "/**")"
     * Also, the resource path of the Equinox class loader is cryptic.</p>
     * <p>In order to mitigate this, following solution was decided:
     * During the build a custom Maven plugin adds a resource to the jar,
     * that contains a list of all resources for that project.
     * The Java program inside the build jar can calculate the name of this resource
     * based on the Maven's artifactId and GroupId and
     * therefore load this resource list in a way, that is supported by most class loaders.
     * This is implemented at {@link FileSystemViaClassResourcesImpl}.
     * </p>
     *
     * @param path
     * @return
     */
    @Override
    public Stream<Path> walkRecursively(Path path) {
        if (isFile(path)) {
            if (IS_TRACING) {
                logs().append(perspective("Call of walkRecursively")
                                .withProperty("path", path.toString())
                                .withProperty("clazz", clazz.getName())
                                .withProperty("result", "Path is hot a folder and therefore nothing can be walked except for the path itself.")
                        , LogLevel.INFO);
            }
            return streamOf(path);
        }
        if (IS_TRACING) {
            if (isDirectory(path)) {
                logs().append(perspective("Call of walkRecursively")
                                .withProperty("path", path.toString())
                                .withProperty("clazz", clazz.getName())
                                .withProperty("result", "Path is a folder.")
                        , LogLevel.INFO);
            } else {
                logs().append(perspective("Call of walkRecursively")
                                .withProperty("path", path.toString())
                                .withProperty("clazz", clazz.getName())
                                .withProperty("result", "Path is not a folder.")
                        , LogLevel.INFO);
            }
        }
        try {
            if (IS_TRACING) {
                logs().append(perspective("Call of walkRecursively")
                                .withProperty("path", path.toString())
                                .withProperty("clazz", clazz.getName())
                                .withProperty("result candidates"
                                        , streamOf(resourceResolver.getResources(normalize((basePath + path + "/**"))))
                                                .filter(r -> r.exists())
                                                .map(r -> r.toString())
                                                .map(p -> p.substring(p.lastIndexOf(basePath) + basePath.length()))
                                                .map(p -> {
                                                    if (p.endsWith("/")) {
                                                        return p.substring(0, p.length() - 1);
                                                    }
                                                    if (p.endsWith("/]")) {
                                                        return p.substring(0, p.length() - 2);
                                                    }
                                                    if (p.endsWith("]")) {
                                                        return p.substring(0, p.length() - 1);
                                                    }
                                                    return p;
                                                })
                                                .collect(Lists.toList()).toString())
                        , LogLevel.INFO);
            }
            final var pathChildren = streamOf(resourceResolver.getResources(normalize((basePath + path + "/**"))))
                    .filter(r -> r.exists())
                    .map(r -> r.toString())
                    .map(p -> p.substring(p.lastIndexOf(basePath) + basePath.length()))
                    .map(p -> {
                        if (p.startsWith("./")) {
                            return p.substring(2);
                        }
                        return p;
                    })
                    .map(p -> {
                        if (p.endsWith("/")) {
                            return p.substring(0, p.length() - 1);
                        }
                        if (p.endsWith("/]")) {
                            return p.substring(0, p.length() - 2);
                        }
                        if (p.endsWith("]")) {
                            return p.substring(0, p.length() - 1);
                        }
                        return p;
                    })
                    .map(Path::of);
            final var pathResource = resourceResolver.getResource(normalize((basePath + path)));
            if (pathResource.exists()) {
                return concat(pathChildren, Stream.of(path));
            }
            return pathChildren;
        } catch (Throwable e) {
            throw executionException(perspective("Could walk resources recursively:")
                            .withProperty("path", path.toString())
                            .withProperty("clazz", clazz.toString())
                    , e);
        }
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        try {
            return resourceResolver.getResource(normalize("/" + basePath + path)).getInputStream().readAllBytes();
        } catch (IOException e) {
            throw executionException(e);
        }
    }

    public FileSystemView subFileSystemView(String path) {
        return new FileSystemViaClassResourcesAndSpring(clazz, (basePath + path + "/").replaceAll("//", "/"));
    }
}
