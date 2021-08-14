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
package net.splitcells.dem;

import net.splitcells.dem.data.atom.Bool;

import static net.splitcells.dem.data.atom.Bools.bool;

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
