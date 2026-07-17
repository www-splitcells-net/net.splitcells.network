/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.tree.Tree;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class AllocationNaturalArgumentation implements MetaData<String> {
    public static AllocationNaturalArgumentation allocationNaturalArgumentation(String value) {
        return new AllocationNaturalArgumentation(value);
    }

    private final String value;

    private AllocationNaturalArgumentation(String argValue) {
        this.value = argValue;
    }

    @Override
    public Tree toTree() {
        return tree(value);
    }

    @Override
    public String value() {
        return value;
    }
}
