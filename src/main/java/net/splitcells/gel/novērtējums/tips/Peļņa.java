package net.splitcells.gel.novērtējums.tips;

import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

import java.util.Optional;

import net.splitcells.gel.novērtējums.struktūra.Novērtējums;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

public class Peļņa implements Novērtējums {
    private static final Comparator<Double> PEĻŅĀS_VERTĪBA_SALĪDZINĀTĀJS = new Comparator<Double>() {
        @Override
        public int compare(Double a, Double b) {
            return a.compareTo(b);
        }
    };
    private double vertība;

    public static Peļņa bezPeļņas() {
        return new Peļņa();
    }

    public static Peļņa peļņa(double vertība) {
        return new Peļņa(vertība);
    }

    protected Peļņa() {
        this(0.0);
    }

    protected Peļņa(double vertība) {
        this.vertība = vertība;
    }

    public double vertība() {
        return vertība;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Novērtējums novērtējums) {
        if (novērtējums instanceof Peļņa) {
            return Optional.of(PEĻŅĀS_VERTĪBA_SALĪDZINĀTĀJS.compareTo(vertība, ((Peļņa) novērtējums).vertība()));
        }
        throw new IllegalArgumentException(novērtējums.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Peļņa kombinē(Novērtējums... papilduNovērtējums) {
        if (papilduNovērtējums[0] instanceof Peļņa) {
            final Peļņa otherPeļņa = (Peļņa) papilduNovērtējums[0];
            return peļņa(vertība + otherPeļņa.vertība);
        }
        throw not_implemented_yet();
    }

    @Override
    public boolean equals(Object cits) {
        return compare_partially_to((Novērtējums) cits).get().equals(EQUAL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Novērtējums> R _clone() {
        return (R) new Peļņa(vertība);
    }

    @Override
    public Element toDom() {
        final org.w3c.dom.Element dom = element(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + vertība));
        return dom;
    }
}
