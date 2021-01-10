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
    private static final Comparator<Boolean> COMPARATOR = comparator_((a, b) -> Boolean.compare(a, b));
    private boolean value;

    public static Compliance compliance(boolean value) {
        return new Compliance(value);
    }

    protected Compliance(boolean value) {
        this.value = value;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof Compliance) {
            return Optional.of(COMPARATOR.compareTo(value, ((Compliance) arg).value));
        }
        throw new IllegalArgumentException(arg.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Compliance kombinē(Rating... additionalRatings) {
        if (additionalRatings[0] instanceof Compliance) {
            return compliance(value && ((Compliance) additionalRatings[0]).value);
        }
        throw new IllegalArgumentException(asList(additionalRatings).toString());
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Compliance) {
            return this.value == ((Compliance) arg).value;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R _clone() {
        return (R) new Compliance(value);
    }

    @Override
    public Element toDom() {
        final var dom = element(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + value));
        return dom;
    }
}
