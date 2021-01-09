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
    private static final Comparator<Double> PEĻŅĀS_VERTĪBA_SALĪDZINĀTĀJS = new Comparator<Double>() {
        @Override
        public int compare(Double a, Double b) {
            return a.compareTo(b);
        }
    };
    private double vertība;

    public static Profit bezPeļņas() {
        return new Profit();
    }

    public static Profit peļņa(double vertība) {
        return new Profit(vertība);
    }

    protected Profit() {
        this(0.0);
    }

    protected Profit(double vertība) {
        this.vertība = vertība;
    }

    public double vertība() {
        return vertība;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating novērtējums) {
        if (novērtējums instanceof Profit) {
            return Optional.of(PEĻŅĀS_VERTĪBA_SALĪDZINĀTĀJS.compareTo(vertība, ((Profit) novērtējums).vertība()));
        }
        throw new IllegalArgumentException(novērtējums.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Profit kombinē(Rating... papilduNovērtējums) {
        if (papilduNovērtējums[0] instanceof Profit) {
            final Profit otherPeļņa = (Profit) papilduNovērtējums[0];
            return peļņa(vertība + otherPeļņa.vertība);
        }
        throw not_implemented_yet();
    }

    @Override
    public boolean equals(Object cits) {
        return compare_partially_to((Rating) cits).get().equals(EQUAL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R _clone() {
        return (R) new Profit(vertība);
    }

    @Override
    public Element toDom() {
        final org.w3c.dom.Element dom = element(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + vertība));
        return dom;
    }
}
