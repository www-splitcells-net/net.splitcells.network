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

import static net.splitcells.dem.data.atom.Bools.bool;
import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Optional;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.rating.framework.MetaRating;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

/**
 * The higher the value of a {@link Cost}'s instance, the more valuable the rated thing is.
 * The {@link Cost}'s value cannot be below zero and a {@link Cost} of zero means, that
 * now defiances are present.
 */
public class Cost implements Rating {
    private static final Cost NO_COST = cost(0.0);
    private static final Comparator<Double> COST_VALUE_COMPARATOR = new Comparator<Double>() {
        @Override
        public Ordering compareTo(Double a, Double b) {
            if (Thing.equals(a, b)) {
                return Ordering.EQUAL;
            } else if (a < b) {
                return Ordering.LESSER_THAN;
            } else {
                if (ENFORCING_UNIT_CONSISTENCY) {
                    bool(a > b).required();
                }
                return Ordering.GREATER_THAN;
            }
        }
    };
    private final double value;

    public static Cost cost(double value) {
        return new Cost(value);
    }

    public static Cost noCost() {
        return NO_COST;
    }

    private Cost(double value) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            if (value < 0) {
                throw new IllegalArgumentException("" + value);
            }
        }
        this.value = value;
    }

    public double value() {
        return value;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof Cost) {
            final var order = COST_VALUE_COMPARATOR.compareTo(value, ((Cost) arg).value());
            if (order.equals(EQUAL)) {
                return Optional.of(EQUAL);
            } else if (order.equals(LESSER_THAN)) {
                return Optional.of(LESSER_THAN);
            } else if (order.equals(GREATER_THAN)) {
                return Optional.of(GREATER_THAN);
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (arg instanceof Optimality) {
            final Optimality argOptimality = ((Optimality) arg);
            if (argOptimality.value() == 1 && value == 0) {
                return Optional.of(EQUAL);
            }
            if (argOptimality.value() == 1 && value > 0) {
                return Optional.of(LESSER_THAN);
            }
            return Optional.empty();
        }
        throw new IllegalArgumentException(arg.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Rating combine(Rating... additionalRatings) {
        if (additionalRatings.length == 1) {
            final Rating additionalRating = additionalRatings[0];
            if (additionalRating instanceof Cost) {
                final Cost otherCost = (Cost) additionalRating;
                return cost(value + otherCost.value);
            }
            if (additionalRating instanceof MetaRating) {
                return additionalRating.combine(this);
            }
            if (additionalRating instanceof Optimality) {
                final Optimality additionalOptimality = (Optimality) additionalRating;
                if (additionalOptimality.value() == 1 && value == 0) {
                    throw notImplementedYet();
                }
            }
        }
        throw notImplementedYet();
    }

    @Override
    public boolean equals(Object ob) {
        if (ob instanceof Rating) {
            return compare_partially_to((Rating) ob).orElseThrow().equals(EQUAL);
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
        return (R) new Cost(value);
    }

    @Override
    public boolean betterThan(Rating rating) {
        return smallerThan(rating);
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + value));
        return dom;
    }

    @Override
    public Perspective toPerspective() {
        return perspective(this.getClass().getSimpleName()).withChild(perspective("" + value));
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
