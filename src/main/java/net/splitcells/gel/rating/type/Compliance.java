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

public class Compliance implements Rating {
    private static final Comparator<Boolean> SALĪDYINĀTĀJS = comparator_((a, b) -> Boolean.compare(a, b));
    private boolean vertība;

    public static Compliance atbilstība(boolean vertība) {
        return new Compliance(vertība);
    }

    protected Compliance(boolean vertība) {
        this.vertība = vertība;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof Compliance) {
            return Optional.of(SALĪDYINĀTĀJS.compareTo(vertība, ((Compliance) arg).vertība));
        }
        throw new IllegalArgumentException(arg.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Compliance kombinē(Rating... papilduNovērtējums) {
        if (papilduNovērtējums[0] instanceof Compliance) {
            return atbilstība(vertība && ((Compliance) papilduNovērtējums[0]).vertība);
        }
        throw new IllegalArgumentException(asList(papilduNovērtējums).toString());
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Compliance) {
            return this.vertība == ((Compliance) arg).vertība;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R _clone() {
        return (R) new Compliance(vertība);
    }

    @Override
    public Element toDom() {
        final var dom = element(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + vertība));
        return dom;
    }
}
