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
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.executionException;

@JavaLegacyArtifact
public class StringUtils {
    private StringUtils() {
        throw constructorIllegal();
    }

    public static String partOf(String arg, String delimiter, int part) {
        return arg.split(delimiter)[part];
    }

    public static String removePrefix(String prefix, String target) {
        if (target.startsWith(prefix)) {
            return target.substring(prefix.length());
        }
        throw executionException(perspective("`target` does not start with `prefix`.")
                .withProperty("prefix", prefix)
                .withProperty("target", target));
    }

    public static byte[] toBytes(String arg) {
        return arg.getBytes(StandardCharsets.UTF_8);
    }

    public static String parseString(byte[] arg) {
        return new String(arg, StandardCharsets.UTF_8);
    }

    public static int countChar(String string, char character) {
        var count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }

    public static String multiple(String base, int factor) {
        final var multiple = new StringBuilder();
        IntStream.range(0, factor).forEach(i -> {
            multiple.append(base);
        });
        return multiple.toString();
    }

    public static String throwableToString(Throwable th) {
        final var stackTraceValue = new java.io.StringWriter();
        final var stackTracePrinter = new java.io.PrintWriter(stackTraceValue);
        th.printStackTrace(stackTracePrinter);
        stackTracePrinter.flush();
        return stackTraceValue.toString();
    }
}
