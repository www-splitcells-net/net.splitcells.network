/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.rating.framework.Rating;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class AllocationRating implements MetaData<Rating> {

    public static AllocationRating allocationRating(Rating rating) {
        return new AllocationRating(rating);
    }

    private final Rating rating;

    private AllocationRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public Rating value() {
        return rating;
    }

    @Override
    public Tree toTree() {
        return tree(getClass().getSimpleName()).withChild(rating.toTree());
    }
}
