package net.splitcells.gel.rating.type;

import static net.splitcells.dem.lang.Xml.element;
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
    private static final Comparator<Double> OPTIMĀLUMU_VĒRTIBAS_SALĪDZINĀTĀJS = new Comparator<Double>() {
        @Override
        public int compare(Double a, Double b) {
            return a.compareTo(b);
        }
    };

    public static Optimality optimālums() {
        return optimālums(0.0);
    }

    public static Optimality optimālums(double optimality) {
        return new Optimality(optimality);
    }

    private double vertība;

    protected Optimality(double vertība) {
        assertThat(vertība).isBetween(0.0, 1.0);
        this.vertība = vertība;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof Optimality) {
            return Optional.of(OPTIMĀLUMU_VĒRTIBAS_SALĪDZINĀTĀJS.compareTo(vertība, ((Optimality) arg).vertība));
        }
        if (arg instanceof Cost) {
            final Cost argCena = ((Cost) arg);
            if (vertība == 1 && argCena.vertība() == 0) {
                return Optional.of(EQUAL);
            }
            if (vertība == 1 && argCena.vertība() > 0) {
                return Optional.of(Ordering.GREATER_THAN);
            }
            return Optional.empty();
        }
        throw new IllegalArgumentException(arg.getClass().getName());
    }

    @Override
    public Rating kombinē(Rating... additionalNovērtējums) {
        throw not_implemented_yet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R _clone() {
        return (R) new Optimality(vertība);
    }

    @Override
    public Element toDom() {
        final var dom = element(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + vertība));
        return dom;
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }

    public double vertība() {
        return vertība;
    }
}
