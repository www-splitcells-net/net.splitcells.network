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
import net.splitcells.dem.resource.ContentType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * TODO cleanup
 * <p>
 * Caller is the routine which is calling the callee.
 * <p>
 * From the perspective of the caller the thing which is passed is an argument.
 * From the perspective of the routine that receives the call, i.e. the callee,
 * the thing which is passed is a parameter.
 */
@JavaLegacy
public class CommonFunctions {

    public static int hashCode(Object... args) {
        return Objects.hash(args);
    }

    public static void makeSystemOutputTracing() {
        final PrintStream outF = new PrintStream(System.out);
        System.setOut(new PrintStream(System.out) {
            @Override
            public void println(String x) {
                outF.println(x);
                new Throwable().printStackTrace(outF);
            }

            @Override
            public void println(long x) {
                outF.println(x);
                new Throwable().printStackTrace(outF);
            }

            @Override
            public void println(Object x) {
                outF.println(x);
                new Throwable().printStackTrace(outF);
            }

            @Override
            public void println(int x) {
                outF.println(x);
                new Throwable().printStackTrace(outF);
            }
        });
        final PrintStream errF = new PrintStream(System.err);
        System.setErr(new PrintStream(System.err) {
            @Override
            public void println(String x) {
                errF.println(x);
                new Throwable().printStackTrace(errF);
            }

            @Override
            public void println(long x) {
                errF.println(x);
                new Throwable().printStackTrace(errF);
            }

            @Override
            public void println(Object x) {
                errF.println(x);
                new Throwable().printStackTrace(errF);
            }

            @Override
            public void println(int x) {
                errF.println(x);
                new Throwable().printStackTrace(errF);
            }
        });
    }

    public static String asString(byte[] content, ContentType contentType) {
        try {
            return new String(content, contentType.codeName());
        } catch (UnsupportedEncodingException e) {
            throw execException(e);
        }
    }

    public static byte[] getBytes(String content, ContentType contentType) {
        try {
            return content.getBytes(contentType.codeName());
        } catch (UnsupportedEncodingException e) {
            throw execException(e);
        }
    }

    public static String asString(Throwable arg) {
        return arg.getMessage() + "\n" + CommonFunctions.stackTraceString(arg);
    }

    public static String stackTraceString(Throwable arg) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        arg.printStackTrace(pw);
        return sw.toString();
    }

    public static void appendToFile(Path filePath, String content) {
        try {
            File file = filePath.toFile();
            if (!file.exists() && !file.createNewFile()) {
                throw ExecutionException.execException("Could not create file: " + filePath);
            }
            try (FileOutputStream basicOutput = new FileOutputStream(file);
                 OutputStreamWriter managedOutput = new OutputStreamWriter(basicOutput, StandardCharsets.UTF_8);
                 FileLock outputFileLock = basicOutput.getChannel().lock()) {
                managedOutput.append(content);
                managedOutput.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SafeVarargs
    private static <T> T[] createArray(int capacity, T... dummyArray) {
        return Arrays.copyOf(dummyArray, capacity);
    }

    /**
     * TODO variadic argument support
     */
    @JavaLegacy
    public static <T extends Object> T[] concat(T[] a, T[] b) {
        final T[] rVal = CommonFunctions.<T>createArray(a.length + b.length);
        int currentIndex = 0;

        for (int i = 0; i < a.length; i++) {
            rVal[currentIndex++] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            rVal[currentIndex++] = b[i];
        }
        return rVal;
    }

    private CommonFunctions() {
        throw new UnsupportedOperationException();
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @JavaLegacy
    public static Stream<String> selectMatchesByRegex(String string, Pattern pattern, int group) {
        Stream.Builder<String> matches = Stream.builder();
        try (Scanner scanner = new Scanner(string)) {
            while (scanner.findWithinHorizon(pattern, 0) != null) {
                matches.accept(scanner.match().group(group));
            }
        }
        return matches.build();
    }
}
