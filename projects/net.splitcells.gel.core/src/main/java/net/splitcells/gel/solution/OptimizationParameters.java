/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution;

import net.splitcells.dem.lang.annotations.ReturnsThis;

public class OptimizationParameters {
    public static OptimizationParameters optimizationParameters() {
        return new OptimizationParameters();
    }
    private boolean dublicateRemovalAllowed = false;
    private OptimizationParameters() {
        
    }
    @ReturnsThis
    public OptimizationParameters withDublicateRemovalAllowed(boolean arg) {
        dublicateRemovalAllowed = arg;
        return this;
    }

    public boolean dublicateRemovalAllowed() {
        return dublicateRemovalAllowed;
    }
}
