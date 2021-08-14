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
package net.splitcells.gel.solution.optimization.primitive;

public final class SupplySelection {

    public static SupplySelection supplySelection(int selectedIndex, boolean currentlyFree) {
        return new SupplySelection(selectedIndex, currentlyFree);
    }

    private final int selectedIndex;
    private final boolean isCurrentlyFree;
    
    private SupplySelection(int selectedIndex, boolean isCurrentlyFree) {
        this.selectedIndex = selectedIndex;
        this.isCurrentlyFree = isCurrentlyFree;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public boolean isCurrentlyFree() {
        return isCurrentlyFree;
    }
}
