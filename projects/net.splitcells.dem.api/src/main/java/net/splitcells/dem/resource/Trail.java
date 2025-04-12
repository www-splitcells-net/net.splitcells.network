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

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.object.Equality;
import net.splitcells.dem.utils.ExecutionException;

import java.nio.file.Path;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>TODO Ensure that only trails, that adhere to guidelines are accepted.</p>
 * <p>Represents a path for things like file systems.
 * It is called {@link Trail} instead of Path in order to avoid confusion with Java's {@link Path} class.
 * It has similar functionality as {@link Path}, but has no protocol or binding to any file system driver.
 * This also avoids the distinction between relative and absolute {@link Path} instances,
 * as only relative paths are allowed.
 * Thereby bugs are avoided,
 * that are created via {@link Path#resolve(Path)} by accidentally mixing relative and absolute paths.</p>
 */
public class Trail implements Thing, Equality<Trail> {
    public static Trail trail(String... content) {
        return new Trail(listWithValuesOf(content).stream().filter(e -> !e.isEmpty()).collect(toList()));
    }

    public static Trail trail(List<String> content) {
        return new Trail(content);
    }

    public static Trail trail(String path) {
        return new Trail(listWithValuesOf(path.split("/")).stream().filter(e -> !e.isEmpty()).collect(toList()));
    }

    private final List<String> content;

    private Trail(List<String> contentArg) {
        this.content = contentArg;
    }

    public ListView<String> content() {
        return content;
    }

    public String unixPathString() {
        return content.stream()
                .filter(e -> !e.isEmpty())
                .reduce((a, b) -> a + "/" + b).orElse("");
    }

    public Path javaLegacyPath() {
        return Path.of(unixPathString());
    }

    public String toString() {
        return unixPathString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trail other) {
            return content.equals(other.content);
        } else {
            throw ExecutionException.execException("Illegal argument: " + obj);
        }
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(content);
    }

    /**
     * Determines how many levels of parent directories, the start of the path is located at.
     *
     * @param path This is a Unix path.
     * @return The number of `../` at the beginning of the path.
     */
    public static int parentCount(String path) {
        final var split = path.replace("//", "/").split("/");
        int count = 0;
        for (int i = 0; i < split.length; i++) {
            if ("..".equals(split[i])) {
                count++;
            } else {
                return count;
            }
        }
        return count;
    }

    /**
     * @param path             This is the relative path to be processed.
     * @param elementsToRemove Number of starting elements to remove.
     * @return Path without the given number of element at the start given the path argument.
     */
    public static String withoutPrefixElements(String path, int elementsToRemove) {
        final var split = path.replaceAll("/+", "/").split("/");
        String result = "";
        if (path.startsWith("/")) {
            throw ExecutionException.execException(tree("Only relative paths are supported.")
                    .withProperty("path", path)
                    .withProperty("elementsToRemove", elementsToRemove + ""));
        }
        if (split.length < elementsToRemove) {
            throw ExecutionException.execException(tree("Trying to remove more prefix elements from path than present.")
                    .withProperty("path", path)
                    .withProperty("elementsToRemove", elementsToRemove + ""));
        }
        if (0 > elementsToRemove) {
            throw ExecutionException.execException(tree("Cannot remove negative amount of prefix elements.")
                    .withProperty("path", path)
                    .withProperty("elementsToRemove", elementsToRemove + ""));
        }
        var first = true;
        for (int i = 0; i + elementsToRemove < split.length; i++) {
            if (first) {
                result += split[i + elementsToRemove];
                first = false;
            } else {
                result += "/" + split[i + elementsToRemove];
            }

        }
        return result;
    }

    /**
     * @param path             This is the relative path to be processed.
     * @param elementsToRemove Number of ending elements to remove.
     * @return Path without the given number of element at the end given the path argument.
     */
    public static String withoutSuffixElements(String path, int elementsToRemove) {
        final var split = path.replaceAll("/+", "/").split("/");
        String result = "";
        if (path.startsWith("/")) {
            throw ExecutionException.execException(tree("Only relative paths are supported.")
                    .withProperty("path", path)
                    .withProperty("elementsToRemove", elementsToRemove + ""));
        }
        if (split.length < elementsToRemove) {
            throw ExecutionException.execException(tree("Trying to remove more suffix elements from path than present.")
                    .withProperty("path", path)
                    .withProperty("elementsToRemove", elementsToRemove + ""));
        }
        if (0 > elementsToRemove) {
            throw ExecutionException.execException(tree("Cannot remove negative amount of suffix elements.")
                    .withProperty("path", path)
                    .withProperty("elementsToRemove", elementsToRemove + ""));
        }
        var first = true;
        for (int i = 0; i < split.length - elementsToRemove; i++) {
            if (first) {
                result += split[i];
                first = false;
            } else {
                result += "/" + split[i];
            }

        }
        return result;
    }

    public static int elementCount(String path) {
        var normalizedPath = path.replaceAll("/+", "/");
        if (normalizedPath.startsWith("/") && normalizedPath.length() > 1) {
            normalizedPath = normalizedPath.substring(1);
        }
        if (normalizedPath.endsWith("/") && normalizedPath.length() > 1) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath.split("/").length;
    }

    @Override
    public <A extends Trail> boolean equalContents(A arg) {
        return content.equals(arg.content());
    }
}
