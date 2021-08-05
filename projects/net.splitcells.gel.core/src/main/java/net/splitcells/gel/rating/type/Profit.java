/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.rating.type;

import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Optional;

import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

public class Profit implements Rating {
    private static final Comparator<Double> PROFIT_VALUE_COMPARATOR = new Comparator<Double>() {
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
        if (rating instanceof Profit) {
            return Optional.of(PROFIT_VALUE_COMPARATOR.compareTo(value, ((Profit) rating).value()));
        }
        throw new IllegalArgumentException(rating.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Profit combine(Rating... additionalRatings) {
        if (additionalRatings[0] instanceof Profit) {
            final Profit otherCost = (Profit) additionalRatings[0];
            return profit(value + otherCost.value);
        }
        throw notImplementedYet();
    }

    @Override
    public boolean equals(Object other) {
        return compare_partially_to((Rating) other).get().equals(EQUAL);
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
    public Element toDom() {
        final org.w3c.dom.Element dom = Xml.elementWithChildren(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + value));
        return dom;
    }
}
