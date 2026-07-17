/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem;

public class ProcessResult {
    public static ProcessResult processResult() {
        return new ProcessResult();
    }

    private boolean hasError = false;

    private ProcessResult() {
    }

    public void hasError(boolean hasError) {
        this.hasError = hasError;
    }

    public boolean hasError() {
        return hasError;
    }

    public void requireErrorFree() {
        if (hasError) {
            throw new RuntimeException();
        }
    }
}
