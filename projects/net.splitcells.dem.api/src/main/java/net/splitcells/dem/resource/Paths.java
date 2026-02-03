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

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.requireMatch;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>TODO REMOVE Keep in mind, that the Java's Path API is deprecated in this project.</p>
 * <p>TODO RENAME because it conflicts with {@link java.nio.file.Paths}.</p>
 * <p>
 * TODO Create an own implementation of path like data structure. Such object should have
 * an conversion method, that transforms the path to a file like object, in order to make it easy to interact with it.
 * Note, that the transform method should accept any kind of backend for the filesystem.
 */
@JavaLegacy
public final class Paths {
    /**
     * TODO Only allow special symbols like $ via a flag.
     */
    private static final Pattern PATH_ELEMENT_SYNTAX = Pattern.compile("[a-zA-Z0-9\\-\\.-_$]*");

    private Paths() {
        throw constructorIllegal();
    }

    public static Path path(String root, String... elements) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            requireMatch(root, PATH_ELEMENT_SYNTAX);
            asList(elements).forEach(element -> requireMatch(element, PATH_ELEMENT_SYNTAX));
        }
        return java.nio.file.Paths.get(root, elements);
    }

    public static Path userHome(String... relativePath) {
        return path(System.getProperty("user.home"), relativePath);
    }

    public static Path path(Path root, String... elements) {
        return path(root, asList(elements));
    }

    public static Path path(Path root, Collection<String> elements) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            elements.forEach(element -> assertThat(element).matches(PATH_ELEMENT_SYNTAX));
        }
        var rVal = root;
        for (final String element : elements) {
            rVal = rVal.resolve(element);
        }
        return rVal;
    }

    public static String removeFileSuffix(String fileName) {
        try {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        } catch (Throwable th) {
            throw ExecutionException.execException("Could not remove suffix from file name: " + fileName, th);
        }
    }

    public static String toString(Path path) {
        return path.toString().replace('\\', '/');
    }
}
