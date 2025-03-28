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
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import net.splitcells.dem.data.order.Comparison;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.dem.data.order.Ordering;

/**
 * Signals with a {@link #value} of zero,
 * that a solution has the worst possible {@link Rating}.
 * Signals with a {@link #value} of one,
 * that the rated subject has the best possible {@link Rating}.
 * {@link #value}s between zero and one signal,
 * how close the object's {@link Rating} is to the optimal
 * or worst solution.
 */
public class Optimality implements Rating {
    private static final Comparison<Double> OPTIMALITY_VALUE_COMPARISON = new Comparison<Double>() {
        @Override
        public int compare(Double a, Double b) {
            return a.compareTo(b);
        }
    };

    public static Optimality optimality() {
        return optimality(0.0);
    }

    public static Optimality optimality(double value) {
        return new Optimality(value);
    }

    private double value;

    protected Optimality(double value) {
        assertThat(value).isBetween(0.0, 1.0);
        this.value = value;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof Optimality optimality) {
            return Optional.of(OPTIMALITY_VALUE_COMPARISON.compareTo(value, optimality.value));
        }
        if (arg instanceof Cost argCost) {
            if (value == 1 && argCost.value() == 0) {
                return Optional.of(EQUAL);
            }
            if (value == 1 && argCost.value() > 0) {
                return Optional.of(Ordering.GREATER_THAN);
            }
            return Optional.empty();
        }
        throw new IllegalArgumentException(arg.getClass().getName());
    }

    @Override
    public Rating combine(Rating... additionalRatings) {
        throw notImplementedYet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R _clone() {
        return (R) new Optimality(value);
    }

    @Override
    public boolean betterThan(Rating rating) {
        return greaterThan(rating);
    }

    @Override
    public Tree toTree() {
        return tree(this.getClass().getSimpleName()).withChild(tree("" + value));
    }

    @Override
    public String toString() {
        return toTree().toXmlString();
    }

    public double value() {
        return value;
    }
}
