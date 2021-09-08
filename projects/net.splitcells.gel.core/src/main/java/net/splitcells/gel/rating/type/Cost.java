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
package net.splitcells.gel.rating.type;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Objects;
import java.util.Optional;

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
    protected static final Comparator<Double> COST_VALUE_COMPARATOR = new Comparator<Double>() {
        @Override
        public Ordering compareTo(Double a, Double b) {
            if (Objects.equals(a, b)) {
                return Ordering.EQUAL;
            } else if (a < b) {
                return Ordering.LESSER_THAN;
            } else if (a > b) {
                return Ordering.GREATER_THAN;
            } else {
                throw new IllegalArgumentException();
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

    protected Cost(double value) {
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
        return compare_partially_to((Rating) ob).get().equals(EQUAL);
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
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
