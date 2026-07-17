/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

@JavaLegacy
public class BinaryUtils {
    private BinaryUtils() {
        throw constructorIllegal();
    }

    public static ByteArrayOutputStream binaryOutputStream() {
        return new ByteArrayOutputStream();
    }

    public static ByteArrayInputStream binaryInputStream(byte[] content) {
        return new ByteArrayInputStream(content);
    }

    public static boolean equalBytes(byte[] expected, byte[] actual) {
        return Arrays.equals(expected, actual);
    }

    public static byte[] emptyByteArray() {
        return new byte[]{};
    }
}
