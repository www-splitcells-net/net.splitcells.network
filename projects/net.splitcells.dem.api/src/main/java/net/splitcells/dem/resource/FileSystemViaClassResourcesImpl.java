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

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Files.readAsString;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * <p>Provides an {@link FileSystem} API for {@link Class#getResource(String)}.
 * Special functionality is supported via the special folders and files,
 * which are created via the Maven plugin `net.splitcells.maven.plugin.resource.list`.</p>
 * <p>Never place multiple {@link FileSystemViaClassResourcesImpl} at the same package across modules or jars,
 * but with a different class name,
 * because this will create hard to debug problems while accessing files.</p>
 * <p>TODO For maximal portability {@link #walkRecursively()} and co.
 * should use a resource file, containing a list of all resource files.
 * Otherwise, relative much support may be required for {@link #walkRecursively()} and co.
 * to work in different environments.
 * This would probably the case for i.e. TeaVM.</p>
 */
@JavaLegacy
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

    private static String metaBasePath(String groupdId, String artifiactId) {
        return groupdId + "." + artifiactId + ".meta/";
    }

    public static String resourceListPath(String groupdId, String artifiactId) {
        return groupdId + "." + artifiactId + ".resources.list.txt";
    }

    public static FileSystemView _fileSystemViaClassResourcesImpl(Class<?> clazz, String groupdId, String artifactId) {
        return new FileSystemViaClassResourcesImpl(clazz, resourceBasePath(groupdId, artifactId)
                , metaBasePath(groupdId, artifactId)
                , resourceListPath(groupdId, artifactId));
    }

    public static FileSystemView _fileSystemViaClassResourcesImpl(Class<?> clazz, String groupdId, String artifactId, Set<String> argResourceList) {
        return new FileSystemViaClassResourcesImpl(clazz, resourceBasePath(groupdId, artifactId)
                , metaBasePath(groupdId, artifactId)
                , argResourceList);
    }

    /**
     * All files accessed via paths has {@link #resourceBasePath} as the prefix for these paths.
     */

    private final String resourceBasePath;
    private final String metaBasePath;
    private final Class<?> clazz;
    private final Set<String> resourceList;

    private FileSystemViaClassResourcesImpl(Class<?> argClazz, String argResourceBasePath
            , String argMetaBasePath
            , Set<String> argResourceList) {
        clazz = argClazz;
        resourceBasePath = argResourceBasePath;
        metaBasePath = argMetaBasePath;
        resourceList = argResourceList;
    }

    private FileSystemViaClassResourcesImpl(Class<?> argClazz, String argResourceBasePath
            , String argMetaBasePath
            , String argResourceListPath) {
        clazz = argClazz;
        resourceBasePath = argResourceBasePath;
        metaBasePath = argMetaBasePath;
        resourceList = setOfUniques();
        final var resourceListContent = argClazz.getResourceAsStream("/" + argResourceListPath);
        if (resourceListContent == null) {
            // TODO TOFIX This only happens, because the `website.server` is dependent on the `network.worker.via.java`.
            logs().warn(execException(tree("Could not provide a file system API for the given class.")
                    .withProperty("Class", argClazz.getName())
                    .withProperty("Base Path", argResourceBasePath)
                    .withProperty("Resource List Path", argResourceListPath)));
        } else {
            for (final var resource : readAsString(resourceListContent).split("\n")) {
                resourceList.add(normalize(resource));
            }
        }
    }

    /**
     * @param path Path starts with `/`.
     * @see {@link #requireValidResourcePath(String)}
     */
    private boolean isResourcePathValid(String path) {
        if (ENFORCING_UNIT_CONSISTENCY) {
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
        if (!resourceList.contains(path.substring(1))) {
            throw ExecutionException.execException(tree("Unknown path requested.")
                    .withProperty("requested path", path));
        }
    }

    @Override
    public InputStream inputStream(Path path) {
        final var resourcePath = normalize("/" + resourceBasePath + path.toString());
        requireValidResourcePath(resourcePath);
        return clazz.getResourceAsStream(resourcePath);
    }

    @Override
    public String readString(Path path) {
        final var resourcePath = normalize("/" + resourceBasePath + path.toString());
        requireValidResourcePath(resourcePath);
        return readAsString(clazz.getResourceAsStream(resourcePath));
    }

    @Override
    public Optional<String> readStringIfPresent(Path path) {
        final var resourcePath = normalize("/" + resourceBasePath + path.toString());
        if (!isResourcePathValid(resourcePath)) {
            return Optional.empty();
        }
        final var fileContent = clazz.getResourceAsStream(resourcePath);
        if (fileContent != null) {
            return Optional.of(readAsString(fileContent));
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
        final var pathStr = normalize(resourceBasePath + path);
        for (final var r : resourceList) {
            if (r.length() == pathStr.length() && r.equals(pathStr)) {
                return true;
            } else if (r.length() == pathStr.length() + 1 && r.equals(pathStr + "/")) {
                return false;
            }
        }
        return false;
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
        return walkRecursively(Path.of("./"));
    }

    /**
     * {@link Path#toString()} never ends with a slash.
     *
     * @param path The folder, whose files and folders are walked recursively.
     * @return A recursive file and folder walk is returned.
     */
    @Override
    public Stream<Path> walkRecursively(Path path) {
        final var pathStr = normalize(resourceBasePath + path.toString());
        final String pathStrFolder;
        if (pathStr.endsWith("/.")) {
            pathStrFolder = pathStr.substring(0, pathStr.length() - 2);
        } else {
            pathStrFolder = pathStr + "/";
        }
        final List<Path> walk = list();
        for (final var resource : resourceList) {
            final var resourceStr = resource;
            if (resourceStr.startsWith(pathStrFolder) || resourceStr.equals(pathStr)) {
                walk.add(Path.of(resourceStr.substring(resourceBasePath.length())));
            }
        }
        return walk.stream();
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        try {
            final var resourcePath = normalize("/" + resourceBasePath + path.toString());
            requireValidResourcePath(resourcePath);
            return clazz.getResourceAsStream(resourcePath).readAllBytes();
        } catch (Throwable e) {
            throw execException(e);
        }
    }

    public FileSystemView subFileSystemView(String path) {
        return new FileSystemViaClassResourcesImpl(clazz
                , normalize(resourceBasePath + path + "/")
                , normalize(metaBasePath + path + "/")
                , resourceList);
    }

    @Override public String toString() {
        return getClass().getName() + " based on " + clazz.getName();
    }

    @Override public Optional<License> license(String path) {
        final var metaPath = normalize("/" + metaBasePath + path.toString());
        if (!isResourcePathValid(metaPath)) {
            return Optional.empty();
        }
        final var fileContent = clazz.getResourceAsStream(metaPath);
        if (fileContent != null) {
            Optional<String> licenseId = Optional.empty();
            Optional<String> copyrightText = Optional.empty();
            for (val line : Files.readAsString(fileContent).split("\\R")) {
                val lineSplit = line.split("=");
                if (lineSplit.length > 1) {
                    if ("SPDX-License-Identifier".equals(lineSplit[0])) {
                        licenseId = Optional.of(lineSplit[1]);
                    } else if ("SPDX-FileCopyrightText".equals(lineSplit[0])) {
                        copyrightText = Optional.of(lineSplit[1]);
                    }
                }
            }
            if (licenseId.isPresent()) {
                val license = License.license(licenseId.get());
                if (copyrightText.isPresent()) {
                    license.setSpdxCopyrightText(copyrightText);
                }
                return Optional.of(license);
            }
        }
        return Optional.empty();
    }
}
