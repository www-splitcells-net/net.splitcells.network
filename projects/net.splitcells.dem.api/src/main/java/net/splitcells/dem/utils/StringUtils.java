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

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.testing.reporting.ErrorReporter;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.reporting.ErrorReporting.getWithReportedErrors;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class StringUtils {
    private StringUtils() {
        throw constructorIllegal();
    }

    public static String partOf(String arg, String delimiter, int part) {
        return arg.split(delimiter)[part];
    }

    public static StringBuilder stringBuilder() {
        return new StringBuilder();
    }

    public static StringBuilder stringBuilder(String initialContent) {
        return new StringBuilder(initialContent);
    }

    public static String intToOrdinal(int arg) {
        if (arg <= 0) {
            throw execException("Only positive numbers can be converted to an ordinal, which is not the case for the number " + arg + ".");
        }
        if (arg == 1) {
            return "1st";
        } else if (arg == 2) {
            return "2nd";
        } else if (arg == 3) {
            return "3rd";
        }
        return arg + "th";
    }

    public static String removePrefix(String prefix, String target) {
        if (target.startsWith(prefix)) {
            return target.substring(prefix.length());
        }
        throw ExecutionException.execException(tree("The target does not start with the prefix.")
                .withProperty("prefix", prefix)
                .withProperty("target", target));
    }

    public static String removeSuffix(String suffix, String target) {
        if (target.endsWith(suffix)) {
            return target.substring(0, target.lastIndexOf(suffix));
        }
        throw ExecutionException.execException(tree("The target does not end with the suffix.")
                .withProperty("suffix", suffix)
                .withProperty("target", target));
    }

    public static byte[] toBytes(String arg) {
        return arg.getBytes(StandardCharsets.UTF_8);
    }

    public static String parseString(byte[] arg, ErrorReporter reporter) {
        return getWithReportedErrors(() -> parseString(arg), reporter);
    }

    public static String parseString(byte[] arg) {
        return new String(arg, StandardCharsets.UTF_8);
    }

    public static boolean isInt(String arg) {
        return arg.matches("-?\\d+");
    }

    public static int parseInt(String arg) {
        return Integer.parseInt(arg);
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

    public static String mergeSimplifiedCsvList(String a, String b) {
        if (a.isEmpty() && b.isEmpty()) {
            return "";
        }
        if (a.isEmpty()) {
            return b;
        }
        if (b.isEmpty()) {
            return a;
        }
        return a + "," + b;
    }

    public static void requireNonEmptyString(String arg) {
        if (arg.isEmpty()) {
            throw ExecutionException.execException("String should not be empty, but it is empty.");
        }
    }

    public static void requirePrefixAbsence(String subject, String prefix) {
        if (subject.startsWith(prefix)) {
            throw execException(tree("Subject should not start with prefix, but does:")
                    .withProperty("subject", subject)
                    .withProperty("prefix", prefix));
        }
    }

    public static void requireMatch(String arg, Pattern pattern) {
        if (!pattern.matcher(arg).matches())
            throw execException(tree("String does not comply with regex pattern.")
                    .withProperty("String", arg)
                    .withProperty("Pattern", pattern.toString()));
    }
}
