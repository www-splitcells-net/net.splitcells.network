package net.splitcells.gel.solution.history.meta.type;

import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Node;

public class AllocationRating implements MetaData<Rating> {

    public static AllocationRating allocationRating(Rating rating) {
        return new AllocationRating(rating);
    }

    private final Rating rating;

    private AllocationRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public Rating value() {
        return rating;
    }

    @Override
    public Node toDom() {
        final var dom = Xml.elementWithChildren(getClass().getSimpleName());
        dom.appendChild(rating.toDom());
        return dom;
    }
}
