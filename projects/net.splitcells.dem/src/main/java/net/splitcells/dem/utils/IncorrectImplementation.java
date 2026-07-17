/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils;

public class IncorrectImplementation extends RuntimeException {
    public static IncorrectImplementation incorrectImplementation(String message) {
        return new IncorrectImplementation(message);
    }

    private IncorrectImplementation(String message) {
        super(message);
    }
}

