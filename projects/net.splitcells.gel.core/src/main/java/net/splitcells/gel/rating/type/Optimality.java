package net.splitcells.gel.rating.type;

import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

public class Optimality implements Rating {
    private static final Comparator<Double> OPTIMALITY_VALUE_COMPARATOR = new Comparator<Double>() {
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
        if (arg instanceof Optimality) {
            return Optional.of(OPTIMALITY_VALUE_COMPARATOR.compareTo(value, ((Optimality) arg).value));
        }
        if (arg instanceof Cost) {
            final Cost argCost = ((Cost) arg);
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
    public Rating combine(Rating... addtionalRatings) {
        throw not_implemented_yet();
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
    public Element toDom() {
        final var dom = Xml.elementWithChildren(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + value));
        return dom;
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }

    public double value() {
        return value;
    }
}
