/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.host;

public class ShellResult {
    public static ShellResult shellResult(int exitCode, String output) {
        return new ShellResult(exitCode, output);
    }

    private final int exitCode;
    private final String output;

    private ShellResult(int exitCode, String output) {
        this.exitCode = exitCode;
        this.output = output;
    }

    public int exitCode() {
        return exitCode;
    }

    public String output() {
        return output;
    }
}
