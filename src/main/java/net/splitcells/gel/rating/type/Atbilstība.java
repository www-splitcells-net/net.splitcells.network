package net.splitcells.gel.rating.type;

import static java.util.Arrays.asList;
import static net.splitcells.dem.data.order.Comparator.comparator_;
import static net.splitcells.dem.lang.Xml.element;

import java.util.Optional;

import net.splitcells.gel.rating.structure.Rating;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

public class Atbilstība implements Rating {
    private static final Comparator<Boolean> SALĪDYINĀTĀJS = comparator_((a, b) -> Boolean.compare(a, b));
    private boolean vertība;

    public static Atbilstība atbilstība(boolean vertība) {
        return new Atbilstība(vertība);
    }

    protected Atbilstība(boolean vertība) {
        this.vertība = vertība;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof Atbilstība) {
            return Optional.of(SALĪDYINĀTĀJS.compareTo(vertība, ((Atbilstība) arg).vertība));
        }
        throw new IllegalArgumentException(arg.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Atbilstība kombinē(Rating... papilduNovērtējums) {
        if (papilduNovērtējums[0] instanceof Atbilstība) {
            return atbilstība(vertība && ((Atbilstība) papilduNovērtējums[0]).vertība);
        }
        throw new IllegalArgumentException(asList(papilduNovērtējums).toString());
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Atbilstība) {
            return this.vertība == ((Atbilstība) arg).vertība;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R _clone() {
        return (R) new Atbilstība(vertība);
    }

    @Override
    public Element toDom() {
        final var dom = element(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + vertība));
        return dom;
    }
}
