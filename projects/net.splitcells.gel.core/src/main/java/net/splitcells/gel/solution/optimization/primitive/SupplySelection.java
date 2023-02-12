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
