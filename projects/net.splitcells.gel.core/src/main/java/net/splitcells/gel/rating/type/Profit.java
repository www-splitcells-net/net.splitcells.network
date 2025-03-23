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
package net.splitcells.gel.rating.type;

import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Optional;

import net.splitcells.dem.data.order.Comparison;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.dem.data.order.Ordering;

public class Profit implements Rating {
    private static final Comparison<Double> PROFIT_VALUE_COMPARISON = new Comparison<Double>() {
        @Override
        public int compare(Double a, Double b) {
            return a.compareTo(b);
        }
    };
    private double value;

    public static Profit withoutProfit() {
        return new Profit();
    }

    public static Profit profit(double value) {
        return new Profit(value);
    }

    protected Profit() {
        this(0.0);
    }

    protected Profit(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating rating) {
        if (rating instanceof Profit argProfit) {
            return Optional.of(PROFIT_VALUE_COMPARISON.compareTo(value, argProfit.value()));
        }
        throw new IllegalArgumentException(rating.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Profit combine(Rating... additionalRatings) {
        if (additionalRatings[0] instanceof Profit otherCost) {
            return profit(value + otherCost.value);
        }
        throw notImplementedYet();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Rating) {
            return compare_partially_to((Rating) other).orElseThrow().equals(EQUAL);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R _clone() {
        return (R) new Profit(value);
    }

    @Override
    public boolean betterThan(Rating rating) {
        return greaterThan(rating);
    }

    @Override
    public Tree toTree() {
        return tree(this.getClass().getSimpleName()).withChild(tree("" + value));
    }
}
