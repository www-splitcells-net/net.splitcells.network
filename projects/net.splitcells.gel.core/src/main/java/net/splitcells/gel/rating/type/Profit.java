package net.splitcells.gel.rating.type;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

import java.util.Optional;

import net.splitcells.gel.rating.structure.Rating;
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
        throw not_implemented_yet();
    }

    @Override
    public boolean equals(Object other) {
        return compare_partially_to((Rating) other).get().equals(EQUAL);
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
        final org.w3c.dom.Element dom = element(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + value));
        return dom;
    }
}
