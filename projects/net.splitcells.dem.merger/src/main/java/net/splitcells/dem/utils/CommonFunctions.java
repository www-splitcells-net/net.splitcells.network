/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.splitcells.dem.utils.ExecutionException.executionException;

/**
 * TODO cleanup
 * <p>
 * Caller is the routine which is calling the callee.
 * <p>
 * From the perspective of the caller the thing which is passed is an argument.
 * From the perspective of the routine that receives the call, i.e. the callee,
 * the thing which is passed is a parameter.
 */
@JavaLegacyArtifact
public class CommonFunctions {

    public static <T> T removeAny(Set<T> arg) {
        final T rVal = arg.iterator().next();
        arg.remove(rVal);
        return rVal;
    }

    public static int hashCode(Object... args) {
        return Objects.hash(args);
    }

    public static void makeSystemOutputTracing() {
        final PrintStream outF = new PrintStream(System.out);
        System.setOut(new PrintStream(System.out) {
            public void println(String x) {
                outF.println(x);
                new Throwable().printStackTrace(outF);
            }

            public void println(long x) {
                outF.println(x);
                new Throwable().printStackTrace(outF);
            }

            public void println(Object x) {
                outF.println(x);
                new Throwable().printStackTrace(outF);
            }

            public void println(int x) {
                outF.println(x);
                new Throwable().printStackTrace(outF);
            }
        });
    }

    public static String asString(byte[] content, ContentType contentType) {
        try {
            return new String(content, contentType.codeName());
        } catch (UnsupportedEncodingException e) {
            throw executionException(e);
        }
    }

    public static byte[] getBytes(String content, ContentType contentType) {
        try {
            return content.getBytes(contentType.codeName());
        } catch (UnsupportedEncodingException e) {
            throw executionException(e);
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
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw executionException("Could not create file: " + filePath);
                }
            }
            try (FileOutputStream basicOutput = new FileOutputStream(file);
                 OutputStreamWriter managedOutput = new OutputStreamWriter(basicOutput, "UTF8");
                 FileLock outputFileLock = basicOutput.getChannel().lock()) {
                managedOutput.append(content);
                managedOutput.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO variadic argument support
     */
    @JavaLegacyBody
    public static <T extends Object> T[] concat(T[] a, T[] b) {
        final T[] rVal = (T[]) new Object[a.length + b.length];
        int current_index = 0;

        for (int i = 0; i < a.length; i++) {
            rVal[current_index++] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            rVal[current_index++] = b[i];
        }
        return rVal;
    }

    private CommonFunctions() {
        throw new UnsupportedOperationException();
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @JavaLegacyBody
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
