package net.splitcells.gel.rating.tips;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import net.splitcells.gel.rating.struktūra.Novērtējums;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

public class Optimālums implements Novērtējums {
    private static final Comparator<Double> OPTIMĀLUMU_VĒRTIBAS_SALĪDZINĀTĀJS = new Comparator<Double>() {
        @Override
        public int compare(Double a, Double b) {
            return a.compareTo(b);
        }
    };

    public static Optimālums optimālums() {
        return optimālums(0.0);
    }

    public static Optimālums optimālums(double optimality) {
        return new Optimālums(optimality);
    }

    private double vertība;

    protected Optimālums(double vertība) {
        assertThat(vertība).isBetween(0.0, 1.0);
        this.vertība = vertība;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Novērtējums arg) {
        if (arg instanceof Optimālums) {
            return Optional.of(OPTIMĀLUMU_VĒRTIBAS_SALĪDZINĀTĀJS.compareTo(vertība, ((Optimālums) arg).vertība));
        }
        if (arg instanceof Cena) {
            final Cena argCena = ((Cena) arg);
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
    public Novērtējums kombinē(Novērtējums... additionalNovērtējums) {
        throw not_implemented_yet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Novērtējums> R _clone() {
        return (R) new Optimālums(vertība);
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
